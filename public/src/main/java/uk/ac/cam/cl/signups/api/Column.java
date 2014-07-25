/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import com.fasterxml.jackson.annotation.*;

import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class Column {
    
    private String name;
    private List<Slot> slots;
    private String _id;

    @JsonIgnore
    public Column(String name, List<Slot> slots) {
        this.name = name;
        this.slots = slots;
    }
    
    @JsonCreator
    public Column(
            @JsonProperty("name")   String name,
            @JsonProperty("slots")  List<Slot> slots,
            @JsonProperty("_id")    String _id
            ) {
        this.name = name;
        this.slots = slots;
        this._id = _id;
    }
    
    @JsonIgnore
    public void addSlot(Slot slot) {
        slots.add(slot);
        Collections.sort(slots);
    }
    
    @JsonIgnore
    public Slot getSlot(Date startTime) throws ItemNotFoundException {
        Iterator<Slot> it = slots.iterator();
        while (it.hasNext()) {
            Slot slot = it.next();
            if (startTime.equals(slot.getStartTime())) {
                return slot;
            }
        }
        throw new ItemNotFoundException("Slot doesn't exist in this column");
    }
    
    @JsonIgnore
    public void removeSlot(Date startTime) {
        Iterator<Slot> it = slots.iterator();
        while (it.hasNext()) {
            Slot slot = it.next();
            if (startTime.equals(slot.getStartTime())) {
                slots.remove(slot);
            }
        }
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("slots")
    public List<Slot> getSlots() {
        return slots;
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
        return "Name: " + name + " Slots: " + slots;
    }

    @Override @JsonIgnore
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((slots == null) ? 0 : slots.hashCode());
        return result;
    }

    @Override @JsonIgnore
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Column other = (Column) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (slots == null) {
            if (other.slots != null)
                return false;
        } else if (! ((Collection<Slot>) slots).equals((Collection<Slot>) other.slots))
            return false;
        return true;
    }
    
       
    
    
}
