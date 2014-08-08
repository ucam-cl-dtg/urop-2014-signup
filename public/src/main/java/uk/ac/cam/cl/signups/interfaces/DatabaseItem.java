/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.interfaces;

import org.mongojack.Id;

/**
 * @author ird28
 *
 */
public interface DatabaseItem {
    
    @Id
    public String get_id();

}