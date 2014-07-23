/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

/**
 * @author ird28
 *
 */
public class SheetInfo {
    
    private String URL;
    private String authCode;
    
    public SheetInfo(Sheet sheet) {
        URL = sheet.getURL();
        authCode = sheet.getAuthCode();
    }

    public String getURL() {
        return URL;
    }

    public String getAuthCode() {
        return authCode;
    }
}
