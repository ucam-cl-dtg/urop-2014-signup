package uk.ac.cam.cl.signups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;

import uk.ac.cam.cl.dtg.teaching.exceptions.RemoteFailureHandler;
import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.beans.*;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.database.DatabaseCollection;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.database.MongoSlots;
import uk.ac.cam.cl.signups.interfaces.SignupsWebInterface;

public class SignupService implements SignupsWebInterface {
    
    /* For logging */
    private static final Logger log = LoggerFactory.getLogger(SignupService.class);
    
    private static DatabaseCollection<Sheet> sheets;
    private static DatabaseCollection<User> users;
    private static DatabaseCollection<Group> groups;
    private static MongoSlots slots = new MongoSlots();
    
    {
        Guice.createInjector(new DatabaseModule()).injectMembers(this);
    }
    
    @Inject
    public void setSheets(DatabaseCollection<Sheet> newSheets) {
        sheets = newSheets;
    }

    @Inject
    public void setUsers(DatabaseCollection<User> newUsers) {
        users = newUsers;
    }

    @Inject
    public void setGroups(DatabaseCollection<Group> newGroups) {
        groups = newGroups;
    }

    public SheetInfo addSheet(Sheet sheet) throws DuplicateNameException {
        log.info("Adding sheet with id=" + sheet.get_id() + ", title=" + sheet.getTitle()
                + ", and groups=" + sheet.getGroups());
        sheets.insertItem(sheet);
        log.info("Sheet added");
        return new SheetInfo(sheet);
    }

    public List<Sheet> listSheets() {
        List<Sheet> toReturn = new ArrayList<Sheet>();
        for (Sheet s : sheets.listItems()) {
            try {
                /* Recompute start times, end times, slot lengths; add to list */
                toReturn.add(computeStartAndEndTimesAndSlotLength(s));
                /* Clearly if there are lots of sheets, this will take a long time. */
            } catch (ItemNotFoundException e) {
                log.error("Something that should definitely be in the " +
                        "database was not found", e);
            }
        }
        Collections.sort(toReturn);
        return toReturn;
    }

    public void deleteSheet(String sheetID, String authCode)
            throws ItemNotFoundException, NotAllowedException {
        log.info("Attempting deletion of sheet with id=" + sheetID);
        Sheet sheet = sheets.getItem(sheetID);
        /* Check auth code */
        if (!sheet.isAuthCode(authCode)) {
            log.warn("Incorrect authorisation code: " + authCode);
            throw new NotAllowedException("Incorrect authorisation code:" + authCode);
        }
        /* Delete the sheet's slots */
        for (Column col : sheet.getColumns()) {
            for (String slotID : col.getSlotIDs()) {
                slots.removeSlot(slotID);
            }
        }
        /* Delete the sheet */
        sheets.removeItem(sheetID);
        log.info("Sheet deleted.");
    }
    
    public Sheet getSheet(String sheetID, String authCode)
            throws ItemNotFoundException, NotAllowedException {
         Sheet sheet = sheets.getItem(sheetID);
         if (!sheet.isAuthCode(authCode)) {
             log.warn("Incorrect authorisation code: " + authCode);
             throw new NotAllowedException("Incorrect authorisation code:" + authCode);
         }
         return sheet;
    }
    
    public void updateSheetInfo(String sheetID, UpdateSheetBean bean)
            throws NotAllowedException, ItemNotFoundException {
        log.info("Attempting edit of sheet of ID " + sheetID);
        synchronized (sheets) {
            Sheet sheet = sheets.getItem(sheetID);
            if (sheet.isAuthCode(bean.getAuthCode())) {
                log.warn("Incorrect authorisation code: " + bean.getAuthCode());
                throw new NotAllowedException("Incorrect authorisation code: "
                        + bean.getAuthCode());
            }
            sheet.setDescription(bean.getDescription());
            sheet.setLocation(bean.getLocation());
            sheet.setTitle(bean.getTitle());
            sheets.updateItem(sheet);
        }
        log.info("Sheet of ID " + sheetID + " updated");
    }

    public List<Column> listColumns(String sheetID)
            throws ItemNotFoundException {
        return sheets.getItem(sheetID).getColumns();
    }

    public void addColumn(String sheetID, ColumnBean bean)
            throws ItemNotFoundException, NotAllowedException, DuplicateNameException {
        log.info("Adding new column of name " + bean.getColumn().getName() + " to "
                + "sheet of ID " + sheetID);
        synchronized (sheets) {
            Sheet sheet = sheets.getItem(sheetID);
            if (!sheet.isAuthCode(bean.getAuthCode())) {
                log.warn("Incorrect authorisation code: " + bean.getAuthCode());
                throw new NotAllowedException("Incorrect authorisation code:"
                        + bean.getAuthCode());
            }
            sheet.addColumn(bean.getColumn());
            sheets.updateItem(sheet);
            log.info("Column added");
        }
    }
    
    public void createColumn(String sheetID, CreateColumnBean bean)
            throws ItemNotFoundException, NotAllowedException, DuplicateNameException {
        log.info("Creating new column of name " + bean.getColumnName() + " and adding it "
                + "to sheet of ID " + sheetID);
        Column column = new Column(bean.getColumnName(), new LinkedList<String>());
        /* Create and add new empty column to sheet */
        addColumn(sheetID, new ColumnBean(column, bean.getAuthCode()));
        for (long slotStart = bean.getStartTime().getTime();
                slotStart < bean.getEndTime().getTime();
                slotStart += bean.getSlotLengthInMinutes()*60000L) {
            /* Populate this column with slots as requested */
            addSlot(sheetID, bean.getColumnName(),
                    new SlotBean(new Slot(sheetID, bean.getColumnName(),
                            new Date(slotStart), bean.getSlotLengthInMinutes()*60000L), bean.getAuthCode()));
        }
    }

    public void deleteColumn(String sheetID, String columnName, String authCode)
            throws NotAllowedException, ItemNotFoundException {
        log.info("Deleting column of name " + columnName + " from sheet of ID "
                + sheetID);
        synchronized (sheets) {
            Sheet sheet = sheets.getItem(sheetID);
            if (!sheet.isAuthCode(authCode)) {
                log.warn("Incorrect authorisation code: " + authCode);
                throw new NotAllowedException("Incorrect authorisation code: "
                        + authCode);
            }
            for (String slotID : sheet.getColumn(columnName).getSlotIDs()) {
                slots.removeSlot(slotID);
            }
            sheet.removeColumn(columnName);
            sheets.updateItem(sheet);
        }
        log.info("Column deleted");
    }

    public List<Slot> listColumnSlots(String sheetID, String columnName)
            throws ItemNotFoundException {
        sheets.getItem(sheetID).getColumn(columnName); // checks column exists
        List<Slot> toReturn = slots.listByColumn(sheetID, columnName);
        Collections.sort(toReturn);
        return toReturn;
    }
    
    public List<Slot> listUserSlots(String user) {
        List<Slot> toReturn = slots.listByUser(user);
        Collections.sort(toReturn);
        return toReturn;
    }
    
    public List<Date> listAllFreeStartTimes(String user, String comment, String groupID, String sheetID)
            throws ItemNotFoundException {
        List<Date> toReturn = new LinkedList<Date>();
        Sheet sheet = sheets.getItem(sheetID);
        User userObj = users.getItem(user);
        Date now = new Date();
        for (Column col : sheet.getColumns()) {
            if (userHasPermission(userObj, sheet, comment, col.getName())) {
                /* For each column the user has permission to sign up with */
                for (Slot slot : listColumnSlots(sheetID, col.getName())) {
                    if (slot.getStartTime().after(now) && !slot.isBooked() && !toReturn.contains(slot.getStartTime())) {
                        /* Add all unbooked future slot times */
                        toReturn.add(slot.getStartTime());
                    }
                }
            }
        }
        Collections.sort(toReturn);
        return toReturn;
    }
    
    public List<String> listColumnsWithFreeSlotsAt(String sheetID, Long startTimeLong)
            throws ItemNotFoundException {
        Date startTime = new Date(startTimeLong);
        Sheet sheet = sheets.getItem(sheetID);
        List<String> toReturn = new ArrayList<String>();
        for (Column col : sheet.getColumns()) {
            /* For each column */
            for (Slot slot : listColumnSlots(sheetID, col.getName())) {
                /* Loop through the slots (in order) */
                if (!slot.isBooked() && slot.getStartTime().equals(startTime)) {
                    /* Add the slot with the matching start time, if it exists */
                    toReturn.add(col.getName());
                    break; // no point checking this column any further
                }
                if (slot.getStartTime().after(startTime)) {
                    break; // no point checking this column any further - slots are ordered
                }
            }
        }
        return toReturn;
    }

    public void addSlot(String sheetID, String columnName, SlotBean bean)
            throws ItemNotFoundException, NotAllowedException, DuplicateNameException {
        log.info("Adding slot of start time " + bean.getSlot().getStartTime() + " which is " +
                (bean.getSlot().isBooked() ? 
                        "booked by user " + bean.getSlot().getBookedUser() +
                        " with comment " + bean.getSlot().getComment()
                        :"unbooked") +
                        " to column " + columnName + " in sheet of ID " + sheetID);
        synchronized (sheets) {
            Sheet sheet = sheets.getItem(sheetID);
            if (!sheet.isAuthCode(bean.getAuthCode())) {
                log.warn("Incorrect authorisation code: " + bean.getAuthCode());
                throw new NotAllowedException("Incorrect authorisation code: "
                        + bean.getAuthCode());
            }
            /* Add slot to database */
            slots.insertSlot(bean.getSlot());
            /* Add slot to sheet */
            sheet.getColumn(columnName).addSlot(bean.getSlot().get_id());
            /* Update sheet in database */
            sheets.updateItem(sheet);
            log.info("Slot added");
        }
    }
    
    public void createSlotsForAllColumns(String sheetID, BatchCreateBean bean)
            throws ItemNotFoundException, NotAllowedException, DuplicateNameException {
        log.info("Adding slots to all columns from " + bean.getStartTime() + " to " + 
                bean.getEndTime() + ", with a slot length of " + bean.getSlotLengthInMinutes() + 
                " milliseconds.");
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(bean.getAuthCode())) {
            log.warn("Incorrect authorisation code: " + bean.getAuthCode());
            throw new NotAllowedException("Incorrect authorisation code: " + bean.getAuthCode());
        }
        long slotLengthInMilliseconds = bean.getSlotLengthInMinutes()*60000;
        for (Column c : sheet.getColumns()) {
            for (long slotTime = bean.getStartTime(); slotTime < bean.getEndTime();
                    slotTime += slotLengthInMilliseconds) {
                addSlot(sheetID, c.getName(), new SlotBean(
                        new Slot(sheetID, c.getName(), new Date(slotTime), slotLengthInMilliseconds), bean.getAuthCode()));
            }
        }
    }

    public void deleteSlot(String sheetID, String columnName, Long startTimeLong,
            String authCode) throws ItemNotFoundException, NotAllowedException {
        Date startTime = new Date(startTimeLong);
        log.info("Deleting slot of start time " + startTime + " from column " + columnName +
                " in sheet of ID " + sheetID);
        synchronized (sheets) {
            Sheet sheet = sheets.getItem(sheetID);
            if (!sheet.isAuthCode(authCode)) {
                log.warn("Incorrect authorisation code: " + authCode);
                throw new NotAllowedException("Incorrect authorisation code: "
                        + authCode);
            }
            /* Delete from database */
            String IDofDeletedSlot = slots.removeByTime(sheetID, columnName,
                    startTime);
            /* Delete from sheet */
            sheet.getColumn(columnName).removeSlot(IDofDeletedSlot);
            /* Update sheet in database */
            sheets.updateItem(sheet);
        }
        log.info("Slot deleted");
    }
    
    public void deleteSlotsBefore(String sheetID, BatchDeleteBean bean)
            throws ItemNotFoundException, NotAllowedException {
        synchronized (sheets) {
            Sheet sheet = sheets.getItem(sheetID);
            if (!sheet.isAuthCode(bean.getAuthCode())) {
                log.warn("Incorrect authorisation code: " + bean.getAuthCode());
                throw new NotAllowedException("Incorrect authorisation code: "
                        + bean.getAuthCode());
            }
            for (Column c : sheet.getColumns()) {
                Iterator<String> it = c.getSlotIDs().iterator();
                while (it.hasNext()) {
                    /* Loop through slots of each column and remove ones before the time */
                    String slotID = it.next();
                    Slot slot = slots.getSlot(slotID);
                    if ((slot.getStartTime().before(new Date(bean.getTime())))) {
                        slots.removeSlot(slotID);
                        it.remove();
                    }
                }
            }
            sheets.updateItem(sheet);
        }
    }
    
    public void deleteSlotsAfter(String sheetID, BatchDeleteBean bean)
            throws ItemNotFoundException, NotAllowedException {
        synchronized (sheets) {
            Sheet sheet = sheets.getItem(sheetID);
            if (!sheet.isAuthCode(bean.getAuthCode())) {
                log.warn("Incorrect authorisation code: " + bean.getAuthCode());
                throw new NotAllowedException("Incorrect authorisation code: "
                        + bean.getAuthCode());
            }
            for (Column c : sheet.getColumns()) {
                Iterator<String> it = c.getSlotIDs().iterator();
                while (it.hasNext()) {
                    String slotID = it.next();
                    Slot slot = slots.getSlot(slotID);
                    if ((slot.getStartTime().after(new Date(bean.getTime())))) {
                        slots.removeSlot(slotID);
                        it.remove();
                    }
                }
            }
            sheets.updateItem(sheet);
        }
    }

    public BookingInfo showBooking(String sheetID, String columnName, Long startTimeLong)
            throws ItemNotFoundException {
        return new BookingInfo(slots.getSlot(sheetID, columnName, new Date(startTimeLong)));
    }

    public void book(String sheetID, String columnName,
            Long startTimeLong, SlotBookingBean bookingBean)
                    throws ItemNotFoundException, NotAllowedException {
        Date startTime = new Date(startTimeLong);
        Sheet sheet = sheets.getItem(sheetID);
        synchronized (slots) {
            Slot slot = slots.getSlot(sheetID, columnName, startTime);
            if (bookingBean.authCodeProvided()) /* an admin request */ {
                if (sheet.isAuthCode(bookingBean.getAuthCode())) {
                    /* If authCode is correct, make the change */
                    slot.book(bookingBean.getUserToBook(), bookingBean.getComment());
                    log.info("Successfully admin-booked slot at time " + startTime +
                            " in column " + columnName + " in sheet of ID " + sheetID +
                            " for user " + bookingBean.getUserToBook() + " with comment " + bookingBean.getComment());
                } else {
                    log.warn("Incorrect authorisation code: " + bookingBean.getAuthCode());
                    throw new NotAllowedException("Incorrect authorisation code: " + bookingBean.getAuthCode());
                }
            } else /* a non-admin request */{
                if (bookingBean.getUserToBook() != null) /* a book request */ {
                    if (slot.isBooked()) {
                        log.info("Booking slot at time " + startTime +
                                " in column " + columnName + " in sheet of ID " + sheetID +
                                " for user " + bookingBean.getUserToBook() + " with comment " + bookingBean.getComment()
                                + " failed because the slot has already been booked by " + slot.getBookedUser());
                        throw new NotAllowedException("The slot has already been booked by "
                                + slot.getBookedUser());
                    }
                    Date now = new Date();
                    if (slot.getStartTime().before(now)) {
                        log.info("Booking slot at time " + startTime +
                                " in column " + columnName + " in sheet of ID " + sheetID +
                                " for user " + bookingBean.getUserToBook() + " with comment " + bookingBean.getComment()
                                + " failed because the slot start time is in the past");
                        throw new NotAllowedException("The start time of the slot has passed");
                    }
                    try {
                        User userObj = users.getItem(bookingBean.getUserToBook());
                        if (!userHasPermission(userObj, sheet,
                                bookingBean.getComment(), columnName)) {
                            /* User doesn't have permission */
                            log.warn("Booking slot at time " + startTime +
                                    " in column " + columnName + " in sheet of ID " + sheetID +
                                    " for user " + bookingBean.getUserToBook() + " with comment " + bookingBean.getComment()
                                    + " failed because the user does not have permission to make the booking");
                            List<String> allowedColumns = new ArrayList<>();
                            /* Work out the columns they *are* allowed to use */
                            for (Group group : sheet.getGroups()) {
                                Map<String, String> map = userObj.getCommentColumnMap(group.get_id());
                                if (map.containsKey(bookingBean.getComment())) {
                                    allowedColumns.add(map.get(bookingBean.getComment()));
                                }
                            }
                            if (allowedColumns.size() == 0) {
                                throw new NotAllowedException("You do not have permission to make "
                                        + "this booking using this comment in any column");
                            } else {
                                throw new NotAllowedException("You can only make a booking with this comment "
                                        + "using the following columns: " + allowedColumns.toString());
                            }
                        }
                    } catch (ItemNotFoundException e) {
                        log.warn("Booking slot at time " + startTime +
                                " in column " + columnName + " in sheet of ID " + sheetID +
                                " for user " + bookingBean.getUserToBook() + " with comment " + bookingBean.getComment()
                                + " failed because the user was not found on the database and "
                                + "so was assumed to not have permission to make the booking");
                        throw new NotAllowedException("The user was not found on the database and "
                                + "so was assumed to not have permission to make the booking");
                    }
                    /* User has permission - make booking */
                    slot.book(bookingBean.getUserToBook(), bookingBean.getComment());
                    log.info("Successfully booked slot at time " + startTime +
                            " in column " + columnName + " in sheet of ID " + sheetID +
                            " for user " + bookingBean.getUserToBook() + " with comment " + bookingBean.getComment());
                } else /* an unbook request */ {
                    if (slot.getBookedUser() == null) {
                        log.warn("Unbooking slot at time " + startTime +
                                " in column " + columnName + " in sheet of ID " + sheetID +
                                " for user " + bookingBean.getCurrentlyBookedUser() + " with comment " + bookingBean.getComment()
                                + " failed because the slot is already unbooked");
                        throw new NotAllowedException("The slot is already unbooked");
                    }
                    if (!slot.getBookedUser().equals(bookingBean.getCurrentlyBookedUser())) {
                        log.warn("Unbooking slot at time " + startTime +
                                " in column " + columnName + " in sheet of ID " + sheetID +
                                " for user " + bookingBean.getCurrentlyBookedUser() + " with comment " + bookingBean.getComment()
                                + " failed because the user given was not booked to this slot");
                        throw new NotAllowedException("The user given was not booked to this slot");
                    }
                    Date now = new Date();
                    if (slot.getStartTime().before(now)) {
                        log.warn("Unbooking slot at time " + startTime +
                                " in column " + columnName + " in sheet of ID " + sheetID +
                                " for user " + bookingBean.getCurrentlyBookedUser() + " with comment " + bookingBean.getComment()
                                + " failed because the start time of the slot has passed");
                        log.info("The start time of the slot has passed");
                        throw new NotAllowedException("The start time of the slot has passed");
                    }
                    /* Everything fine; make unbooking */
                    slot.unbook();
                    log.info("Successfully unbooked slot at time " + startTime +
                            " in column " + columnName + " in sheet of ID " + sheetID +
                            " for user " + bookingBean.getCurrentlyBookedUser() + " with comment " + bookingBean.getComment());
                }
            }
            slots.updateSlot(slot);
        }
    }
    
    public void removeAllUserBookings(String sheetID, String user, String authCode)
            throws NotAllowedException, ItemNotFoundException {
        log.info("Attempting to remove all future booking of user " + user + " in the " +
                "sheet of ID " + sheetID);
        synchronized (sheets) {
            Sheet sheet = sheets.getItem(sheetID);
            if (!sheet.isAuthCode(authCode)) {
                log.warn("Incorrect authorisation code: " + authCode);
                throw new NotAllowedException("Incorrect authorisation code: " + authCode);
            }
            Date now = new Date();
            for (Column col : sheet.getColumns()) {
                synchronized (slots) {
                    for (Slot slot : slots.listByColumn(sheetID, col.getName())) {
                        if (slot.isBooked() && slot.getBookedUser().equals(user) && slot.getStartTime().after(now)) {
                            slot.unbook();
                            slots.updateSlot(slot);
                        }
                    }
                }
            }
            sheets.updateItem(sheet);
        }
        log.info("All future bookings successfully removed");
    }

    public String addGroup(Group group) throws DuplicateNameException {
        log.info("Adding group: " + group);
        groups.insertItem(group);
        log.info("Group added");
        return group.getGroupAuthCode();
    }

    public List<Group> listGroups() {
        return groups.listItems();
    }

    public void deleteGroup(String groupID, String groupAuthCode)
            throws ItemNotFoundException, NotAllowedException {
        log.info("Deleting group of ID " + groupID);
        Group group = groups.getItem(groupID);
        if (!group.isGroupAuthCode(groupAuthCode)) {
            throw new NotAllowedException("Incorrect group authorisation code");
        }
        groups.removeItem(groupID);
        synchronized (sheets) {
            for (Sheet sheet : sheets.listItems()) {
                if (sheet.isPartOfGroup(groupID)) {
                    sheet.removeGroup(groupID);
                    sheets.updateItem(sheet);
                }
            }
        }
        synchronized (users) {
            for (User user : users.listItems()) {
                user.getGroupToCommentToColumnMap().remove(groupID);
                users.updateItem(user);
            }
        }
        log.info("Group successfully deleted");
    }

    public Map<String, String> getPermissions(String groupID, String user) {
        try {
            return users.getItem(user).getCommentColumnMap(groupID);
        } catch (ItemNotFoundException e) {
            /* User not found in our database, so certainly has no permissions */
            return new HashMap<String, String>();
        }
    }

    public void addPermissions(String groupID, String user, AddPermissionsBean bean)
            throws NotAllowedException, ItemNotFoundException {
        log.info("Changing user " + user + "'s permissions in group of ID " + groupID);
        if (!groups.getItem(groupID).isGroupAuthCode(bean.getGroupAuthCode())) {
            throw new NotAllowedException("Incorrect group authorisation code");
        }
        User userObj;
        try {
            synchronized (users) {
                userObj = users.getItem(user);
                userObj.getCommentColumnMap(groupID).putAll(
                        bean.getCommentColumnMap());
                users.updateItem(userObj);
            }
        } catch (ItemNotFoundException e) { // user not in database - add user to database
            log.info("The user " + user + " was not found in the database - attempting to add them");
            userObj = new User(user);
            userObj.getCommentColumnMap(groupID).putAll(bean.getCommentColumnMap());
            try {
                users.insertItem(userObj);
            } catch (DuplicateNameException dne) {
                log.error("This should never happen - the reason we're adding "
                        + "the user to the database is that they weren't found in it!", e);
            }
        }
        log.info("Permissions successfully updated");
    }

    public void removePermissions(String groupID, String user,
            RemovePermissionsBean bean) throws NotAllowedException, ItemNotFoundException {
        log.info("Attempting to remove permissions from user " + user + " in group of ID " + groupID);
        /* Check auth code */
        if (!groups.getItem(groupID).isGroupAuthCode(bean.getGroupAuthCode())) {
            throw new NotAllowedException("Incorrect group authorisation code");
        }
        User userObj;
        try {
            synchronized (users) {
                userObj = users.getItem(user);
                Map<String, String> userMap = userObj
                        .getCommentColumnMap(groupID);
                for (String comment : bean.getComments()) {
                    userMap.remove(comment);
                }
                users.updateItem(userObj);
            }
        } catch (ItemNotFoundException e) { // user not in database - add user to database
            log.info("The user " + user + " was not found in the database - attempting to add them");
            userObj = new User(user);
            try {
                users.insertItem(userObj);
            } catch (DuplicateNameException dne) {
                log.error("This should never happen - the reason we're adding "
                        + "the user to the database is that they weren't found in it!", e);
            }
        }
        log.info("Permissions successfully removed");
    }

    public void addSheetToGroup(String groupID, GroupSheetBean bean)
            throws ItemNotFoundException, NotAllowedException {
        log.info("Attempting to add sheet of ID " + bean.getSheetID() + " to group " +
                "of ID " + groupID);
        Group group = groups.getItem(groupID);
        if (!group.isGroupAuthCode(bean.getGroupAuthCode())) {
            log.warn("Incorrect group authorisation code: " + bean.getGroupAuthCode());
            throw new NotAllowedException("Incorrect group authorisation code: " + bean.getGroupAuthCode());
        }
        synchronized (sheets) {
            Sheet sheet = sheets.getItem(bean.getSheetID());
            if (!sheet.isAuthCode(bean.getSheetAuthCode())) {
                log.warn("Incorrectsheet authorisation code: "
                        + bean.getSheetAuthCode());
                throw new NotAllowedException(
                        "Incorrect sheet authorisation code: "
                                + bean.getSheetAuthCode());
            }
            sheet.addGroup(group);
            sheets.updateItem(sheet);
        }
        log.info("Sheet added to group");
    }
    
    public List<String> getGroupIDs(String sheetID) throws ItemNotFoundException {
        List<String> toReturn = new LinkedList<String>();
        for (Group g : sheets.getItem(sheetID).getGroups()) {
            toReturn.add(g.get_id());
        }
        return toReturn;
    }
    
    public List<Sheet> listSheets(String groupID, String groupAuthCode)
            throws ItemNotFoundException, NotAllowedException {
        Group group = groups.getItem(groupID);
        if (!group.isGroupAuthCode(groupAuthCode)) {
            log.warn("Incorrect group authorisation code: " + groupAuthCode);
            throw new NotAllowedException("Incorrect group authorisation code: " + groupAuthCode);
        }
        List<Sheet> toReturn = new ArrayList<Sheet>();
        for (Sheet sheet : sheets.listItems()) { // TODO: optimise. create MongoSheets class,
            try {                                // get all sheets which are part of a group?
                if (sheet.isPartOfGroup(groupID)) {
                    toReturn.add(computeStartAndEndTimesAndSlotLength(sheet));
                }
            } catch (ItemNotFoundException e) {
                log.error("Something that should definitely be in the " +
                        "database was not found", e);
            }
        }
        Collections.sort(toReturn);
        return toReturn;
    }

    public void removeSheetFromGroup(String groupID, String sheetID,
            String groupAuthCode) throws ItemNotFoundException, NotAllowedException {
        log.info("Attempting to remove sheet of ID " + sheetID +
                " from group of ID " + groupID);
        Group group = groups.getItem(groupID);
        if (!group.isGroupAuthCode(groupAuthCode)) {
            log.warn("Incorrect group authorisation code: " + groupAuthCode);
            throw new NotAllowedException("Incorrect group authorisation code: " + groupAuthCode);
        }
        synchronized (sheets) {
            Sheet sheet = sheets.getItem(sheetID);
            sheet.removeGroup(groupID);
            sheets.updateItem(sheet);
        }
        log.info("Sheet removed from group");
    }
    
    public boolean columnIsFullyBooked(String sheetID, String columnName) {
        Date now = new Date();
        for (Slot slot : slots.listByColumn(sheetID, columnName)) {
            if (!slot.isBooked() && slot.getStartTime().after(now)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns true iff there exists a group in the sheet's list of groups such that
     * comment maps to either null or columnName in the entry in the user's map
     * corresponding to that group.
     */
    private boolean userHasPermission(User user, Sheet sheet, String comment, String columnName) throws ItemNotFoundException {
        for (Group group : sheet.getGroups()) {
            boolean commentFound = user.getCommentColumnMap(group.get_id()).containsKey(comment);
            String allowedColumn = user.getCommentColumnMap(group.get_id()).get(comment);
            boolean columnAllowed = allowedColumn == null || allowedColumn.equals(columnName);
            boolean allowedColumnIsFullyBooked = columnIsFullyBooked(sheet.get_id(), allowedColumn);
            if (commentFound && (columnAllowed || allowedColumnIsFullyBooked)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Computes the time of the first slot in the sheet and sets the startTime
     * field of the sheet to that time, and return the sheet.
     * @param sheet
     * @return The sheet with its start time now correct
     * @throws ItemNotFoundException
     */
    private Sheet computeStartAndEndTimesAndSlotLength(Sheet sheet) throws ItemNotFoundException {
        Date sheetStartTime = null;
        Date sheetEndTime = null;
        long slotLengthInMilliseconds = -60000;
        for (Column col : sheet.getColumns()) {
            for (String slotID : col.getSlotIDs()) {
                Slot slot = slots.getSlot(slotID);
                slotLengthInMilliseconds = slot.getDuration();
                Date slotStartTime = slot.getStartTime();
                if (sheetStartTime == null || sheetStartTime.after(slotStartTime)) {
                    sheetStartTime = slotStartTime;
                }
                Date slotEndTime = new Date(slotStartTime.getTime() + slot.getDuration());
                if (sheetEndTime == null || sheetEndTime.before(slotEndTime)) {
                    sheetEndTime = slotEndTime;
                }
            }
            break; // Hopefully will make loading sheets quicker. TODO: solve properly?
        }
        sheet.setStartTime(sheetStartTime);
        sheet.setEndTime(sheetEndTime);
        sheet.setSlotLengthInMinutes((int) slotLengthInMilliseconds/60000); 
        sheets.updateItem(sheet);
        return sheet;
    }

}
