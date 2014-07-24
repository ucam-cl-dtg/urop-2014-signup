/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api.beans;

import uk.ac.cam.cl.signups.api.Slot;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class SlotBean {
    
    private Slot slot;
    private String authCode;
    
    public SlotBean(Slot slot, String authCode) {
        this.slot = slot;
        this.authCode = authCode;
    }

    public Slot getSlot() {
        return slot;
    }

    public String getAuthCode() {
        return authCode;
    }

}
