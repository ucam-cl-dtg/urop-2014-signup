/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api.beans;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */

//TODO: check this is sufficient info
public class SlotBookingBean {
    
    private String user;
    private String comment;
    private String authCode;

    public SlotBookingBean(String user, String comment) {
        this.user = user;
        this.comment = comment;
        this.authCode = null;
    }
    
    public SlotBookingBean(String user, String comment, String authCode) {
        this.user = user;
        this.comment = comment;
        this.authCode = authCode;
    }
    
    public String getUser() {
        return user;
    }

    public String getComment() {
        return comment;
    }
    
    public String getAuthCode() {
        return authCode;
    }
    
    public boolean authCodeProvided() {
        return authCode != null;
    }

}
