/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api.beans;

import uk.ac.cam.cl.signups.api.Sheet;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class UpdateSheetBean {
    
    private Sheet sheet;
    private String authCode;
    
    public UpdateSheetBean(Sheet sheet, String authCode) {
        this.sheet = sheet;
        this.authCode = authCode;
    }
    
    public UpdateSheetBean() {
        // Default constructor
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
    
    

}
