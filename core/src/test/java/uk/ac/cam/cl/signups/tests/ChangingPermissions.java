/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
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
import uk.ac.cam.cl.signups.api.Column;
import uk.ac.cam.cl.signups.api.Group;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.SheetInfo;
import uk.ac.cam.cl.signups.api.Slot;
import uk.ac.cam.cl.signups.api.beans.GroupSheetBean;
import uk.ac.cam.cl.signups.api.beans.PermissionsBean;
import uk.ac.cam.cl.signups.api.beans.SlotBean;
import uk.ac.cam.cl.signups.api.beans.SlotBookingBean;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

import com.google.inject.Guice;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class ChangingPermissions {
    
    private WebInterface service =
            Guice.createInjector(ModuleProvider.provide())
            .getInstance(WebInterface.class);
    
    private Group group;
    private Sheet sheet;
    private Column column;
    private Column otherColumn;
    private Slot emptySlot;
    private Slot emptySlot2;
    private Slot bookedSlot;
    private String id;
    private String sauth;
    private String gauth;
    private PermissionsBean specificColumnPermBean;
    private PermissionsBean otherColumnPermBean;
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
        emptySlot2 = new Slot(sheet.getID(), column.getName(),
                new Date(1806308413000L), 60000L);
        bookedSlot = new Slot(sheet.getID(), column.getName(),
                new Date(2806302413000L), 789000L, "ird28", "tick6");
        
        sheet.addColumn(column);
        sheet.addColumn(otherColumn);
        SheetInfo info = service.addSheet(sheet);
        id = info.getSheetID();
        sauth = info.getAuthCode();
        service.addSheet("test-group", new GroupSheetBean(id, gauth, sauth));
        
        service.addSlot(sheet.getID(), column.getName(), new SlotBean(emptySlot, sauth));
        service.addSlot(sheet.getID(), column.getName(), new SlotBean(emptySlot2, sauth));
        service.addSlot(sheet.getID(), column.getName(), new SlotBean(bookedSlot, sauth));
        
        service.addSlot(sheet.getID(), otherColumn.getName(),
                new SlotBean(new Slot(sheet.getID(), otherColumn.getName(),
                        new Date(1806302413000L), 60000), sauth)); // emptySlot
        service.addSlot(sheet.getID(), otherColumn.getName(),
                new SlotBean(new Slot(sheet.getID(), otherColumn.getName(),
                        new Date(1806308413000L), 60000), sauth)); // emptySlot2
        service.addSlot(sheet.getID(), otherColumn.getName(),
                new SlotBean(new Slot(sheet.getID(), otherColumn.getName(), // bookedSlot
                        new Date(2806302413000L), 789000, "ird28", "tick6"), sauth));
        
        
        Map<String,String> map = new HashMap<String,String>();
        map.put("tick789", column.getName());
        specificColumnPermBean = new PermissionsBean(map, gauth);
        map = new HashMap<String,String>();
        map.put("tick789", otherColumn.getName());
        otherColumnPermBean = new PermissionsBean(map, gauth);
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
    public void setAndUnsetSpecificColumn_success() {
        try {
            service.addPermissions("test-group", "ird28", specificColumnPermBean);
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The adding of permissions should be allowed");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        }
        try {
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "ird28", "tick789"));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("Permission for the booking should have been granted");
        }
        try {
            service.removePermissions("test-group", "ird28", specificColumnPermBean);
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The removing of permissions should be allowed");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        }
        try {
            service.book(id, column.getName(), emptySlot2.getStartTime(),
                    new SlotBookingBean(null, "ird28", "tick789"));
            fail("Permission should be denied");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        } catch (NotAllowedException e) {
            /* Permission should be denied */
        }
    }
    
    @Test
    public void setAndUnsetAnyColumn_success() {
        try {
            service.addPermissions("test-group", "ird28", anyColumnPermBean);
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The adding of permissions should be allowed");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        }
        try {
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "ird28", "tick789"));
            service.book(id, otherColumn.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "ird28", "tick789"));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("Permission for the booking should have been granted");
        }
        try {
            service.removePermissions("test-group", "ird28", anyColumnPermBean);
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The removing of permissions should be allowed");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        }
        try {
            service.book(id, column.getName(), emptySlot2.getStartTime(),
                    new SlotBookingBean(null, "ird28", "tick789"));
            service.book(id, otherColumn.getName(), emptySlot2.getStartTime(),
                    new SlotBookingBean(null, "ird28", "tick789"));
            fail("Permission should be denied");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        } catch (NotAllowedException e) {
            /* Permission should be denied */
        }
    }
    
    @Test
    public void changeColumn_success() {
        try {
            service.addPermissions("test-group", "ird28", specificColumnPermBean);
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The adding of permissions should be allowed");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        }
        try {
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "ird28", "tick789"));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("Permission for the booking should have been granted");
        }
        try {
            service.book(id, otherColumn.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "ird28", "tick789"));
            fail("Permission should not be granted for this column");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        } catch (NotAllowedException e) {
            /* Permission should not be granted for this column */
        }
        try {
            service.addPermissions("test-group", "ird28", otherColumnPermBean);
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The adding of permissions should be allowed");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        }
        try {
            service.book(id, otherColumn.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "ird28", "tick789"));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("Permission for the booking should have been granted");
        }
        try {
            service.book(id, column.getName(), emptySlot.getStartTime(),
                    new SlotBookingBean(null, "ird28", "tick789"));
            fail("Permission should not be granted for this column");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        } catch (NotAllowedException e) {
            /* Permission should not be granted for this column */
        }
    }

}
