/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api.beans;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class BatchCreateBean {
    
    private Long startTime;
    private Long endTime;
    private int slotLength;
    private String authCode;
    
    
    public BatchCreateBean(Long startTime, Long endTime, int slotLength,
            String authCode) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotLength = slotLength;
        this.authCode = authCode;
    }
    
    public BatchCreateBean() {
        // Default constructor
    }


    public Long getStartTime() {
        return startTime;
    }


    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }


    public Long getEndTime() {
        return endTime;
    }


    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }


    public int getSlotLength() {
        return slotLength;
    }


    public void setSlotLength(int slotLength) {
        this.slotLength = slotLength;
    }


    public String getAuthCode() {
        return authCode;
    }


    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
    

}
