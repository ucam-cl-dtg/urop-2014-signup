/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cl.signups.api.Group;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

import com.google.inject.Guice;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class AddingGroups {

    private WebInterface service =
            Guice.createInjector(new TestDatabaseModule())
            .getInstance(WebInterface.class);
    
    private Group g1 = new Group("one");
    private String auth1;
    private Group g2 = new Group("two");
    private String auth2;
    
    @After
    public void tearDown() throws Exception {
        if (service.listGroups().contains(g1)) {
            service.deleteGroup("one", auth1);
        }
        if (service.listGroups().contains(g2)) {
            service.deleteGroup("two", auth2);
        }
    }

    @Test
    public void addGroup_success() {
        try {
            auth1 = service.addGroup(g1).getAuthCode();
            auth2 = service.addGroup(g2).getAuthCode();
            assertTrue("Database should contain both groups",
                    service.listGroups().contains(g1));
            assertTrue("Database should contain both groups",
                    service.listGroups().contains(g2));
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("Group should not already exist");
        }
    }
    
    @Test
    public void addGroup_exception_alreadyExists() {
        try {
            auth1 = service.addGroup(g1).getAuthCode();
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("Group should not already exist");
        }
        try {
            auth1 = service.addGroup(g1).getAuthCode();
            fail("Group already exists");
        } catch (DuplicateNameException e) {
            /* Group already exists */
        }
        try {
            service.addGroup(new Group("one"));
            fail("Group already exists");
        } catch (DuplicateNameException e) {
            /* Group already exists */
        }
        
    }

}
