package uk.ac.cam.cl.signups.tests;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;

import uk.ac.cam.cl.signups.Get;
import uk.ac.cam.cl.signups.TestDatabaseModule;
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
            Guice.createInjector(new TestDatabaseModule())
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
        slot = new Slot(new Date(), 60000, "test-user", "this slot has been added in the test");
        SheetInfo info = service.addSheet(sheet); // We assume this function is fine
        id = info.getSheetID();
        auth = info.getAuthCode();
    }

    @Test
    public void addSlot_success() {
        try {
            System.out.println("Testing adding a slot to a column");
            System.out.println("Slots before:");
            System.out.println(service.listSlots(id, column.getName()));
            service.addSlot(id, column.getName(), new SlotBean(slot, auth));
            System.out.println("Slots after (should now contain test-user booked slot):");
            System.out.println(service.listSlots(id, column.getName()));
            System.out.println();
            assertTrue("Column should now contain slot",
                    service.listSlots(id, column.getName()).contains(slot));
        } catch (ItemNotFoundException e) {
            fail("The sheet and column should be found");
        } catch (NotAllowedException e) {
            fail("The authCode should be correct");
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("The slot should not already exist");
        }
    }
    
    @Test
    public void addSlot_exception_sheetNotFound() {
        try {
            service.addSlot("not existing", column.getName(), new SlotBean(slot, auth));
            fail("Sheet should not be found");
        } catch (ItemNotFoundException e) {
            /* Sheet should not be found */
        } catch (NotAllowedException e) {
            fail("Sheet should not be found");
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("The slot should not already exist");
        }
    }
    
    @Test
    public void addSlot_exception_columnNotFound() {
        try {
            service.addSlot(id, "some unknown column", new SlotBean(slot, auth));
            fail("Column should not be found");
        } catch (ItemNotFoundException e) {
            /* Column should not be found */
        } catch (NotAllowedException e) {
            fail("Column should not be found");
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("The slot should not already exist");
        }
    }
    
    @Test
    public void addSlot_exception_slotAlreadyExists() {
        try {
            /* Add a slot */
            service.addSlot(id, column.getName(), new SlotBean(slot, auth));
            assertTrue("Column should now contain slot",
                    service.listSlots(id, column.getName()).contains(slot));
        } catch (ItemNotFoundException e) {
            fail("The sheet and column should be found");
        } catch (NotAllowedException e) {
            fail("The authCode should be correct");
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("The slot should not already exist");
        }
        try {
            /* Add the same slot again */
            service.addSlot(id, column.getName(), new SlotBean(slot, auth));
            fail("Slot already exists");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet and column should be found");
        } catch (NotAllowedException e) {
            fail("Auth code should be correct");
        } catch (DuplicateNameException e) {
            /* The slot should already exist */
        }
        try {
            /* Add a slot with the same start time again */
            service.addSlot(id, column.getName(),
                    new SlotBean(new Slot(slot.getStartTime(), 50000L), auth));
            fail("Slot already exists");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Sheet and column should be found");
        } catch (NotAllowedException e) {
            fail("Auth code should be correct");
        } catch (DuplicateNameException e) {
            /* The slot should already exist */
        }
    }
    
    @Test
    public void addSlot_exception_wrongAuthCode() {
        try {
            service.addSlot(id, column.getName(), new SlotBean(slot, "wrong auth code"));
            fail("The authCode should be incorrect");
        } catch (ItemNotFoundException e) {
            fail("The authCode should be incorrect");
        } catch (NotAllowedException e) {
            /* The authCode should be incorrect" */
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("The slot should not already exist");
        }
    }

}
