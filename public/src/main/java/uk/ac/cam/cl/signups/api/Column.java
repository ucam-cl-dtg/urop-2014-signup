/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author ird28
 *
 */
public class Column {
    
    private String name;
    private List<Slot> slots;

    public Column(String name, List<Slot> slots) {
        this.name = name;
        this.slots = slots;
    }
    
    public void addSlot(Slot slot) {
        slots.add(slot);
    }
    
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
    
    public void removeSlot(Date startTime) {
        Iterator<Slot> it = slots.iterator();
        while (it.hasNext()) {
            Slot slot = it.next();
            if (startTime.equals(slot.getStartTime())) {
                slots.remove(slot);
            }
        }
    }

    public String getName() {
        return name;
    }

    public List<Slot> getSlots() {
        return slots;
    }
    
}
