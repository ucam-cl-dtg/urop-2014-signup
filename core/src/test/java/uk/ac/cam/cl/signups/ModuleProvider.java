/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups;

import uk.ac.cam.cl.signups.database.DatabaseModule;

import com.google.inject.AbstractModule;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class ModuleProvider {
    
    public static AbstractModule provide() {
        return new DatabaseModule();
    }
    
    public static boolean usingMongo() {
        return provide().getClass() == DatabaseModule.class;
    }
    
}
