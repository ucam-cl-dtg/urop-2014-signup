/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import java.util.List;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class Group {
    
    private List<Sheet> sheets;
    
    public void addSheet(Sheet sheet) {
        sheets.add(sheet);
    }

}
