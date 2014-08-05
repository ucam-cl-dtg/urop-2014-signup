/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api.beans;

import uk.ac.cam.cl.signups.api.Column;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class ColumnBean {
    
    private Column column;
    private String authCode;
    
    public ColumnBean(Column column, String authCode) {
        this.column = column;
        this.authCode = authCode;
    }

    public ColumnBean() {
    }

    public Column getColumn() {
        return column;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
    
    

}
