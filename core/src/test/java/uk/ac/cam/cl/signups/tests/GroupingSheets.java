/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.cam.cl.signups.Get;
import uk.ac.cam.cl.signups.ModuleProvider;
import uk.ac.cam.cl.signups.TestDatabaseModule;
import uk.ac.cam.cl.signups.api.Group;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.beans.GroupSheetBean;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.interfaces.SignupsWebInterface;

import com.google.inject.Guice;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class GroupingSheets {

    private SignupsWebInterface service =
            Guice.createInjector(ModuleProvider.provide())
            .getInstance(SignupsWebInterface.class);

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
        gAuthCode1 = service.addGroup(group1);
        gAuthCode2 = service.addGroup(group2);
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
    public void addSheet_success() {
        try {
            service.addSheetToGroup("test-group-1",
                    new GroupSheetBean(sheet1.get_id(), gAuthCode1, sAuthCode1));
            service.addSheetToGroup("test-group-1",
                    new GroupSheetBean(sheet2.get_id(), gAuthCode1, sAuthCode2));
            service.addSheetToGroup("test-group-2",
                    new GroupSheetBean(sheet1.get_id(), gAuthCode2, sAuthCode1));
            assertTrue(service.listSheets("test-group-1").contains(sheet1));
            assertTrue(service.listSheets("test-group-1").contains(sheet2));
            assertTrue(service.listSheets("test-group-2").contains(sheet1));
            assertFalse(service.listSheets("test-group-2").contains(sheet2));
            service.removeSheetFromGroup("test-group-1", sheet1.get_id(), gAuthCode1);
            assertFalse(service.listSheets("test-group-1").contains(sheet1));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The groups and sheets should all be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("All operations should be allowed");
        }
    }
    
    @Test
    public void addSheet_exception_sheetNotFound() {
        try {
            service.addSheetToGroup("test-group-1",
                    new GroupSheetBean("sheet not found", gAuthCode1, sAuthCode1));
            fail("Sheet should not be found");
        } catch (ItemNotFoundException e) {
            /* Sheet should not be found */
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("Sheet should not be found");
        }
    }
    
    @Test
    public void addSheet_exception_groupNotFound() {
        try {
            service.addSheetToGroup("no such group",
                    new GroupSheetBean(sheet1.get_id(), gAuthCode1, sAuthCode1));
            fail("Group should not be found");
        } catch (ItemNotFoundException e) {
            /* Group should not be found */
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("Group should not be found");
        }
    }
    
    @Test
    public void addSheet_exception_wrongSheetAuthCode() {
        try {
            service.addSheetToGroup("test-group-1",
                    new GroupSheetBean(sheet1.get_id(), gAuthCode1, "wrong code"));
            fail("The sheet auth code is wrong");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        } catch (NotAllowedException e) {
            /* The sheet auth code is wrong */
        }
    }
    
    @Test
    public void addSheet_exception_wrongGroupAuthCode() {
        try {
            service.addSheetToGroup("test-group-1",
                    new GroupSheetBean(sheet1.get_id(), "wrong code", sAuthCode1));
            fail("The group auth code is wrong");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        } catch (NotAllowedException e) {
            /* The group auth code is wrong */
        }
    }
    
    

    @Test
    public void removeSheet_success() {
        try {
            /* add sheets to be removed - assumed to work - tested elsewhere */
            service.addSheetToGroup("test-group-1",
                    new GroupSheetBean(sheet1.get_id(), gAuthCode1, sAuthCode1));
            service.addSheetToGroup("test-group-1",
                    new GroupSheetBean(sheet2.get_id(), gAuthCode1, sAuthCode2));
            service.addSheetToGroup("test-group-2",
                    new GroupSheetBean(sheet1.get_id(), gAuthCode2, sAuthCode1));
            /* alternately remove sheets and check they have been removed */
            service.removeSheetFromGroup("test-group-1", sheet1.get_id(), gAuthCode1);
            assertFalse(service.listSheets("test-group-1").contains(sheet1.get_id()));
            service.removeSheetFromGroup("test-group-1", sheet2.get_id(), gAuthCode1);
            assertFalse(service.listSheets("test-group-1").contains(sheet2.get_id()));
            service.removeSheetFromGroup("test-group-2", sheet1.get_id(), gAuthCode2);
            assertFalse(service.listSheets("test-group-2").contains(sheet2.get_id()));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The groups and sheets should all be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("All operations should be allowed");
        }

    }
    
    @Test
    public void removeSheet_exception_sheetNotFound() {
        try {
            service.removeSheetFromGroup("test-group-1", "no such sheet", gAuthCode1);
            fail("Sheet should not be found");
        } catch (ItemNotFoundException e) {
            /* Sheet should not be found */
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("Sheet should not be found");
        }
    }
    
    @Test
    public void removeSheet_exception_groupNotFound() {
        try {
            service.removeSheetFromGroup("no such group", sheet1.get_id(), gAuthCode1);
            fail("Group should not be found");
        } catch (ItemNotFoundException e) {
            /* Group should not be found */
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("Group should not be found");
        }
    }
    
    @Test
    public void removeSheet_exception_wrongGroupAuthCode() {
        try { /* add a sheet to remove */
            service.addSheetToGroup("test-group-1",
                    new GroupSheetBean(sheet1.get_id(), gAuthCode1, sAuthCode1));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The groups and sheets should all be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("All operations should be allowed");
        }
        try {
            service.removeSheetFromGroup("test-group-1", sheet1.get_id(), "wrong code");
            fail("The group auth code is wrong");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        } catch (NotAllowedException e) {
            /* The group auth code is wrong */
        }
    }
    
    @Test
    public void removeSheet_exception_sheetNotInGroup() {
        try {
            service.removeSheetFromGroup("test-group-1", sheet1.get_id(), gAuthCode1);
            fail("The sheet was not in the group to begin with");
        } catch (ItemNotFoundException e) {
            /* The sheet was not in the group to begin with */
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The sheet was not in the group to begin with");
        }
    }


}
