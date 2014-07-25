/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups;

import static org.junit.Assert.*;

import org.junit.Test;
import org.easymock.EasyMock;

import com.google.inject.Guice;

import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class SignupServiceTest {
    
    private WebInterface service =
            Guice.createInjector(new TestDatabaseModule())
            .getInstance(WebInterface.class);
    
    @Test
    public void addAndRemoveSlotToColumn() {
        Column c1 = Get.column();
        Slot slot = Get.slot();
        c1.addSlot(slot); // add slot to column
        assertTrue(c1.getSlots().contains(slot)); // check has been added
        try {
            assertEquals(slot, c1.getSlot(slot.getStartTime()));
        } catch (ItemNotFoundException e) {
            fail("This should never be reached as if it wasn't contained "
                    + "it would have failed at the previous assertion");
        }
        c1.removeSlot(slot.getStartTime()); // remove slot
        assertFalse(c1.getSlots().contains(slot)); // check has been removed
    }
    
}
