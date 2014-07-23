/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import java.util.Date;

/**
 * @author ird28
 *
 */
public class Slot {
    
    private Date startTime;
    private long duration; /* in milliseconds! */
    private String bookedUser; /* null if unbooked */
    private String comment;
    
    public Slot(Date startTime, int duration) {
        this.startTime = startTime;
        this.duration = duration;
        this.bookedUser = null;
        this.comment = null;
    }
    
    public Slot(Date startTime, int duration, String user, String comment) {
        this.startTime = startTime;
        this.duration = duration;
        this.bookedUser = user;
        this.comment = comment;
    }

    public Date getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public String getBookedUser() {
        return bookedUser;
    }

    public String getComment() {
        return comment;
    }
    
    

}
