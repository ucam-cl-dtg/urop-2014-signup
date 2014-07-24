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

public class SlotBookingBean {
    
    /* null represents unbooked */
    
    private String currentlyBookedUser;
    private String userToBook;
    private String comment;
    private String authCode;
    
    /**
     * If booking a slot, currentlyBookedUser should be null (although this
     * actually doesn't matter).
     * If unbooking a slot, then clearly userToBook should be null.
     * @param currentlyBookedUser
     * @param userToBook
     * @param comment
     */
    public SlotBookingBean(String currentlyBookedUser,
            String userToBook, String comment) {
        this.currentlyBookedUser = currentlyBookedUser;
        this.userToBook = userToBook;
        this.comment = comment;
        this.authCode = null;
    }
    
    /**
     * It doesn't actually matter what currentlyBookedUser is set to.
     * @param currentlyBookedUser
     * @param userToBook
     * @param comment
     * @param authCode
     */
    public SlotBookingBean(String currentlyBookedUser,
            String userToBook, String comment, String authCode) {
        this.currentlyBookedUser = currentlyBookedUser;
        this.userToBook = userToBook;
        this.comment = comment;
        this.authCode = authCode;
    }
    
    public String getCurrentlyBookedUser() {
        return currentlyBookedUser;
    }

    public String getUserToBook() {
        return userToBook;
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
