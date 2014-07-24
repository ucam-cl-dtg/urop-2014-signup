package uk.ac.cam.cl.signups;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.beans.*;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.database.DatabaseCollection;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

public class SignupService implements WebInterface {
    
    @Inject private static DatabaseCollection<Sheet> sheets;
    @Inject private static DatabaseCollection<User> users;
    @Inject private static DatabaseCollection<Group> groups;

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
        return sheets.getItem(sheetID).listColumns();
    }

    public void addColumn(String sheetID, ColumnBean bean)
            throws ItemNotFoundException, NotAllowedException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(bean.getAuthCode())) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        sheet.addColumn(bean.getColumn());
    }

    public void deleteColumn(String sheetID, String columnName, String authCode)
            throws NotAllowedException, ItemNotFoundException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(authCode)) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        sheet.removeColumn(columnName);
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
    }

    public void deleteSlot(String sheetID, String columnName, Date startTime,
            String authCode) throws ItemNotFoundException, NotAllowedException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(authCode)) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        sheet.getColumn(columnName).removeSlot(startTime);
    }

    public BookingInfo showBooking(String sheetID, String columnName, Date startTime)
            throws ItemNotFoundException {
        return new BookingInfo(
                sheets.getItem(sheetID)
                .getColumn(columnName)
                .getSlot(startTime));
    }

    public void modifyBooking(String sheetID, String columnName,
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
    }

    public void addGroup(Group group) throws DuplicateNameException {
        groups.insertItem(group);
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
    }

    public Map<String, String> getPermissions(String groupName, String user)
            throws ItemNotFoundException {
        return users.getItem(user).getCommentColumnMap(groupName);
    }

    public void addPermissions(String groupName, String user, PermissionsBean bean)
            throws NotAllowedException, ItemNotFoundException {
         if (!groups.getItem(groupName).isGroupAuthCode(bean.getGroupAuthCode())) {
             throw new NotAllowedException("Incorrect group authorisation code");
         }
        users.getItem(user).getCommentColumnMap(groupName).putAll(bean.getCommentColumnMap());
    }

    public void removePermissions(String groupName, String user,
            PermissionsBean bean) throws NotAllowedException, ItemNotFoundException {
        if (!groups.getItem(groupName).isGroupAuthCode(bean.getGroupAuthCode())) {
            throw new NotAllowedException("Incorrect group authorisation code");
        }
        Map<String, String> userMap = users.getItem(user).getCommentColumnMap(groupName);
        for (String comment : bean.getCommentColumnMap().keySet()) {
            userMap.remove(comment);
        }
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
        group.addSheet(sheet);
        sheet.addGroup(group);
    }

    public List<String> listSheetIDs(String groupName) throws ItemNotFoundException {
        Group group = groups.getItem(groupName);
        return group.getSheetIDs();
    }

    public void removeSheetFromGroup(String groupName, String sheetID,
            String groupAuthCode) throws ItemNotFoundException, NotAllowedException {
        Group group = groups.getItem(groupName);
        if (!group.isGroupAuthCode(groupAuthCode)) {
            throw new NotAllowedException("Incorrect group authorisation code");
        }
        Sheet sheet = sheets.getItem(sheetID);
        sheet.removeGroup(group);
        group.removeSheet(sheet);
    }

}
