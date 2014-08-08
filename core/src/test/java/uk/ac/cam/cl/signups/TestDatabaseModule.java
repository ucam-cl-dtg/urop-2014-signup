/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups;

import uk.ac.cam.cl.signups.SignupService;
import uk.ac.cam.cl.signups.User;
import uk.ac.cam.cl.signups.api.Group;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.Slot;
import uk.ac.cam.cl.signups.database.DatabaseCollection;
import uk.ac.cam.cl.signups.database.HashMapCollection;
import uk.ac.cam.cl.signups.interfaces.SignupsWebInterface;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class TestDatabaseModule extends AbstractModule {
        
    @Override
    protected void configure() {
        bind(SignupsWebInterface.class).to(SignupService.class);
    }
    
    @Provides
    public DatabaseCollection<Sheet> provideSheetCollection() {
        return new HashMapCollection<Sheet>();
    }
    
    @Provides
    public DatabaseCollection<User> provideUserCollection() {
        return new HashMapCollection<User>();
    }
    
    @Provides
    public DatabaseCollection<Group> provideGroupCollection() {
        return new HashMapCollection<Group>();
    }
    
    @Provides
    public DatabaseCollection<Slot> provideSlotCollection() {
        return new HashMapCollection<Slot>();
    }

}
