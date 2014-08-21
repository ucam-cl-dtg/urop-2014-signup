/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api.beans;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class BatchDeleteBean {
    
    private Long time;
    private String authCode;
    
    
    public BatchDeleteBean(Long time, String authCode) {
        this.time = time;
        this.authCode = authCode;
    }

    public BatchDeleteBean() {
        // Default constructor
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
    

}
