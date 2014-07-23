/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

/**
 * @author ird28
 *
 */
public class SlotBookingBean {
    
    private String user;
    private String comment;
    
    public SlotBookingBean(String user, String comment) {
        this.user = user;
        this.comment = comment;
    }

    public String getUser() {
        return user;
    }

    public String getComment() {
        return comment;
    }

}
