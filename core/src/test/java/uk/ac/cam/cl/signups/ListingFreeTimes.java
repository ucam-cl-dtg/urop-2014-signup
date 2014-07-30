/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;

import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class ListingFreeTimes {
    
    private WebInterface service =
            Guice.createInjector(new TestDatabaseModule())
            .getInstance(WebInterface.class);
    
    private Sheet sheet;
    private String id;
    private Column column;

    @Before
    public void setUp() throws Exception {
        List<Slot> slotList = Get.slotList();
        column = new Column("test-column", slotList);
        Collection<Column> cols = new LinkedList<Column>();
        cols.add(column);
        sheet = new Sheet("title", "description", "location", cols);
        id = service.addSheet(sheet).getSheetID();
    }

    @Test
    public void listAllFreeStartTimes_success() {
        try {
            List<Date> frees = service.listAllFreeStartTimes(id);
            for (int i = 1; i < frees.size(); i++) {
                assertTrue("The list should be in chronological order without repeats",
                        frees.get(i).after(frees.get(i-1)));
            }
            for (Slot slot : column.getSlots()) {
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
            service.listAllFreeStartTimes("non existent sheet");
        } catch (ItemNotFoundException e) {
            /* Sheet should not be found */
        }
    }
    
    private boolean thereIsASlotWithStartTime(Date startTime) {
        for (Slot slot : column.getSlots()) {
            if (slot.getStartTime().equals(startTime)) {
                return true;
            }
        }
        return false;
    }

}
