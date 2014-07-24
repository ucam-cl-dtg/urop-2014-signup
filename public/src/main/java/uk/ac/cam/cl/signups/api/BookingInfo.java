/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;


/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class BookingInfo {
    
    private String user;
    private String comment;
    
    public BookingInfo(Slot slot) {
        this.user = slot.getBookedUser();
        this.comment = slot.getComment();
    }

    public String getUser() {
        return user;
    }

    public String getComment() {
        return comment;
    }
    
}
