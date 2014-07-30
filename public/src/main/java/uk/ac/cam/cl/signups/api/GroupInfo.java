/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kind of pointless class - used to contain
 * more information than this. Used to return
 * the group authorisation code from a method
 * call.
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class GroupInfo {
    
    private String groupAuthCode;
    
    @JsonCreator
    public GroupInfo(@JsonProperty("code") Group g) {
        groupAuthCode = g.getGroupAuthCode();
    }
    
    @JsonProperty("code")
    public String getAuthCode() {
        return groupAuthCode;
    }

}
