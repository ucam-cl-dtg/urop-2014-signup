package uk.ac.cam.cl.signups;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;

import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.SheetInfo;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

public class AddingAndRemovingSheets {

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
        sheet1 = Get.sheet();
        sheet2 = Get.sheet();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addRemoveSheetTest() {
        try {
            SheetInfo info1 = service.addSheet(sheet1);
            id1 = info1.getSheetID();
            auth1 = info1.getAuthCode();
            SheetInfo info2 = service.addSheet(sheet2);
            id2 = info2.getSheetID();
            auth2 = info2.getAuthCode();
            List<Sheet> list = service.listSheets();
            assertEquals(2, list.size());
            assertTrue(list.contains(sheet1));
            assertTrue(list.contains(sheet2));
            service.deleteSheet(id1, auth1);
            list = service.listSheets();
            assertEquals(1, list.size());
            assertFalse(list.contains(sheet1));
            assertTrue(list.contains(sheet2));
            service.deleteSheet(id2, auth2);
            list = service.listSheets();
            assertEquals(0, list.size());
            assertFalse(list.contains(sheet1));
            assertFalse(list.contains(sheet2));
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("The sheets should not already be in the database");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheets should be found in the database");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The deletions should be allowed");
        }
    }

}
