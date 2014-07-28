/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups;

import java.util.Map;
import java.util.HashMap;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import com.fasterxml.jackson.annotation.*;

import uk.ac.cam.cl.signups.interfaces.DatabaseItem;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class User implements DatabaseItem {
    
    private String name;
    private Map<String, HashMap<String, String>> groupToCommentToColumnMap;
    private String _id;
    
    @JsonIgnore
    public User(String name) {
        this.name = name;
        groupToCommentToColumnMap = new HashMap<String, HashMap<String, String>>();
    }
    
    @JsonCreator
    public User(
            @JsonProperty("name")   String name,
            @JsonProperty("map")    Map<String, HashMap<String, String>> map,
            @JsonProperty("_id")    String _id
            ) {
        this.name = name;
        this.groupToCommentToColumnMap = map;
        this._id = _id;
    }
    
    @JsonIgnore
    public Map<String, String> getCommentColumnMap(String groupName) {
        HashMap<String, String> commentToColumnMap = groupToCommentToColumnMap.get(groupName);
        if (commentToColumnMap == null) {
            commentToColumnMap = new HashMap<String, String>();
            groupToCommentToColumnMap.put(groupName, commentToColumnMap);
        }
        return commentToColumnMap;
    }
    
    @JsonProperty("name")
    public String getName() {
        return name;
    }
    
    @JsonProperty("map")
    public Map<String, HashMap<String, String>> getGroupToCommentToColumnMap() {
        return groupToCommentToColumnMap;
    }

    @Id @ObjectId
    public String get_id() {
        return _id;
    }
    
    @Id @ObjectId
    public void set_id(String _id) {
        this._id = _id;
    }

}
