/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups;

import static org.junit.Assert.*;

import org.junit.Test;
import org.easymock.EasyMock;

import uk.ac.cam.cl.signups.api.Group;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.database.DatabaseCollection;
import uk.ac.cam.cl.signups.database.HashMapCollection;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class SignupServiceTest {
    
    private SignupService service = new SignupService();
    
    @SuppressWarnings("unchecked")
    private DatabaseCollection<Sheet> sheets = EasyMock.createMock(DatabaseCollection.class);
    @SuppressWarnings("unchecked")
    private DatabaseCollection<User> users = EasyMock.createMock(DatabaseCollection.class);
    @SuppressWarnings("unchecked")
    private DatabaseCollection<Group> groups = EasyMock.createMock(DatabaseCollection.class);
    
    {
        SignupService.setSheets(sheets);
        SignupService.setUsers(users);
        SignupService.setGroups(groups);
    }

    @Test
    public void test() {
        /* The below method calls are expected */
        
        EasyMock.replay(sheets, users, groups);
        /* The actual test begins here */
        
        EasyMock.verify(sheets, users, groups);        
    }

}
