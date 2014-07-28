/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.cam.cl.signups.api.Group;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.beans.GroupSheetBean;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

import com.google.inject.Guice;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class GroupingSheets {
    
    private WebInterface service =
            Guice.createInjector(new DatabaseModule())
            .getInstance(WebInterface.class);
    
    private Sheet sheet1;
    private Sheet sheet2;
    private Group group1 = new Group("test-group-1");
    private Group group2 = new Group("test-group-2");
    private String sAuthCode1;
    private String sAuthCode2;
    private String gAuthCode1;
    private String gAuthCode2;

    @Before
    public void setUp() throws DuplicateNameException {
        gAuthCode1 = service.addGroup(group1).getAuthCode();
        gAuthCode2 = service.addGroup(group2).getAuthCode();
        sheet1 = Get.sheet();
        sheet2 = Get.sheet();
        sAuthCode1 = service.addSheet(sheet1).getAuthCode();
        sAuthCode2 = service.addSheet(sheet2).getAuthCode();
    }
    
    @After
    public void tearDown() throws ItemNotFoundException, NotAllowedException {
        service.deleteGroup("test-group-1", gAuthCode1);
        service.deleteGroup("test-group-2", gAuthCode2);
    }
    
    @Test
    public void groupSheets() {
        try {
            service.addSheet("test-group-1",
                    new GroupSheetBean(sheet1.getName(), gAuthCode1, sAuthCode1));
            service.addSheet("test-group-1",
                    new GroupSheetBean(sheet2.getName(), gAuthCode1, sAuthCode2));
            service.addSheet("test-group-2",
                    new GroupSheetBean(sheet1.getName(), gAuthCode2, sAuthCode1));
            assertTrue(service.listSheetIDs("test-group-1").contains(sheet1.getName()));
            assertTrue(service.listSheetIDs("test-group-1").contains(sheet2.getName()));
            assertTrue(service.listSheetIDs("test-group-2").contains(sheet1.getName()));
            assertFalse(service.listSheetIDs("test-group-2").contains(sheet2.getName()));
            service.removeSheetFromGroup("test-group-1", sheet1.getName(), gAuthCode1);
            assertFalse(service.listSheetIDs("test-group-1").contains(sheet1.getName()));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The groups and sheets should all be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("All operations should be allowed");
        }
        
        
    }
    

}
