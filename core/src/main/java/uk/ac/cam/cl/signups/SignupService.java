package uk.ac.cam.cl.signups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.inject.Guice;
import com.google.inject.Inject;

import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.beans.*;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.database.DatabaseCollection;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.database.MongoSlots;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

public class SignupService implements WebInterface {
    
    private DatabaseCollection<Sheet> sheets;
    private DatabaseCollection<User> users;
    private DatabaseCollection<Group> groups;
    private MongoSlots slots = new MongoSlots();
    
    {
        Guice.createInjector(new DatabaseModule()).injectMembers(this);
    }
    
    @Inject
    public void setSheets(DatabaseCollection<Sheet> sheets) {
        this.sheets = sheets;
    }

    @Inject
    public void setUsers(DatabaseCollection<User> users) {
        this.users = users;
    }

    @Inject
    public void setGroups(DatabaseCollection<Group> groups) {
        this.groups = groups;
    }

    public SheetInfo addSheet(Sheet sheet) throws DuplicateNameException {
        sheets.insertItem(sheet);
        return new SheetInfo(sheet);
    }

    public List<Sheet> listSheets() {
        return sheets.listItems();
    }

    public void deleteSheet(String sheetID, String authCode)
            throws ItemNotFoundException, NotAllowedException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(authCode)) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        sheets.removeItem(sheetID);
    }

    public List<Column> listColumns(String sheetID)
            throws ItemNotFoundException {
        return sheets.getItem(sheetID).getColumns();
    }

    public void addColumn(String sheetID, ColumnBean bean)
            throws ItemNotFoundException, NotAllowedException, DuplicateNameException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(bean.getAuthCode())) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        sheet.addColumn(bean.getColumn());
        sheets.updateItem(sheet);
    }

    public void deleteColumn(String sheetID, String columnName, String authCode)
            throws NotAllowedException, ItemNotFoundException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(authCode)) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        sheet.removeColumn(columnName);
        sheets.updateItem(sheet);
    }

    public List<Slot> listSlots(String sheetID, String columnName)
            throws ItemNotFoundException {
        sheets.getItem(sheetID).getColumn(columnName); // checks column exists
        return slots.listByColumn(sheetID, columnName);
    }
    
    public List<Date> listAllFreeStartTimes(String sheetID) throws ItemNotFoundException {
        List<Date> toReturn = new ArrayList<Date>();
        Sheet sheet = sheets.getItem(sheetID);
        for (Column col : sheet.getColumns()) {
            for (Slot slot : listSlots(sheetID, col.getName())) {
                if (!slot.isBooked() && !toReturn.contains(slot.getStartTime())) {
                    toReturn.add(slot.getStartTime());
                }
            }
        }
        Collections.sort(toReturn);
        return toReturn;
    }
    
    public List<String> listColumnsWithFreeSlotsAt(String sheetID, Date startTime)
            throws ItemNotFoundException {
        Sheet sheet = sheets.getItem(sheetID);
        List<String> toReturn = new ArrayList<String>();
        for (Column col : sheet.getColumns()) {
            for (Slot slot : listSlots(sheetID, col.getName())) {
                if (!slot.isBooked() && slot.getStartTime().equals(startTime)) {
                    toReturn.add(col.getName());
                    break;
                }
                if (slot.getStartTime().after(startTime)) {
                    break;
                }
            }
        }
        return toReturn;
    }

    public void addSlot(String sheetID, String columnName, SlotBean bean)
            throws ItemNotFoundException, NotAllowedException, DuplicateNameException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(bean.getAuthCode())) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        slots.insertSlot(bean.getSlot());
        sheet.getColumn(columnName).addSlot(bean.getSlot().getID());
        sheets.updateItem(sheet);
    }

    public void deleteSlot(String sheetID, String columnName, Date startTime,
            String authCode) throws ItemNotFoundException, NotAllowedException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(authCode)) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        String IDofDeletedSlot = slots.removeByTime(sheetID, columnName, startTime);
        sheet.getColumn(columnName).removeSlot(IDofDeletedSlot);
        sheets.updateItem(sheet);
    }

    public BookingInfo showBooking(String sheetID, String columnName, Date startTime)
            throws ItemNotFoundException {
        return new BookingInfo(slots.getSlot(sheetID, columnName, startTime));
    }

    public void book(String sheetID, String columnName,
            Date startTime, SlotBookingBean bookingBean)
            throws ItemNotFoundException, NotAllowedException {
        Sheet sheet = sheets.getItem(sheetID);
        Slot slot = slots.getSlot(sheetID, columnName, startTime);
        if (bookingBean.authCodeProvided()) /* an admin request */ {
            if (sheet.isAuthCode(bookingBean.getAuthCode())) {
                slot.book(bookingBean.getUserToBook(), bookingBean.getComment());
            } else {
                throw new NotAllowedException("Incorrect authorisation code");
            }
        } else /* a non-admin request */{
            if (bookingBean.getUserToBook() != null) /* a book request */ {
                if (slot.isBooked()) {
                    throw new NotAllowedException("The slot has already been booked by "
                            + slot.getBookedUser());
                }
                if (slot.getStartTime().before(new Date())) {
                    throw new NotAllowedException("The start time of the slot has passed");
                }
                try {
                    if (!userHasPermission(users.getItem(bookingBean.getUserToBook()), sheet,
                            bookingBean.getComment(), columnName)) {
                        throw new NotAllowedException("The user does not have permission to make "
                                + "a booking using this comment on this column");
                        /* 
                         * TODO: we can give more information about exactly what is not allowed -
                         * we can say if the comment is not allowed at all or whether a different
                         * column must be used.
                         */
                    }
                } catch (ItemNotFoundException e) {
                    throw new NotAllowedException("The user was not found on the database and "
                            + "so was assumed to not have permission to make the booking");
                }
                slot.book(bookingBean.getUserToBook(), bookingBean.getComment());
            } else /* an unbook request */ {
                if (bookingBean.getCurrentlyBookedUser() == null) {
                    throw new NotAllowedException("You think the slot is unbooked already - "
                            + "why are you trying to unbook it?");
                }
                if (slot.getBookedUser() == null) {
                    throw new NotAllowedException("The slot is already unbooked");
                }
                if (!slot.getBookedUser().equals(bookingBean.getCurrentlyBookedUser())) {
                    throw new NotAllowedException("The user given was not booked to this slot");
                }
                if (slot.getStartTime().before(new Date())) {
                    throw new NotAllowedException("The start time of the slot has passed");
                }
                slot.unbook();
            }
        }
        slots.updateSlot(slot);
        sheets.updateItem(sheet);
    }
    
    public void removeAllUserBookings(String sheetID, String user, String authCode)
            throws NotAllowedException, ItemNotFoundException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(authCode)) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        for (Column col : sheet.getColumns()) {
            for (Slot slot : slots.listByColumn(sheetID, col.getName())) {
                if (slot.isBooked() && slot.getBookedUser().equals(user) && slot.getStartTime().after(new Date())) {
                    slot.unbook();
                    slots.updateSlot(slot);
                }
            }
        }
        sheets.updateItem(sheet);
    }

    public GroupInfo addGroup(Group group) throws DuplicateNameException {
        groups.insertItem(group);
        return new GroupInfo(group);
    }

    public List<Group> listGroups() {
        return groups.listItems();
    }

    public void deleteGroup(String groupID, String groupAuthCode)
            throws ItemNotFoundException, NotAllowedException {
        Group group = groups.getItem(groupID);
        if (!group.isGroupAuthCode(groupAuthCode)) {
            throw new NotAllowedException("Incorrect group authorisation code");
        }
        groups.removeItem(groupID);
        for (Sheet sheet : sheets.listItems()) {
            if (sheet.isPartOfGroup(groupID)) {
                sheet.removeGroup(groupID);
            }
        }
        for (User user : users.listItems()) {
            user.getGroupToCommentToColumnMap().remove(groupID);
        }
    }

    public Map<String, String> getPermissions(String groupID, String user) {
        try {
            return users.getItem(user).getCommentColumnMap(groupID);
        } catch (ItemNotFoundException e) {
            return new HashMap<String, String>();
        }
    }

    public void addPermissions(String groupID, String user, PermissionsBean bean)
            throws NotAllowedException, ItemNotFoundException {
        if (!groups.getItem(groupID).isGroupAuthCode(bean.getGroupAuthCode())) {
            throw new NotAllowedException("Incorrect group authorisation code");
        }
        User userObj;
        try {
            userObj = users.getItem(user);
            userObj.getCommentColumnMap(groupID).putAll(bean.getCommentColumnMap());
            users.updateItem(userObj);
        } catch (ItemNotFoundException e) { // user not in database - add user to database
            userObj = new User(user);
            userObj.getCommentColumnMap(groupID).putAll(bean.getCommentColumnMap());
            try {
                users.insertItem(userObj);
            } catch (DuplicateNameException dne) {
                System.err.println("This should never happen - the reason we're adding"
                        + "the user to the database is that they weren't found in it!");
                e.printStackTrace();
            }
        }
    }

    public void removePermissions(String groupID, String user,
            PermissionsBean bean) throws NotAllowedException, ItemNotFoundException {
        if (!groups.getItem(groupID).isGroupAuthCode(bean.getGroupAuthCode())) {
            throw new NotAllowedException("Incorrect group authorisation code");
        }
        User userObj = users.getItem(user);
        Map<String, String> userMap = userObj.getCommentColumnMap(groupID);
        for (String comment : bean.getCommentColumnMap().keySet()) {
            userMap.remove(comment);
        }
        users.updateItem(userObj);
    }

    public void addSheet(String groupID, GroupSheetBean bean)
            throws ItemNotFoundException, NotAllowedException {
        Group group = groups.getItem(groupID);
        if (!group.isGroupAuthCode(bean.getGroupAuthCode())) {
            throw new NotAllowedException("Incorrect group authorisation code");
        }
        Sheet sheet = sheets.getItem(bean.getSheetID());
        if (!sheet.isAuthCode(bean.getSheetAuthCode())) {
            throw new NotAllowedException("Incorrect sheet authorisation code");
        }
        sheet.addGroup(group);
        sheets.updateItem(sheet);
    }

    public List<String> listSheetIDs(String groupID) throws ItemNotFoundException {
        List<String> toReturn = (List<String>) new LinkedList<String>();
        for (Sheet sheet : listSheets()) {
            if (sheet.isPartOfGroup(groupID)) {
                toReturn.add(sheet.getID());
            }
        }
        return toReturn;
    }

    public void removeSheetFromGroup(String groupID, String sheetID,
            String groupAuthCode) throws ItemNotFoundException, NotAllowedException {
        Group group = groups.getItem(groupID);
        if (!group.isGroupAuthCode(groupAuthCode)) {
            throw new NotAllowedException("Incorrect group authorisation code");
        }
        Sheet sheet = sheets.getItem(sheetID);
        sheet.removeGroup(groupID);
        sheets.updateItem(sheet);
    }
    
    public boolean columnIsFullyBooked(String sheetID, String columnName) {
        for (Slot slot : slots.listByColumn(sheetID, columnName)) {
            if (!slot.isBooked()) {
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
            boolean commentFound = user.getCommentColumnMap(group.getID()).containsKey(comment);
            String allowedColumn = user.getCommentColumnMap(group.getID()).get(comment);
            boolean columnAllowed = allowedColumn == null || allowedColumn.equals(columnName);
            boolean allowedColumnIsFullyBooked = columnIsFullyBooked(sheet.getID(), columnName);
            if (commentFound && (columnAllowed || allowedColumnIsFullyBooked)) {
                return true;
            }
        }
        return false;
    }

}
