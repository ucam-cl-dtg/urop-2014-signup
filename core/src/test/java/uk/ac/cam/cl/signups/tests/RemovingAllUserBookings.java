/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.tests;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cl.signups.Get;
import uk.ac.cam.cl.signups.ModuleProvider;
import uk.ac.cam.cl.signups.TestDatabaseModule;
import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.beans.SlotBean;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

import com.google.inject.Guice;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class RemovingAllUserBookings {
    
    private WebInterface service =
            Guice.createInjector(ModuleProvider.provide())
            .getInstance(WebInterface.class);
    
    private Sheet sheet = Get.sheet();
    private Column col1 = Get.column(sheet.getID());
    private Column col2 = Get.column(sheet.getID());
    
    private Slot bookedByUserInFuture =
            new Slot(sheet.getID(), col1.getName(),
                    new Date(40000000000000L), 100000L, "user", "comment");
    private Slot bookedByUserInNearFuture =
            new Slot(sheet.getID(), col2.getName(),
                    new Date(new Date().getTime()+10000L), 10000000L, "user", "comment");
    private Slot bookedByUserInNearPast =
            new Slot(sheet.getID(), col2.getName(),
                    new Date(new Date().getTime()-10000L), 10000000L, "user", "comment");
    private Slot bookedByUserInPast =
            new Slot(sheet.getID(), col1.getName(),
                    new Date(0L), 50L, "user", "comment");
    
    private Slot bookedByOtherUserInFuture =
            new Slot(sheet.getID(), col2.getName(),
                    new Date(41000000000000L), 100000L, "other", "comment");
    private Slot bookedByOtherUserInNearFuture =
            new Slot(sheet.getID(), col1.getName(),
                    new Date(new Date().getTime()+10000L), 10000000L, "other", "comment");
    private Slot bookedByOtherUserInPast =
            new Slot(sheet.getID(), col1.getName(),
                    new Date(1001L), 600L, "other", "comment");
    
    private String id;
    private String auth;
    
    @Before
    public void setUp() throws Exception {
        sheet.addColumn(col1);
        sheet.addColumn(col2);
        SheetInfo info = service.addSheet(sheet);
        id = info.getSheetID();
        auth = info.getAuthCode();
        service.addSlot(sheet.getID(), col1.getName(), new SlotBean(bookedByUserInFuture, auth));
        service.addSlot(sheet.getID(), col1.getName(), new SlotBean(bookedByUserInPast, auth));
        service.addSlot(sheet.getID(), col1.getName(), new SlotBean(bookedByOtherUserInPast, auth));
        service.addSlot(sheet.getID(), col1.getName(), new SlotBean(bookedByOtherUserInNearFuture, auth));
        service.addSlot(sheet.getID(), col2.getName(), new SlotBean(bookedByUserInNearFuture, auth));
        service.addSlot(sheet.getID(), col2.getName(), new SlotBean(bookedByUserInNearPast, auth));
        service.addSlot(sheet.getID(), col2.getName(), new SlotBean(bookedByOtherUserInFuture, auth));
    }

    @Test
    public void removeAllUserBookings_success() {
        try {
            service.removeAllUserBookings(id, "user", auth);
            
            assertTrue("Slot booked in future by user should be unbooked",
                    service.showBooking(id, col1.getName(),
                            bookedByUserInFuture.getStartTime()).getUser() == null);
            
            assertTrue("Slot booked in future by user should be unbooked",
                    service.showBooking(id, col2.getName(),
                            bookedByUserInNearFuture.getStartTime()).getUser() == null);
            
            assertTrue("Slot booked in past by user should be still booked",
                    service.showBooking(id, col2.getName(),
                            bookedByUserInNearPast.getStartTime()).getUser().equals("user"));
            
            assertTrue("Slot booked in past by user should be still booked",
                    service.showBooking(id, col1.getName(),
                            bookedByUserInPast.getStartTime()).getUser().equals("user"));
            
            assertTrue("Slot booked by other user should be untouched",
                    service.showBooking(id, col2.getName(),
                            bookedByOtherUserInFuture.getStartTime()).getUser().equals("other"));
            
            assertTrue("Slot booked by other user should be untouched",
                    service.showBooking(id, col1.getName(),
                            bookedByOtherUserInNearFuture.getStartTime()).getUser().equals("other"));
            
            assertTrue("Slot booked by other user should be untouched",
                    service.showBooking(id, col1.getName(),
                            bookedByOtherUserInPast.getStartTime()).getUser().equals("other"));
            
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("Auth code should be correct");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Everything should be found");
        }
    }

}
