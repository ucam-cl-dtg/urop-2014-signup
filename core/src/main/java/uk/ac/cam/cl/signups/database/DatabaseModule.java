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
import uk.ac.cam.cl.signups.api.Slot;
import uk.ac.cam.cl.signups.database.Mongo;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class DatabaseModule extends AbstractModule {
    
    private static final String SHEETCOLLECTION = "sheets";
    private static final String USERCOLLECTION = "users";
    private static final String GROUPCOLLECTION = "groups";
    private static final String SLOTCOLLECTION = "slots";
        
    @Override
    protected void configure() {
        bind(WebInterface.class).to(SignupService.class);
    }
    
    @Provides
    public DatabaseCollection<Sheet> provideSheetCollection() {
        MongoCollection<Sheet> toReturn = new MongoCollection<Sheet>();
        toReturn.setCollection(
                JacksonDBCollection.wrap(
                        Mongo.getDB().getCollection(SHEETCOLLECTION)
                        , Sheet.class
                        , String.class));
        return toReturn;
    }
    
    @Provides
    public DatabaseCollection<User> provideUserCollection() {
        MongoCollection<User> toReturn = new MongoCollection<User>();
        toReturn.setCollection(
                JacksonDBCollection.wrap(
                        Mongo.getDB().getCollection(USERCOLLECTION)
                        , User.class
                        , String.class));
        return toReturn;
    }
    
    @Provides
    public DatabaseCollection<Group> provideGroupCollection() {
        MongoCollection<Group> toReturn = new MongoCollection<Group>();
        toReturn.setCollection(
                JacksonDBCollection.wrap(
                        Mongo.getDB().getCollection(GROUPCOLLECTION)
                        , Group.class
                        , String.class));
        return toReturn;
    }
    
    @Provides
    public DatabaseCollection<Slot> provideSlotCollection() {
        MongoCollection<Slot> toReturn = new MongoCollection<Slot>();
        toReturn.setCollection(
                JacksonDBCollection.wrap(
                        Mongo.getDB().getCollection(SLOTCOLLECTION)
                        , Slot.class
                        , String.class));
        return toReturn;
    }

}
