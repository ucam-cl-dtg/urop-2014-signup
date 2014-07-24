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

    public Column getColumn() {
        return column;
    }

    public String getAuthCode() {
        return authCode;
    }

}
