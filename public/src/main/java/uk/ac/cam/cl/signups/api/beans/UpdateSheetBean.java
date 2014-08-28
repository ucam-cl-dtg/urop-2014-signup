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
    
    private String title;
    private String location;
    private String description;
    private String authCode;
    
    public UpdateSheetBean(String title, String location, String description,
            String authCode) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.authCode = authCode;
    }
    
    public UpdateSheetBean() {
        // Default constructor
    }
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getAuthCode() {
        return authCode;
    }
    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
    
}
