package uk.ac.cam.cl.signups;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cl.signups.api.Group;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

import com.google.inject.Guice;

public class DeletingGroups {
    
    private WebInterface service =
            Guice.createInjector(new TestDatabaseModule())
            .getInstance(WebInterface.class);
    
    private Group g1 = new Group("one");
    private String auth1;
    private Group g2 = new Group("two");
    private String auth2;

    @Before
    public void setUp() throws Exception {
        auth1 = service.addGroup(g1).getAuthCode();
        auth2 = service.addGroup(g2).getAuthCode();
    }
    
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
    public void deleteGroup_success() {
        try {
            service.deleteGroup("one", auth1);
            service.deleteGroup("two", auth2);
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("Groups should be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("Auth codes should be correct");
        }
    }
    
    @Test
    public void deleteGroup_exception_groupNotFound() {
        try {
            service.deleteGroup("non existent group", auth1);
            fail("The group should not be found");
        } catch (ItemNotFoundException e) {
            /* The group should not be found */
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The group should not be found");
        }
    }
    
    @Test
    public void deleteGroup_exception_wrongAuthCode() {
        try {
            service.deleteGroup("one", "wrong auth code");
            fail("The auth code is wrong");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The auth code is wrong");
        } catch (NotAllowedException e) {
            /* The auth code is wrong */
        }
    }

}
