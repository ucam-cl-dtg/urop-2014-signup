package uk.ac.cam.cl.signups.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;

import uk.ac.cam.cl.signups.Get;
import uk.ac.cam.cl.signups.TestDatabaseModule;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

public class AddingSheets {

    private WebInterface service =
            Guice.createInjector(new TestDatabaseModule()) // Uses hashmap instead of mongo!
            .getInstance(WebInterface.class);
    
    private Sheet sheet1;
    private Sheet sheet2;
    

    @Before
    public void setUp() throws Exception {
        sheet1 = Get.sheet();
        sheet2 = Get.sheet();
    }

    @Test
    public void addSheet_success() {
        try {
            /* add sheets */
            service.addSheet(sheet1);
            service.addSheet(sheet2);
            
            /* check that the sheets were added */
            List<Sheet> list = service.listSheets();
            assertEquals(2, list.size());
            assertTrue(list.contains(sheet1));
            assertTrue(list.contains(sheet2));
        } catch (DuplicateNameException e) {
            /* really this should never happen - we are using a fresh "database" */
            e.printStackTrace();
            fail("The sheets should not already be in the database");
        }
    }
    
    @Test
    public void addSheet_exception_alreadyInDatabase() {
        try {
            /* Add sheet - should work */
            service.addSheet(sheet1);
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("The sheet should not already exist in the database");
        }
        try {
            /* Add same sheet again - should fail */
            service.addSheet(sheet1);
            fail("Should not be allowed to add the same sheet twice");
        } catch (DuplicateNameException e) {
            /* Should be caught - added same sheet twice */
        }
    }

}
