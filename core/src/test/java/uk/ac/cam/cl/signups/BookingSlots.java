package uk.ac.cam.cl.signups;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cl.signups.api.Column;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.SheetInfo;
import uk.ac.cam.cl.signups.api.Slot;
import uk.ac.cam.cl.signups.api.beans.SlotBookingBean;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

import com.google.inject.Guice;

public class BookingSlots {
    
    private WebInterface service =
            Guice.createInjector(new DatabaseModule())
            .getInstance(WebInterface.class);
    
    private Sheet sheet;
    private Column column;
    private Slot emptySlot;
    private Slot bookedSlot;
    private String id;
    private String auth;
    
    @Before
    public void setUp() throws DuplicateNameException {
        sheet = Get.sheet();
        column = Get.column();
        emptySlot = new Slot(new Date(1806302413000L), 60000);
        bookedSlot = new Slot(new Date(2806302413000L), 789000, "ird28", "tick6");
        column.addSlot(emptySlot);
        column.addSlot(bookedSlot);
        sheet.addColumn(column);
        SheetInfo info = service.addSheet(sheet); // We assume this function is fine
        id = info.getSheetID();
        auth = info.getAuthCode();
    }
    
    @After
    public void removeDatabaseEntry() throws ItemNotFoundException, NotAllowedException {
        service.deleteSheet(id, auth); // We assume this function is fine
    }
    
    @Test /* fails at the moment: see fixme */
    public void BookEmptySlotTest() {
        try {
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "abc123", "tick789"));
                    /* FIXME: need to give user abc123 permission to book this slot! */
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column and slot should all have been found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The booking should be allowed");
        }
    }
    
    @Test
    public void BookFullSlotTest() {
        try {
            service.book(id, column.getName(), bookedSlot.getStartTime(),
                    new SlotBookingBean(null, "prv36", "tick89"));
            fail("The booking should not be allowed because the slot is already booked");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column and slot should all have been found");
        } catch (NotAllowedException e) {
            /* The booking should not be allowed because the slot is already booked */
        }
    }
    
    @Test
    public void ForceBookEmptySlotTest() {
        try {
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "prv36", "tick89", auth));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column and slot should all have been found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The booking should be allowed");
        }
    }
    
    @Test
    public void ForceBookFullSlotTest() {
        try {
            service.book(id, column.getName(), bookedSlot.getStartTime(),
                    new SlotBookingBean(null, "prv36", "tick89", auth));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column and slot should all have been found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The booking should be allowed");
        }
    }

}
