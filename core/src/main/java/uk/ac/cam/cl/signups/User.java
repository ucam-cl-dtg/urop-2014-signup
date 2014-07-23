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

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class User {
    
    private String CRSID;
    private Map<String, String> groupToColumnMap;
    private Collection<String> allowedComments;
    
    public User(String CRSID) {
        this.CRSID = CRSID;
        groupToColumnMap = new HashMap<String, String>();
        allowedComments = new LinkedList<String>();
    }
    
    public String getCRSID() {
        return CRSID;
    }
    
    public String allowedColumnOf(String groupID) {
        return groupToColumnMap.get(groupID);
    }
    
    public boolean isAllowedcomment(String comment) {
        return allowedComments.contains(comment);
    }

}
