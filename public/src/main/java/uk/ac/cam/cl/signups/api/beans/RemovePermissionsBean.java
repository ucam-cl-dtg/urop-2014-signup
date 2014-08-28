/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api.beans;

import java.util.List;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class RemovePermissionsBean {
    
    List<String> comments;
    String groupAuthCode;
    
    public RemovePermissionsBean() {
        // Default constructor
    }

    public RemovePermissionsBean(List<String> comments, String groupAuthCode) {
        this.comments = comments;
        this.groupAuthCode = groupAuthCode;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getGroupAuthCode() {
        return groupAuthCode;
    }

    public void setGroupAuthCode(String groupAuthCode) {
        this.groupAuthCode = groupAuthCode;
    }
    
}
