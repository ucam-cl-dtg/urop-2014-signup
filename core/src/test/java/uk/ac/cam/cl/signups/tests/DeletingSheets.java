/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cl.signups.Get;
import uk.ac.cam.cl.signups.TestDatabaseModule;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.SheetInfo;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

import com.google.inject.Guice;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class DeletingSheets {
    
    private WebInterface service =
            Guice.createInjector(new TestDatabaseModule()) // Uses hashmap instead of mongo!
            .getInstance(WebInterface.class);
    
    private Sheet sheet1;
    private Sheet sheet2;
    private String id1;
    private String id2;
    private String auth1;
    private String auth2;
    
    @Before
    public void setUp() throws Exception {
        /* creates new sheets and adds them to the database */
        sheet1 = Get.sheet();
        sheet2 = Get.sheet();
        SheetInfo info1 = service.addSheet(sheet1);
        id1 = info1.getSheetID();
        auth1 = info1.getAuthCode();
        SheetInfo info2 = service.addSheet(sheet2);
        id2 = info2.getSheetID();
        auth2 = info2.getAuthCode();
        /* 
         * The sheets can be left in the database at the end of
         * tests but it doesn't matter because the database isn't
         * real.
         */
    }
    
    @Test
    public void deleteSheet_success() {
        /* delete one sheet */
        try {
            service.deleteSheet(id1, auth1);
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet should be in the database from setUp");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The authCode should be correct");
        }
        
        /* check that the sheet was deleted */
        List<Sheet> list = service.listSheets();
        assertEquals("Only one sheet should remain", 1, list.size());
        assertFalse("sheet1 should have been deleted", list.contains(sheet1));
        assertTrue("sheet2 should not have been deleted", list.contains(sheet2));
        
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
        assertEquals("No sheets should be left in the database", 0, list.size());
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
            fail("Sheet should be in database from setUp");
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
            fail("The sheet should not have been found, and this point not reached");
        }
    }

}
