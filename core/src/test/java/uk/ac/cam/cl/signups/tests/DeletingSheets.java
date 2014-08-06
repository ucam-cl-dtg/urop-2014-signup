/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.tests;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cl.signups.Get;
import uk.ac.cam.cl.signups.ModuleProvider;
import uk.ac.cam.cl.signups.TestDatabaseModule;
import uk.ac.cam.cl.signups.api.Column;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.SheetInfo;
import uk.ac.cam.cl.signups.api.Slot;
import uk.ac.cam.cl.signups.api.beans.ColumnBean;
import uk.ac.cam.cl.signups.api.beans.SlotBean;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

import com.google.inject.Guice;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class DeletingSheets {
    
    private WebInterface service =
            Guice.createInjector(ModuleProvider.provide())
            .getInstance(WebInterface.class);
    
    private Sheet sheet1;
    private Sheet sheet2;
    private String id1;
    private String id2;
    private String auth1;
    private String auth2;
    private Column column;
    private Slot slot;
    
    @Before
    public void setUp() throws Exception {
        /* creates new sheets and adds them to the database */
        sheet1 = Get.sheetWithEmptyCols();
        sheet2 = Get.sheetWithEmptyCols();
        SheetInfo info1 = service.addSheet(sheet1);
        id1 = info1.getSheetID();
        auth1 = info1.getAuthCode();
        SheetInfo info2 = service.addSheet(sheet2);
        id2 = info2.getSheetID();
        auth2 = info2.getAuthCode();
        column = Get.emptyColumn();
        
        service.addColumn(id1, new ColumnBean(column, auth1));
        slot = new Slot(id1, column.getName(), new Date(1607319592000L), 60000, "ird28", "tick9");
        service.addSlot(id1, column.getName(), new SlotBean(slot, auth1));
    }
    
    @After
    public void tearDown() throws Exception {
        if (service.listSheets().contains(sheet1)) {
            service.deleteSheet(id1, auth1);
        }
        if (service.listSheets().contains(sheet2)) {
            service.deleteSheet(id2, auth2);
        }
    }
    
    @Test
    public void deleteSheet_success() {
        int initialSheets = service.listSheets().size();
        assertTrue(service.listUserSlots("ird28").contains(slot));
        
        /* delete one sheet */
        try {
            service.deleteSheet(id1, auth1);
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet and its slot should be found in the database");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The authCode should be correct");
        }
        
        /* check that the sheet was deleted */
        List<Sheet> list = service.listSheets();
        assertEquals("One sheet should have been deleted", 1, initialSheets-list.size());
        assertFalse("sheet1 should have been deleted", list.contains(sheet1));
        assertTrue("sheet2 should not have been deleted", list.contains(sheet2));
        
        /* check that the slot was deleted */
        assertFalse("Slot should have been deleted as well", service.listUserSlots("ird28").contains(slot));
        
        /* delete the other sheet */
        try {
            service.deleteSheet(id2, auth2);
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet should be in the database from setUp");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The authCode should be correct");
        }
        
        /* check that the sheet was deleted */
        list = service.listSheets();
        assertEquals("Both sheets should have been deleted", 2, initialSheets-list.size());
        assertFalse("sheet1 should have been deleted", list.contains(sheet1));
        assertFalse("sheet2 should have been deleted", list.contains(sheet2));
    }
    
    @Test
    public void deleteSheet_exception_wrongAuthCode() {
        try {
            service.deleteSheet(id1, "wrong auth code");
            fail("Permission should be denied - wrong authCode");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet and its slots should be found in the database");
        } catch (NotAllowedException e) {
            /* Should be caught - wrong auth code */
        }
    }
    
    @Test
    public void deleteSheet_exception_sheetNotfound() {
        try {
            service.deleteSheet("some ID not in the database", auth1);
            fail("Exception should have been raised - sheet should not be found");
        } catch (ItemNotFoundException e) {
            /* The sheet should not have been found and this exception raised */
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The sheet should not have been found, and this point not reached");
        }
    }

}
