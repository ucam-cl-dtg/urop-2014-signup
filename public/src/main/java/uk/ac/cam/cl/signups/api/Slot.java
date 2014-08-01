/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import java.util.Date;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import uk.ac.cam.cl.signups.interfaces.DatabaseItem;

import com.fasterxml.jackson.annotation.*;

/**
 * This is what a user signs up for - a specific period
 * in time in a certain column, with a comment of their
 * choosing. In the context of ticking, the comment
 * refers to the tick to be marked.
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class Slot implements Comparable<Slot>, DatabaseItem {
    
    private Date startTime;
    private long duration; /* in milliseconds! */
    private String bookedUser; /* null if unbooked */
    private String comment;
    private String _id;
    
    /**
     * Creates a new unbooked slot.
     * @param startTime The time the slot starts
     * @param duration The length of the slot - in milliseconds!
     */
    @JsonIgnore
    public Slot(Date startTime, long duration) {
        this.startTime = startTime;
        this.duration = duration;
        this.bookedUser = null;
        this.comment = null;
    }
    
    @JsonIgnore
    public Slot(Date startTime, long duration, String user, String comment) {
        this.startTime = startTime;
        this.duration = duration;
        this.bookedUser = user;
        this.comment = comment;
    }
    
    @JsonCreator
    public Slot(
            @JsonProperty("startTime")  Date startTime,
            @JsonProperty("duration")   long duration,
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
    public String getID() {
        return _id;
    }

    @Id @ObjectId
    public void setID(String _id) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((bookedUser == null) ? 0 : bookedUser.hashCode());
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + (int) (duration ^ (duration >>> 32));
        result = prime * result
                + ((startTime == null) ? 0 : startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Slot other = (Slot) obj;
        if (bookedUser == null) {
            if (other.bookedUser != null)
                return false;
        } else if (!bookedUser.equals(other.bookedUser))
            return false;
        if (comment == null) {
            if (other.comment != null)
                return false;
        } else if (!comment.equals(other.comment))
            return false;
        if (duration != other.duration)
            return false;
        if (startTime == null) {
            if (other.startTime != null)
                return false;
        } else if (!startTime.equals(other.startTime))
            return false;
        return true;
    }
    
    

}
