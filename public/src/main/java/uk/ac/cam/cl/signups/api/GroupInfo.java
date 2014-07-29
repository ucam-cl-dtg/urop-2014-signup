/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

/**
 * Kind of pointless class - used to contain
 * more information than this. Used to return
 * the group authorisation code from a method
 * call.
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class GroupInfo {
    
    private String groupAuthCode;
    
    public GroupInfo(Group g) {
        groupAuthCode = g.getGroupAuthCode();
    }
    
    public String getAuthCode() {
        return groupAuthCode;
    }

}
