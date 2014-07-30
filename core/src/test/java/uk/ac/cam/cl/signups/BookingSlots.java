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
    
    // TODO
    
    /* Also testing adding and removing permissions */
    
    private WebInterface service =
            Guice.createInjector(new TestDatabaseModule())
            .getInstance(WebInterface.class);
    
    private Group group;
    private Sheet sheet;
    private Column column;
    private Column otherColumn;
    private Slot emptySlot;
    private Slot bookedSlot;
    private String id;
    private String sauth;
    private String gauth;
    private PermissionsBean permBean;
    
    @Before
    public void setUp() throws Exception {
        group = new Group("test-group");
        gauth = service.addGroup(group).getAuthCode();
        sheet = Get.sheet();
        column = Get.column();
        otherColumn = Get.column();
        emptySlot = new Slot(new Date(1806302413000L), 60000);
        bookedSlot = new Slot(new Date(2806302413000L), 789000, "ird28", "tick6");
        column.addSlot(emptySlot);
        column.addSlot(bookedSlot);
        otherColumn.addSlot(emptySlot);
        otherColumn.addSlot(bookedSlot);
        sheet.addColumn(column);
        sheet.addColumn(otherColumn);
        SheetInfo info = service.addSheet(sheet);
        id = info.getSheetID();
        sauth = info.getAuthCode();
        service.addSheet("test-group", new GroupSheetBean(id, gauth, sauth));
        Map<String,String> map = new HashMap<String,String>();
        map.put("tick789", column.getName());
        permBean = new PermissionsBean(map, gauth);
    }
    
    @After
    public void removeDatabaseEntry() throws ItemNotFoundException, NotAllowedException {
        service.deleteSheet(id, sauth);
        service.removePermissions("test-group", "abc123", permBean);
        service.deleteGroup("test-group", gauth);
    }
    
    @Test
    public void bookEmptySlotTest() {
        try {
            service.addPermissions("test-group", "abc123", permBean); // allow user permission to make booking
            System.out.println("Testing booking a slot");
            System.out.println("Slots before:");
            System.out.println(service.listSlots(id, column.getName()));
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "abc123", "tick789"));
            System.out.println("Slots after (empty slot should be booked by abc123 for tick789):");
            System.out.println(service.listSlots(id, column.getName()));
            System.out.println();
            
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column, and slot should all have been found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The booking should be allowed");
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("Something went wrong with the test - see stack trace");
        }
    }
    
    @Test
    public void permissionDeniedTest() {
        try {
            service.removePermissions("test-group", "abc123", permBean); // makes sure no permission
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
    public void unknownUserPermissionDeniedTest() {
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
    public void wrongColumnPermissionDeniedTest() {
        try {
            service.addPermissions("test-group", "abc123", permBean); // allow "column" booking only
            service.book(id, otherColumn.getName(), emptySlot.getStartTime(), // wrong column, permission denied
                    new SlotBookingBean(null, "abc123", "tick789"));
            fail("The user should not have permission to make the booking");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column, and slot should all have been found");
        } catch (NotAllowedException e) {
            /* The booking should not be allowed */
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("Something went wrong with the test - see stack trace");
        }
    }
    
    @Test
    public void anyColumnPermissionTest() {
        Map<String,String> map = new HashMap<String,String>();
        map.put("tick789", null); // allow any column
        try {
            service.addPermissions("test-group", "abc123", new PermissionsBean(map, gauth));
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "abc123", "tick789"));
            service.book(id, otherColumn.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "abc123", "tick789"));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet, column, and slot should all have been found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The booking should be allowed");
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("Something went wrong with the test - see stack trace");
        }
    }
    
    @Test
    public void bookFullSlotTest() {
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
    public void forceBookEmptySlotTest() {
        try {
            System.out.println("Testing admin slot booking");
            System.out.println("Slots before: ");
            System.out.println(service.listSlots(id, column.getName()));
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "user who wants to book empty slot", "tick they want to do", sauth));
            System.out.println("Slots after (empty slot should now be booked): ");
            System.out.println(service.listSlots(id, column.getName()));
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
    public void forceBookFullSlotTest() {
        try {
            System.out.println("Testing admin slot overwriting");
            System.out.println("Slots before: ");
            System.out.println(service.listSlots(id, column.getName()));
            service.book(id, column.getName(), bookedSlot.getStartTime(),
                    new SlotBookingBean(null, "user who wants to book full slot", "tick they want to do", sauth));
            System.out.println("Slots after (slot booked by ird28 slot should now be rebooked): ");
            System.out.println(service.listSlots(id, column.getName()));
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
