/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.tests;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;

import uk.ac.cam.cl.signups.Get;
import uk.ac.cam.cl.signups.ModuleProvider;
import uk.ac.cam.cl.signups.TestDatabaseModule;
import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.beans.ColumnBean;
import uk.ac.cam.cl.signups.api.beans.PermissionsBean;
import uk.ac.cam.cl.signups.api.beans.SlotBean;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.interfaces.SignupsWebInterface;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class ListingFreeTimes {
    
    /* TODO: lots more testing of new functionality (only appropriate times shown) */
    
    private SignupsWebInterface service =
            Guice.createInjector(ModuleProvider.provide())
            .getInstance(SignupsWebInterface.class);
    
    private Sheet sheet;
    private String id;
    private String sauth;
    private String gauth;
    private Column column;
    private Slot slot1;
    private Slot slot2;

    @Before
    public void setUp() throws Exception {
        sheet = new Sheet("title", Get.name(), "location");
        column = Get.emptyColumn();
        List<Slot> slotList = Get.slotList(sheet.get_id(), column.getName());
        Slot slot = Get.slot(sheet.get_id(), "test-column");
        slotList.add(slot);
        for (int i = 0; i < 100; i++) {
            slotList.addAll(Get.slotList(sheet.get_id(), "test-column"));
        }
        sheet = new Sheet("title", Get.name(), "location");
        SheetInfo info = service.addSheet(sheet);
        id = info.getSheetID();
        sauth = info.getAuthCode();
        service.addColumn(sheet.get_id(), new ColumnBean(column, sauth));
        for (Slot s : slotList) {
            service.addSlot(id, column.getName(), new SlotBean(s, sauth));
        }
        slot1 = new Slot(id, column.getName(), new Date(14073359110000L), 60000L);
        slot1 = new Slot(id, column.getName(), new Date(14073359110000L), 60000L, "ird28", "Tick 6");
        service.addSlot(id, column.getName(), new SlotBean(slot1, sauth));
        service.addSlot(id, column.getName(), new SlotBean(slot2, sauth));
        
        gauth = service.addGroup(new Group("test-group"));
        Map<String, String> map = new HashMap<String, String>();
        map.put("Tick 6", null);
        service.addPermissions("test-group", "ird28", new PermissionsBean(map, gauth));
    }
    
    @After
    public void tearDown() throws Exception {
        service.deleteSheet(id, sauth);
    }

    @Test
    public void listAllFreeStartTimes_success() {
        try {
            List<Date> frees = service.listAllFreeStartTimes("ird28", "Tick 6", "test-group", id);
            for (int i = 1; i < frees.size(); i++) {
                assertFalse("The list should be in chronological order",
                        frees.get(i).before(frees.get(i-1)));
                assertFalse("The list should be without repeats",
                        frees.get(i).equals(frees.get(i-1)));
            }
            for (Slot slot : service.listColumnSlots(sheet.get_id(), column.getName())) {
                if (!slot.isBooked()) {
                    assertTrue("All free slots should be listed",
                            frees.contains(slot.getStartTime()));
                }
            }
            for (Date startTime : frees) {
                assertTrue("All listed times should correspond to at least one free slot",
                        thereIsASlotWithStartTime(startTime));
            }
            
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet should be found");
        }
    }
    
    @Test
    public void listAllFreeStartTimes_exception_sheetNotFound() {
        try {
            service.listAllFreeStartTimes("ird28", "Tick 6", "test-group", "non existent sheet");
        } catch (ItemNotFoundException e) {
            /* Sheet should not be found */
        }
    }
    
    private boolean thereIsASlotWithStartTime(Date startTime) throws ItemNotFoundException {
        for (Slot slot : service.listColumnSlots(sheet.get_id(), column.getName())) {
            if (slot.getStartTime().equals(startTime)) {
                return true;
            }
        }
        return false;
    }

}
