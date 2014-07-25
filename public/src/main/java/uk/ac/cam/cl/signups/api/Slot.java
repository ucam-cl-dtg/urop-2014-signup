/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import java.util.Date;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import com.fasterxml.jackson.annotation.*;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class Slot implements Comparable<Slot> {
    
    private Date startTime;
    private long duration; /* in milliseconds! */
    private String bookedUser; /* null if unbooked */
    private String comment;
    private String _id;
    
    @JsonIgnore
    public Slot(Date startTime, int duration) {
        this.startTime = startTime;
        this.duration = duration;
        this.bookedUser = null;
        this.comment = null;
    }
    
    @JsonIgnore
    public Slot(Date startTime, int duration, String user, String comment) {
        this.startTime = startTime;
        this.duration = duration;
        this.bookedUser = user;
        this.comment = comment;
    }
    
    @JsonCreator
    public Slot(
            @JsonProperty("startTime")  Date startTime,
            @JsonProperty("duration")   int duration,
            @JsonProperty("bookedUser")       String user,
            @JsonProperty("comment")    String comment,
            @JsonProperty("_id")        String _id
            ) {
        this.startTime = startTime;
        this.duration = duration;
        this.bookedUser = user;
        this.comment = comment;
    }

    @JsonProperty("startTime")
    public Date getStartTime() {
        return startTime;
    }

    @JsonProperty("duration")
    public long getDuration() {
        return duration;
    }

    @JsonProperty("bookedUser")
    public String getBookedUser() {
        return bookedUser;
    }

    @JsonProperty("comment")
    public String getComment() {
        return comment;
    }

    @JsonIgnore
    public boolean isBooked() {
        return bookedUser != null;
    }

    @JsonIgnore
    public void book(String user, String comment) {
        this.bookedUser = user;
        this.comment = comment;
    }

    @JsonIgnore
    public void unbook() {
        bookedUser = null;
        comment = null;
    }
    
    @Override @JsonIgnore
    public int compareTo(Slot other) {
        if (this.getStartTime().before(other.getStartTime())) {
            return -1;
        }
        else if (this.getStartTime().after(other.getStartTime())) {
            return +1;
        }
        else {
            return 0;
        }
    }

    @Id @ObjectId
    public String get_id() {
        return _id;
    }

    @Id @ObjectId
    public void set_id(String _id) {
        this._id = _id;
    }

    @Override @JsonIgnore
    public String toString() {
        String status = "Booked by " + bookedUser + " - " + comment;
        if (!isBooked()) {
            status = "Unbooked";
        }
        String length = (duration/60000) + " minutes " + ((duration/1000)%60) + " seconds";
        if ((duration/1000) % 60 == 0) {
            length = (duration/60000) + " minutes";
        }
        return "Start: " + startTime + " Length: " + length
                + " Status: " + status;
    }

}
