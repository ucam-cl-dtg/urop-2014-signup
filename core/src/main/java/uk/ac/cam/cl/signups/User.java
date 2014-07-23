/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import uk.ac.cam.cl.signups.interfaces.DatabaseItem;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class User implements DatabaseItem {
    
    private String name;
    private Map<String, String> groupToColumnMap;
    private Collection<String> allowedComments;
    private String _id;
    
    public User(String name) {
        this.name = name;
        groupToColumnMap = new HashMap<String, String>();
        allowedComments = new LinkedList<String>();
    }
    
    public String allowedColumnOf(String groupID) {
        return groupToColumnMap.get(groupID);
    }
    
    public boolean isAllowedcomment(String comment) {
        return allowedComments.contains(comment);
    }
    
    public String getName() {
        return name;
    }

    public String get_id() {
        return _id;
    }

}
