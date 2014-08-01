/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import com.fasterxml.jackson.annotation.*;

import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;

/**
 * Represents one "column" of a sheet. In the context of ticks, each
 * column would correspond to a ticker.
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class Column implements Comparable<Column>{
    
    @Id
    private String name;
    private List<String> slotIDs;
    
    @JsonCreator
    public Column(
            @JsonProperty("_id")   String name,
            @JsonProperty("slotsIDs")  List<String> slotIDs
            ) {
        this.name = name;
        this.slotIDs = slotIDs;
    }
    
    @JsonIgnore
    public void addSlot(String slot) throws DuplicateNameException {
        if (slotIDs.contains(slot)) {
            throw new DuplicateNameException(slot);
        }
        slotIDs.add(slot);
        Collections.sort(slotIDs);
    }
    
    @JsonIgnore
    public void removeSlot(String slot) throws ItemNotFoundException {
        if (!slotIDs.contains(slot)) {
            throw new ItemNotFoundException(slot);
        }
        slotIDs.remove(slot);
    }

    @Id
    public String getName() {
        return name;
    }

    @JsonProperty("slots")
    public List<String> getSlotIDs() {
        return slotIDs;
    }

    @Override @JsonIgnore
    public String toString() {
        return "Name: " + name + " SlotIDs: " + slotIDs;
    }

    

    @Override @JsonIgnore
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((slotIDs == null) ? 0 : slotIDs.hashCode());
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
        if (slotIDs == null) {
            if (other.slotIDs != null)
                return false;
        } else if (!slotIDs.equals(other.slotIDs))
            return false;
        return true;
    }

    @Override
    public int compareTo(Column arg0) { // sort by name
        return this.name.compareTo(arg0.name);
    }
    
       
    
    
}
