/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.database;

import org.mongojack.JacksonDBCollection;

import uk.ac.cam.cl.signups.SignupService;
import uk.ac.cam.cl.signups.User;
import uk.ac.cam.cl.signups.api.Group;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.database.Mongo;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class DatabaseModule extends AbstractModule {
    
    /*
     * FIXME: Guice doesn't work with Generics the way I assumed it would.
     *  TODO: Make it work, somehow. Look into the TypeLiteral class.
     */
    
    @Override
    protected void configure() {
        bind(DatabaseCollection.class).to(MongoCollection.class);
        bind(WebInterface.class).to(SignupService.class);
    }
    
    @Provides
    public JacksonDBCollection<Sheet, String> provideSheetCollection() {
        return JacksonDBCollection.wrap
                ( Mongo.getDB().getCollection("sheets")
                        , Sheet.class
                        , String.class);
    }
    
    @Provides
    public JacksonDBCollection<User, String> provideUserCollection() {
        return JacksonDBCollection.wrap
                ( Mongo.getDB().getCollection("users")
                        , User.class
                        , String.class);
    }
    
    @Provides
    public JacksonDBCollection<Group, String> provideGroupCollection() {
        return JacksonDBCollection.wrap
                ( Mongo.getDB().getCollection("groups")
                        , Group.class
                        , String.class);
    }

}
