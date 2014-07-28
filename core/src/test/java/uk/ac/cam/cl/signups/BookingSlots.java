package uk.ac.cam.cl.signups;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.beans.GroupSheetBean;
import uk.ac.cam.cl.signups.api.beans.PermissionsBean;
import uk.ac.cam.cl.signups.api.beans.SlotBookingBean;
import uk.ac.cam.cl.signups.api.exceptions.*;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

import com.google.inject.Guice;

public class BookingSlots {
    
    /* Also testing adding and removing permissions */
    
    private WebInterface service =
            Guice.createInjector(new DatabaseModule())
            .getInstance(WebInterface.class);
    
    private Group group;
    private Sheet sheet;
    private Column column;
    private Slot emptySlot;
    private Slot bookedSlot;
    private String id;
    private String sauth;
    private String gauth;
    
    @Before
    public void setUp() throws Exception {
        group = new Group("test-group");
        gauth = service.addGroup(group).getAuthCode();
        sheet = Get.sheet();
        column = Get.column();
        emptySlot = new Slot(new Date(1806302413000L), 60000);
        bookedSlot = new Slot(new Date(2806302413000L), 789000, "ird28", "tick6");
        column.addSlot(emptySlot);
        column.addSlot(bookedSlot);
        sheet.addColumn(column);
        SheetInfo info = service.addSheet(sheet);
        id = info.getSheetID();
        sauth = info.getAuthCode();
        service.addSheet("test-group", new GroupSheetBean(id, gauth, sauth));
    }
    
    @After
    public void removeDatabaseEntry() throws ItemNotFoundException, NotAllowedException {
        service.deleteSheet(id, sauth);
        service.deleteGroup("test-group", gauth);
    }
    
    @Test
    public void BookEmptySlotTest() {
        try {
            Map<String,String> map = new HashMap<String,String>();
            map.put("tick789", null);
            service.addPermissions("test-group", "abc123",
                    new PermissionsBean(map, gauth));
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "abc123", "tick789"));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column, slot and user should all have been found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The booking should be allowed");
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("Something went wrong with the test - see stack trace");
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
            System.out.println("Slots before: ");
            System.out.println(service.listSlots(id, column.getName()));
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "user who wants to book empty slot", "tick they want to do", sauth));
            System.out.println("Slots after (empty slot should now be booked): ");
            System.out.println(service.listSlots(id, column.getName()));
            System.out.println();
            System.out.println();
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
            System.out.println("Slots before: ");
            System.out.println(service.listSlots(id, column.getName()));
            service.book(id, column.getName(), bookedSlot.getStartTime(),
                    new SlotBookingBean(null, "user who wants to book full slot", "tick they want to do", sauth));
            System.out.println("Slots after (slot booked by ird28 slot should now be rebooked): ");
            System.out.println(service.listSlots(id, column.getName()));
            System.out.println();
            System.out.println();
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column and slot should all have been found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The booking should be allowed");
        }
    }

}
