package uk.ac.cam.cl.signups.tests;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;

import uk.ac.cam.cl.signups.Get;
import uk.ac.cam.cl.signups.ModuleProvider;
import uk.ac.cam.cl.signups.TestDatabaseModule;
import uk.ac.cam.cl.signups.api.Column;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.SheetInfo;
import uk.ac.cam.cl.signups.api.Slot;
import uk.ac.cam.cl.signups.api.beans.SlotBean;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.interfaces.SignupsWebInterface;

public class DeletingSlots {
    
    private SignupsWebInterface service =
            Guice.createInjector(ModuleProvider.provide())
            .getInstance(SignupsWebInterface.class);
    
    private Sheet sheet;
    private Column column;
    private Slot slot;
    private String id;
    private String auth;

    @Before
    public void setUp() throws Exception {
        sheet = Get.sheet();
        column = Get.column(sheet.get_id());
        
        slot = new Slot(sheet.get_id(), column.getName(),
                new Date(), 60000, "test-user", "slot to remove in the test");
        sheet.addColumn(column);        
        SheetInfo info = service.addSheet(sheet); // We assume this function is fine
        id = info.getSheetID();
        auth = info.getAuthCode();
        service.addSlot(sheet.get_id(), column.getName(), new SlotBean(slot, auth));
    }

    @Test
    public void deleteSlot_success() {
        try {
            System.out.println("Testing deleting a slot from a column");
            System.out.println("Slots before:");
            System.out.println(service.listColumnSlots(id, column.getName()));
            service.deleteSlot(id, column.getName(), slot.getStartTime().getTime(), auth);
            System.out.println("Slots after (should now not contain test-user booked slot):");
            System.out.println(service.listColumnSlots(id, column.getName()));
            System.out.println();
        } catch (ItemNotFoundException e) {
            fail("The sheet and column should be found");
        } catch (NotAllowedException e) {
            fail("The authCode should be correct");
        }
    }
    
    @Test
    public void deleteSlot_exception_sheetNotFound() {
        try {
            service.deleteSlot("non existent sheet", column.getName(), slot.getStartTime().getTime(), auth);
            fail("Sheet should not be found");
        } catch (ItemNotFoundException e) {
            /* Sheet should not be found */
        } catch (NotAllowedException e) {
            fail("Sheet should not be found");
        }
    }
    
    @Test
    public void deleteSlot_exception_columnNotFound() {
        try {
            service.deleteSlot(id, "non existent column", slot.getStartTime().getTime(), auth);
            fail("Column should not be found");
        } catch (ItemNotFoundException e) {
            /* Column should not be found */
        } catch (NotAllowedException e) {
            fail("Column should not be found");
        }
    }
    
    @Test
    public void deleteSlot_exception_slotNotFound() {
        try {
            service.deleteSlot(id, column.getName(),
                    Get.slot(sheet.get_id(), column.getName()).getStartTime().getTime(), auth);
            fail("Slot should not be found");
        } catch (ItemNotFoundException e) {
            /* Slot should not be found */
        } catch (NotAllowedException e) {
            fail("Slot should not be found");
        }
    }
    
    @Test
    public void deleteSlot_exception_wrongAuthCode() {
        try {
            service.deleteSlot(id, column.getName(), slot.getStartTime().getTime(), "incorrect auth code");
            fail("The authCode should be incorrect");
        } catch (ItemNotFoundException e) {
            fail("The authCode should be incorrect");
        } catch (NotAllowedException e) {
            /* The authCode should be incorrect" */
        }
    }

}
