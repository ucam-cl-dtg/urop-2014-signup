package uk.ac.cam.cl.signups;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.beans.*;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.database.DatabaseCollection;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

public class SignupService implements WebInterface {
    
    private DatabaseCollection<Sheet> sheets;
    private DatabaseCollection<User> users;
    private DatabaseCollection<Group> groups;
    
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
            throws ItemNotFoundException, NotAllowedException {
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
        return sheets.getItem(sheetID).getColumn(columnName).getSlots();
    }

    public void addSlot(String sheetID, String columnName, SlotBean bean)
            throws ItemNotFoundException, NotAllowedException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(bean.getAuthCode())) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        sheet.getColumn(columnName).addSlot(bean.getSlot());
        sheets.updateItem(sheet);
    }

    public void deleteSlot(String sheetID, String columnName, Date startTime,
            String authCode) throws ItemNotFoundException, NotAllowedException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(authCode)) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        sheet.getColumn(columnName).removeSlot(startTime);
        sheets.updateItem(sheet);
    }

    public BookingInfo showBooking(String sheetID, String columnName, Date startTime)
            throws ItemNotFoundException {
        return new BookingInfo(
                sheets.getItem(sheetID)
                .getColumn(columnName)
                .getSlot(startTime));
    }

    public void book(String sheetID, String columnName,
            Date startTime, SlotBookingBean bookingBean)
            throws ItemNotFoundException, NotAllowedException {
        Sheet sheet = sheets.getItem(sheetID);
        Slot slot = sheet.getColumn(columnName).getSlot(startTime);
        if (bookingBean.authCodeProvided()) /* an admin request */ {
            if (sheet.isAuthCode(bookingBean.getAuthCode())) {
                slot.book(bookingBean.getUserToBook(), bookingBean.getComment());
            } else {
                throw new NotAllowedException("Incorrect authorisation code");
            }
        }
        else /* a non-admin request */{
            if (bookingBean.getUserToBook() != null) /* a book request */ {
                if (slot.isBooked()) {
                    throw new NotAllowedException("The slot has already been booked by someone");
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
            }
            else /* an unbook request */ {
                if (slot.getBookedUser() != bookingBean.getCurrentlyBookedUser()) {
                    throw new NotAllowedException("The user given was not booked to this slot");
                }
                if (slot.getStartTime().before(new Date())) {
                    throw new NotAllowedException("The start time of the slot has passed");
                }
                slot.unbook();
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

    public void deleteGroup(String groupName, String groupAuthCode)
            throws ItemNotFoundException, NotAllowedException {
        Group group = groups.getItem(groupName);
        if (!group.isGroupAuthCode(groupAuthCode)) {
            throw new NotAllowedException("Incorrect group authorisation code");
        }
        groups.removeItem(groupName);
        //TODO: remove group from all sheets' lists of groups
    }

    public Map<String, String> getPermissions(String groupName, String user)
            throws ItemNotFoundException {
        return users.getItem(user).getCommentColumnMap(groupName);
    }

    public void addPermissions(String groupName, String user, PermissionsBean bean)
            throws NotAllowedException, ItemNotFoundException, DuplicateNameException {
        if (!groups.getItem(groupName).isGroupAuthCode(bean.getGroupAuthCode())) {
            throw new NotAllowedException("Incorrect group authorisation code");
        }
        User userObj;
        try {
            userObj = users.getItem(user);
            userObj.getCommentColumnMap(groupName).putAll(bean.getCommentColumnMap());
            users.updateItem(userObj);
        } catch (ItemNotFoundException e) { // user not in database - add user to database
            userObj = new User(user);
            userObj.getCommentColumnMap(groupName).putAll(bean.getCommentColumnMap());
            users.insertItem(userObj);
        }
    }

    public void removePermissions(String groupName, String user,
            PermissionsBean bean) throws NotAllowedException, ItemNotFoundException {
        if (!groups.getItem(groupName).isGroupAuthCode(bean.getGroupAuthCode())) {
            throw new NotAllowedException("Incorrect group authorisation code");
        }
        User userObj = users.getItem(user);
        Map<String, String> userMap = userObj.getCommentColumnMap(groupName);
        for (String comment : bean.getCommentColumnMap().keySet()) {
            userMap.remove(comment);
        }
        users.updateItem(userObj);
    }

    public void addSheet(String groupName, GroupSheetBean bean)
            throws ItemNotFoundException, NotAllowedException {
        Group group = groups.getItem(groupName);
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

    public List<String> listSheetIDs(String groupName) throws ItemNotFoundException {
        List<String> toReturn = (List<String>) new LinkedList<String>();
        for (Sheet sheet : listSheets()) {
            if (sheet.isPartOfGroup(groupName)) {
                toReturn.add(sheet.getName());
            }
        }
        return toReturn;
    }

    public void removeSheetFromGroup(String groupName, String sheetID,
            String groupAuthCode) throws ItemNotFoundException, NotAllowedException {
        Group group = groups.getItem(groupName);
        if (!group.isGroupAuthCode(groupAuthCode)) {
            throw new NotAllowedException("Incorrect group authorisation code");
        }
        Sheet sheet = sheets.getItem(sheetID);
        sheet.removeGroup(groupName);
        sheets.updateItem(sheet);
    }
    
    /**
     * Returns true iff there exists a group in the sheet's list of groups such that
     * comment maps to either null or columnName in the entry in the user's map
     * corresponding to that group.
     */
    private boolean userHasPermission(User user, Sheet sheet, String comment, String columnName) {
        for (Group group : sheet.getGroups()) {
            boolean commentFound = user.getCommentColumnMap(group.getName()).containsKey(comment);
            String allowedColumn = user.getCommentColumnMap(group.getName()).get(comment);
            boolean columnAllowed = allowedColumn == null || allowedColumn.equals(columnName);
            if (commentFound && columnAllowed) {
                return true;
            }
        }
        return false;
    }

}
