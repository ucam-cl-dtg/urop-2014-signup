/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api.beans;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class GroupSheetBean {
    
    String sheetID;
    String groupAuthCode;
    String sheetAuthCode;
    
    public GroupSheetBean(String sheetID, String groupAuthCode,
            String sheetAuthCode) {
        this.sheetID = sheetID;
        this.groupAuthCode = groupAuthCode;
        this.sheetAuthCode = sheetAuthCode;
    }
    public String getSheetID() {
        return sheetID;
    }
    public String getGroupAuthCode() {
        return groupAuthCode;
    }
    public String getSheetAuthCode() {
        return sheetAuthCode;
    }
    
    

}
