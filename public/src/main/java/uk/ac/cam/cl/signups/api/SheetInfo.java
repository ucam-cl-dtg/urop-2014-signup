/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Used to return the three Strings from a method call.
 * @author ird28
 */
public class SheetInfo {
    
    private String authCode;
    private String sheetID;
    
    @JsonIgnore
    public SheetInfo(Sheet sheet) {
        authCode = sheet.getAuthCode();
        sheetID = sheet.get_id();
    }
    
    @JsonCreator
    public SheetInfo(@JsonProperty("code") String authCode,
            @JsonProperty("sheetID") String sheetID) {
        this.authCode = authCode;
        this.sheetID = sheetID;
    }

    @JsonProperty("code")
    public String getAuthCode() {
        return authCode;
    }
    
    @JsonProperty("sheetID")
    public String getSheetID() {
        return sheetID;
    }
}
