/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

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

    public String getName() {
        return name;
    }

    public List<Slot> getSlots() {
        return slots;
    }
    
}
