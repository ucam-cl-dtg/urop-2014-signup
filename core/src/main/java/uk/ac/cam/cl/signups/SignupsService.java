package uk.ac.cam.cl.signups;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

import uk.ac.cam.cl.signups.api.Column;
import uk.ac.cam.cl.signups.api.DuplicateNameException;
import uk.ac.cam.cl.signups.api.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.NotAllowedException;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.SheetInfo;
import uk.ac.cam.cl.signups.api.Slot;
import uk.ac.cam.cl.signups.api.SlotBookingBean;
import uk.ac.cam.cl.signups.database.DatabaseCollection;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

public class SignupsService implements WebInterface {
    
    @Inject private static DatabaseCollection<Sheet> sheets;
    @Inject private static DatabaseCollection<User> users;

    public SheetInfo addSheet(Sheet sheet) throws DuplicateNameException {
        sheets.insertItem(sheet);
        return new SheetInfo(sheet);
    }

    public void addColumn(String sheetID, String authCode, Column column)
            throws ItemNotFoundException, NotAllowedException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(authCode)) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        sheet.addColumn(column);
    }

    public void addSlot(String sheetID, String authCode,
            String columnName, Slot slot)
                    throws ItemNotFoundException, NotAllowedException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(authCode)) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        sheet.getColumn(columnName).addSlot(slot);
    }

    public void deleteColumn(String sheetID, String authCode, String columnName)
            throws NotAllowedException, ItemNotFoundException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(authCode)) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        sheet.removeColumn(columnName);
    }

    public void deleteSlot(String sheetID, String authCode, String columnName,
            Date startTime) throws ItemNotFoundException, NotAllowedException {
        Sheet sheet = sheets.getItem(sheetID);
        if (!sheet.isAuthCode(authCode)) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        sheet.getColumn(columnName).removeSlot(startTime);
    }

    public void bookSlot(SlotBookingBean details) throws NotAllowedException, ItemNotFoundException {
        /* TODO: check user has permission to book the slot */
        Slot slot = sheets.getItem(details.getSheetID())
        .getColumn(details.getColumnName())
        .getSlot(details.getStartTime());
        if (slot.isBooked()) {
            throw new NotAllowedException("The slot has already been booked by someone");
        }
        if (slot.getStartTime().before(new Date())) {
            throw new NotAllowedException("The start time of the slot has passed");
        }
        slot.book(details.getUser(), details.getComment());
    }

    public void assignSlot(SlotBookingBean details, String authCode)
            throws ItemNotFoundException, NotAllowedException {
        Sheet sheet = sheets.getItem(details.getSheetID());
        if (!sheet.isAuthCode(authCode)) {
            throw new NotAllowedException("Incorrect authorisation code");
        }
        sheet.getColumn(details.getColumnName())
        .getSlot(details.getStartTime())
        .book(details.getUser(), details.getComment());
    }

    public void unbookSlot(SlotBookingBean details) throws NotAllowedException, ItemNotFoundException {
        /* TODO: check user has permission to unbook the slot */
        Slot slot = sheets.getItem(details.getSheetID())
                .getColumn(details.getColumnName())
                .getSlot(details.getStartTime());
        if (slot.getBookedUser() != details.getUser()) {
            throw new NotAllowedException("The user given was not booked to this slot");
        }
        if (slot.getStartTime().before(new Date())) {
            throw new NotAllowedException("The start time of the slot has passed");
        }
        slot.unbook();
    }

    public void unassignSlot(SlotBookingBean details, String authCode) {
        // TODO Auto-generated method stub

    }

    public List<Slot> listSlots(String sheet, String column) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> listColumns(String sheet) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addToWhitelist(String CRSID, String groupID,
            Map<String, String> commentColumnMap, String authCode) {
        // TODO Auto-generated method stub

    }

    public void removeFromWhitelist(String CRSID, String groupID,
            Collection<String> comments, String authCode) {
        // TODO Auto-generated method stub

    }

}
