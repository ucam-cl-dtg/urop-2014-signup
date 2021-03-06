/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api.beans;

import java.util.Map;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class AddPermissionsBean {
    
    Map<String, String> commentColumnMap;
    String groupAuthCode;
    
    public AddPermissionsBean() {}
    
    public AddPermissionsBean(Map<String, String> commentColumnMap,
            String groupAuthCode) {
        this.commentColumnMap = commentColumnMap;
        this.groupAuthCode = groupAuthCode;
    }
    
    public Map<String, String> getCommentColumnMap() {
        return commentColumnMap;
    }
    
    public String getGroupAuthCode() {
        return groupAuthCode;
    }

    public void setCommentColumnMap(Map<String, String> commentColumnMap) {
        this.commentColumnMap = commentColumnMap;
    }

    public void setGroupAuthCode(String groupAuthCode) {
        this.groupAuthCode = groupAuthCode;
    }
    
    

}
