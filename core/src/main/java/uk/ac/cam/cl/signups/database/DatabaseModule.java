/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.database;

import org.mongojack.JacksonDBCollection;

import uk.ac.cam.cl.git.database.Mongo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class DatabaseModule extends AbstractModule {
    
    @Override
    protected void configure() {
        
    }
    
    @Provides
    public JacksonDBCollection<Sheet, String> provideMongoCollection() {
        return JacksonDBCollection.wrap
                ( Mongo.getDB().getCollection("sheets")
                        , Repository.class
                        , String.class);
    }

}
