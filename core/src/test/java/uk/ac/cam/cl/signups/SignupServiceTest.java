/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.ac.cam.cl.signups.tests.*;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
@RunWith(Suite.class)
@SuiteClasses({ AddingColumns.class, AddingGroups.class, AddingSheets.class,
        AddingSlots.class, BookingSlots.class, DeletingColumns.class,
        DeletingGroups.class, DeletingSheets.class, DeletingSlots.class,
        GroupingSheets.class, ListingFreeTimes.class,
        RemovingAllUserBookings.class })
public class SignupServiceTest {

}
