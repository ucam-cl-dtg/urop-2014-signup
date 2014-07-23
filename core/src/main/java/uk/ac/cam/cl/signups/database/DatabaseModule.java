/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.database;

import org.mongojack.JacksonDBCollection;

import uk.ac.cam.cl.signups.User;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.database.Mongo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class DatabaseModule extends AbstractModule {
    
    @Override
    protected void configure() {
        bind(DatabaseCollection.class).to(MongoCollection.class);
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

}
