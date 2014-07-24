/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups;

import java.util.Map;
import java.util.HashMap;

import uk.ac.cam.cl.signups.interfaces.DatabaseItem;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class User implements DatabaseItem {
    
    private String name;
    private Map<String, HashMap<String, String>> groupToCommentToColumnMap;
    private String _id;
    
    public User(String name) {
        this.name = name;
        groupToCommentToColumnMap = new HashMap<String, HashMap<String, String>>();
    }
    
    public Map<String, String> getCommentColumnMap(String groupName) {
        return groupToCommentToColumnMap.get(groupName);
    }
        
    public String getName() {
        return name;
    }

    public String get_id() {
        return _id;
    }

}
