/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.interfaces;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ird28
 *
 */
public interface DatabaseItem {
    
    @JsonProperty("name")
    public String getName();
    
    @Id @ObjectId
    public String get_id();

}
