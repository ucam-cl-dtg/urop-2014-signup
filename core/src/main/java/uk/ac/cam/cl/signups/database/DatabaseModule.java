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
        
    @Override
    protected void configure() {
        bind(WebInterface.class).to(SignupService.class);
    }
    
    @Provides
    public DatabaseCollection<Sheet> provideSheetCollection() {
        MongoCollection<Sheet> toReturn = new MongoCollection<Sheet>();
        toReturn.setCollection(
                JacksonDBCollection.wrap(
                        Mongo.getDB().getCollection("sheets")
                        , Sheet.class
                        , String.class));
        return toReturn;
    }
    
    @Provides
    public DatabaseCollection<User> provideUserCollection() {
        MongoCollection<User> toReturn = new MongoCollection<User>();
        toReturn.setCollection(
                JacksonDBCollection.wrap(
                        Mongo.getDB().getCollection("users")
                        , User.class
                        , String.class));
        return toReturn;
    }
    
    @Provides
    public DatabaseCollection<Group> provideGroupCollection() {
        MongoCollection<Group> toReturn = new MongoCollection<Group>();
        toReturn.setCollection(
                JacksonDBCollection.wrap(
                        Mongo.getDB().getCollection("groups")
                        , Group.class
                        , String.class));
        return toReturn;
    }

}
