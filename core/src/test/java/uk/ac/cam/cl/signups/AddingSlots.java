package uk.ac.cam.cl.signups;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;

import uk.ac.cam.cl.signups.api.Column;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.SheetInfo;
import uk.ac.cam.cl.signups.api.Slot;
import uk.ac.cam.cl.signups.api.beans.SlotBean;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

public class AddingSlots {
    
    private WebInterface service =
            Guice.createInjector(new DatabaseModule())
            .getInstance(WebInterface.class);
    
    private Sheet sheet;
    private Column column;
    private Slot slot;
    private String id;
    private String auth;

    @Before
    public void setUp() throws DuplicateNameException {
        sheet = Get.sheet();
        column = Get.column();
        sheet.addColumn(column);
        slot = Get.slot();
        SheetInfo info = service.addSheet(sheet); // We assume this function is fine
        id = info.getSheetID();
        auth = info.getAuthCode();
    }
    
    @After
    public void removeDatabaseEntry() throws ItemNotFoundException, NotAllowedException {
        service.deleteSheet(id, auth); // We assume this function is fine
    }

    @Test
    public void addSlotTest() {
        try {
            service.addSlot(id, column.getName(), new SlotBean(slot, auth));
        } catch (ItemNotFoundException e) {
            fail("The sheet and column should be found");
        } catch (NotAllowedException e) {
            fail("The authCode should be correct");
        }
    }
    
    @Test
    public void nonExistentSheetTest() {
        try {
            service.addSlot("not existing", column.getName(), new SlotBean(slot, auth));
            fail("Sheet should not be found");
        } catch (ItemNotFoundException e) {
            /* Sheet should not be found */
        } catch (NotAllowedException e) {
            fail("Sheet should not be found");
        }
    }
    
    @Test
    public void nonExistentColumnTest() {
        try {
            service.addSlot(id, "daniel", new SlotBean(slot, auth));
            fail("Column should not be found");
        } catch (ItemNotFoundException e) {
            /* Column should not be found */
        } catch (NotAllowedException e) {
            fail("Column should not be found");
        }
    }
    
    @Test
    public void incorrectAuthCodeTest() {
        try {
            service.addSlot(id, column.getName(), new SlotBean(slot, "password"));
            fail("The authCode should almost certainly incorrect");
        } catch (ItemNotFoundException e) {
            fail("The authCode should almost certainly incorrect");
        } catch (NotAllowedException e) {
            /* The authCode should almost certainly incorrect" */
        }
    }

}
