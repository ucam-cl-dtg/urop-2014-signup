package uk.ac.cam.cl.signups.tests;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cl.signups.Get;
import uk.ac.cam.cl.signups.ModuleProvider;
import uk.ac.cam.cl.signups.TestDatabaseModule;
import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.beans.GroupSheetBean;
import uk.ac.cam.cl.signups.api.beans.PermissionsBean;
import uk.ac.cam.cl.signups.api.beans.SlotBean;
import uk.ac.cam.cl.signups.api.beans.SlotBookingBean;
import uk.ac.cam.cl.signups.api.exceptions.*;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

import com.google.inject.Guice;

public class BookingSlots {
    
    private WebInterface service =
            Guice.createInjector(ModuleProvider.provide())
            .getInstance(WebInterface.class);
    
    private Group group;
    private Sheet sheet;
    private Column column;
    private Column otherColumn;
    private Slot emptySlot;
    private Slot bookedSlot;
    private Slot pastSlot;
    private String id;
    private String sauth;
    private String gauth;
    private PermissionsBean specificColumnPermBean;
    private PermissionsBean anyColumnPermBean;
    
    @Before
    public void setUp() throws Exception {
        group = new Group("test-group");
        gauth = service.addGroup(group);
        sheet = Get.sheet();
        column = Get.column(sheet.getID());
        otherColumn = Get.column(sheet.getID());
        emptySlot = new Slot(sheet.getID(), column.getName(),
                new Date(1806302413000L), 60000L);
        bookedSlot = new Slot(sheet.getID(), column.getName(),
                new Date(2806302413000L), 789000L, "ird28", "tick6");
        pastSlot = new Slot(sheet.getID(), column.getName(),
                new Date(new Date().getTime()-2000), 60000L);
        
        sheet.addColumn(column);
        sheet.addColumn(otherColumn);
        SheetInfo info = service.addSheet(sheet);
        id = info.getSheetID();
        sauth = info.getAuthCode();
        service.addSheet("test-group", new GroupSheetBean(id, gauth, sauth));
        
        service.addSlot(sheet.getID(), column.getName(), new SlotBean(emptySlot, sauth));
        service.addSlot(sheet.getID(), column.getName(), new SlotBean(bookedSlot, sauth));
        service.addSlot(sheet.getID(), column.getName(), new SlotBean(pastSlot, sauth));
        
        service.addSlot(sheet.getID(), otherColumn.getName(),
                new SlotBean(new Slot(sheet.getID(), otherColumn.getName(),
                        new Date(1806302413000L), 60000), sauth)); // emptySlot
        service.addSlot(sheet.getID(), otherColumn.getName(),
                new SlotBean(new Slot(sheet.getID(), otherColumn.getName(), // bookedSlot
                        new Date(2806302413000L), 789000, "ird28", "tick6"), sauth));
        
        
        Map<String,String> map = new HashMap<String,String>();
        map.put("tick789", column.getName());
        specificColumnPermBean = new PermissionsBean(map, gauth);
        map = new HashMap<String,String>();
        map.put("tick789", null);
        anyColumnPermBean = new PermissionsBean(map, gauth);
    }
    
    @After
    public void removeDatabaseEntry() throws ItemNotFoundException, NotAllowedException {
        service.deleteSheet(id, sauth);
        service.deleteGroup("test-group", gauth);
    }
    
    @Test
    public void userBookEmptySlotSpecificColumn_success() {
        try {
            service.addPermissions("test-group", "abc123", specificColumnPermBean); // allow user permission to make booking
            System.out.println("Testing booking a slot");
            System.out.println("Slots before:");
            System.out.println(service.listColumnSlots(id, column.getName()));
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "abc123", "tick789"));
            System.out.println("Slots after (empty slot should be booked by abc123 for tick789):");
            System.out.println(service.listColumnSlots(id, column.getName()));
            System.out.println();
            assertEquals("The user should have booked the slot",
                    "abc123", service.showBooking(id, column.getName(), emptySlot.getStartTime()).getUser());
            assertEquals("The user should have booked the slot with this comment",
                    "tick789", service.showBooking(id, column.getName(), emptySlot.getStartTime()).getComment());
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column, and slot should all have been found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The booking should be allowed");
        }
    }
    
    @Test
    public void userBookEmptySlot_exception_notWhitelisted() {
        try {
            service.removePermissions("test-group", "abc123", specificColumnPermBean);
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("Should be allowed");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            /* Never mind, user probably doesn't have permission then */
        }
        try {
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "abc123", "tick789"));
            fail("The user should not have permission to make the booking");
        } catch (ItemNotFoundException infe) {
            infe.printStackTrace();
            fail("Sheet, column, and slot should all have been found");
        } catch (NotAllowedException nae) {
            /* The user does not have permission to make the booking */
        }
    }
    
    @Test
    public void userBookEmptySlot_exception_unknownUser() {
        try {
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "unknown user", "some comment"));
            fail("The user is should not be in the database and so "
                    + "should not have permission to make the booking");
        } catch (ItemNotFoundException infe) {
            infe.printStackTrace();
            fail("Sheet, column, and slot should all have been found");
        } catch (NotAllowedException nae) {
            /* The user does not have permission to make the booking */
        }
    }
    
    @Test
    public void userBookEmptySlot_exception_wrongColumn() {
        try {
            service.addPermissions("test-group", "abc123", specificColumnPermBean); // allow "column" booking only
            service.book(id, otherColumn.getName(), emptySlot.getStartTime(), // wrong column, permission denied
                    new SlotBookingBean(null, "abc123", "tick789"));
            fail("The user should not have permission to make the booking");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column, and slot should all have been found");
        } catch (NotAllowedException e) {
            /* The booking should not be allowed */
        }
    }
    
    @Test
    public void userBookEmptySlot_exception_startTimePassed() {
        try {
            service.addPermissions("test-group", "abc123", anyColumnPermBean);
            service.book(id, column.getName(), pastSlot.getStartTime(),
                    new SlotBookingBean(null, "abc123", "tick789"));
            fail("The start time has passed so permission should be denied");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column, and slot should all have been found");
        } catch (NotAllowedException e) {
            /* The start time has passed so permission should be denied */
        }
    }
    
    @Test
    public void userBookEmptySlotAnyColumn_success() {
        try {
            service.addPermissions("test-group", "abc123", anyColumnPermBean);
            
            service.book(id, column.getName(), emptySlot.getStartTime(),
                   new SlotBookingBean(null, "abc123", "tick789"));
            service.book(id, otherColumn.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "abc123", "tick789"));
            
            assertEquals("The user should have booked the slot",
                    "abc123", service.showBooking(id, column.getName(), emptySlot.getStartTime()).getUser());
            assertEquals("The user should have booked the slot with this comment",
                    "tick789", service.showBooking(id, column.getName(), emptySlot.getStartTime()).getComment());
            assertEquals("The user should have booked the slot",
                    "abc123", service.showBooking(id, otherColumn.getName(), emptySlot.getStartTime()).getUser());
            assertEquals("The user should have booked the slot with this comment",
                    "tick789", service.showBooking(id, otherColumn.getName(), emptySlot.getStartTime()).getComment());
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column, and slot should all have been found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The bookings should be allowed");
        }
    }
    
    @Test
    public void userUnbookFullSlot_success() {
        try {
            System.out.println(service.showBooking(id, column.getName(), bookedSlot.getStartTime()).getUser());
            System.out.println(bookedSlot.getBookedUser());
            service.book(id, column.getName(), bookedSlot.getStartTime(),
                    new SlotBookingBean(bookedSlot.getBookedUser(), null, null));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet, column and slot should all be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The unbooking should be allowed");
        }
    }
    
    @Test
    public void userUnbookFullSlot_exception_wrongCurrentUser() {
        try {
            service.book(id, column.getName(), bookedSlot.getStartTime(),
                    new SlotBookingBean("not the currently booked user", null, null));
            fail("Permission should be denied - given user incorrect");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet, column and slot should all be found");
        } catch (NotAllowedException e) {
            /* Permission should be denied - given user incorrect */
        }
        try {
            service.book(id, column.getName(), bookedSlot.getStartTime(),
                    new SlotBookingBean(null, null, null));
            fail("Permission should be denied - given user incorrect");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet, column and slot should all be found");
        } catch (NotAllowedException e) {
            /* Permission should be denied - given user incorrect */
        }
    }
    
    @Test
    public void userUnbookFullSlot_exception_startTimePassed() {
        try {
            service.book(id, column.getName(), pastSlot.getStartTime(),
                    new SlotBookingBean(pastSlot.getBookedUser(), null, null));
            fail("Permission should be denied - the start time of the slot has passed");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet, column and slot should all be found");
        } catch (NotAllowedException e) {
            /* Permission should be denied - the start time of the slot has passed */
        }
    }
    
    @Test
    public void userBookFullSlot_exception_slotAlreadyBooked() {
        try {
            service.addPermissions("test-group", "abc123", anyColumnPermBean);
            service.book(id, column.getName(), bookedSlot.getStartTime(),
                    new SlotBookingBean(null, "abc123", "tick789"));
            fail("The booking should not be allowed because the slot is already booked");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column and slot should all have been found");
        } catch (NotAllowedException e) {
            /* The booking should not be allowed because the slot is already booked */
        }
    }
    
    @Test
    public void adminBookEmptySlot_success() {
        try {
            System.out.println("Testing admin slot booking");
            System.out.println("Slots before: ");
            System.out.println(service.listColumnSlots(id, column.getName()));
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "user who wants to book empty slot",
                            "tick they want to do", sauth));
            System.out.println("Slots after (empty slot should now be booked): ");
            System.out.println(service.listColumnSlots(id, column.getName()));
            System.out.println();
            assertEquals("The user should have been booked the to slot",
                    "user who wants to book empty slot",
                    service.showBooking(id, column.getName(), emptySlot.getStartTime()).getUser());
            assertEquals("The user should have been booked to the slot with this comment",
                    "tick they want to do",
                    service.showBooking(id, column.getName(), emptySlot.getStartTime()).getComment());
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column and slot should all have been found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The booking should be allowed");
        }
    }
    
    @Test
    public void adminBookFullSlot_success() {
        try {
            System.out.println("Testing admin slot overwriting");
            System.out.println("Slots before: ");
            System.out.println(service.listColumnSlots(id, column.getName()));
            service.book(id, column.getName(), bookedSlot.getStartTime(),
                    new SlotBookingBean(null, "user who wants to book full slot", "tick they want to do", sauth));
            System.out.println("Slots after (slot booked by ird28 slot should now be rebooked): ");
            System.out.println(service.listColumnSlots(id, column.getName()));
            System.out.println();
            assertEquals("The user should have been booked the to slot",
                    "user who wants to book full slot",
                    service.showBooking(id, column.getName(), bookedSlot.getStartTime()).getUser());
            assertEquals("The user should have been booked to the slot with this comment",
                    "tick they want to do",
                    service.showBooking(id, column.getName(), bookedSlot.getStartTime()).getComment());
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column and slot should all have been found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The booking should be allowed");
        }
    }
    
    @Test
    public void adminBook_exception_wrongAuthCode() {
        try {
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "user who wants to book empty slot",
                            "tick they want to do", "wrong auth code"));
            fail("The auth code is incorrect - permission should be denied");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet, column and slot should be found");
        } catch (NotAllowedException e) {
            /* The auth code is incorrect - permission should be denied */
        }
    }

}
