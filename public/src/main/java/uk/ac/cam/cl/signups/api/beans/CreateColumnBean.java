/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api.beans;

import java.util.Date;

import com.fasterxml.jackson.annotation.*;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class CreateColumnBean {
    
    private String columnName;
    private String authCode;
    private Date startTime;
    private Date endTime;
    private int slotLengthInMinutes;
    
    @JsonCreator
    public CreateColumnBean(
            @JsonProperty("columnName") String columnName,
            @JsonProperty("authCode")   String authCode,
            @JsonProperty("startTime")  Date startTime,
            @JsonProperty("endTime")    Date endTime,
            @JsonProperty("slotLengthInMinutes") int slotLengthInMinutes) {
        this.columnName = columnName;
        this.authCode = authCode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotLengthInMinutes = slotLengthInMinutes;
    }

    @JsonProperty("columnName")
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

    public int getSlotLengthInMinutes() {
        return slotLengthInMinutes;
    }

        

}
