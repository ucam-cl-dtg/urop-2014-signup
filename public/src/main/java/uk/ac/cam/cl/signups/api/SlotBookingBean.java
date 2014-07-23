/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import java.util.Date;

/**
 * @author ird28
 *
 */
public class SlotBookingBean {
    
    private String sheetID;
    private String columnName;
    private Date startTime;
    private String user;
    private String comment;

    public SlotBookingBean(String sheetID, String columnName, Date startTime,
            String user, String comment) {
        this.sheetID = sheetID;
        this.columnName = columnName;
        this.startTime = startTime;
        this.user = user;
        this.comment = comment;
    }

    public String getSheetID() {
        return sheetID;
    }

    public String getColumnName() {
        return columnName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getUser() {
        return user;
    }

    public String getComment() {
        return comment;
    }

    

}
