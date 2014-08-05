/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api.beans;

import java.util.Date;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class CreateColumnBean {
    
    private String columnName;
    private String authCode;
    private Date startTime;
    private Date endTime;
    private int slotLength;
    
    public CreateColumnBean(
            String columnName,
            String authCode,
            Date startTime,
            Date endTime,
            int slotLength) {
        this.columnName = columnName;
        this.authCode = authCode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotLength = slotLength;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getAuthCode() {
        return authCode;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public int getSlotLength() {
        return slotLength;
    }

        

}
