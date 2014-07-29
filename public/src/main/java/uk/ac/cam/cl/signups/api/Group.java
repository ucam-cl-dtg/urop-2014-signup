/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import com.fasterxml.jackson.annotation.*;

import uk.ac.cam.cl.signups.interfaces.DatabaseItem;

/**
 * Represents a "group" of sheets. Access to sheets is done
 * by group. For example, all the ticking session sheets for
 * one course should be grouped so that if a student has permission
 * to sign up for a tick one week, they have the same permission for
 * all other weeks.
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class Group implements DatabaseItem {
    
    private String name;
    private String groupAuthCode;
    private String _id;
    
    @JsonIgnore
    public Group(String name) {
        this.name = name;
        this.groupAuthCode = generateAuthCode();
    }
    
    @JsonCreator
    public Group (
            @JsonProperty("sheets")         List<Sheet> sheets,
            @JsonProperty("name")           String name,
            @JsonProperty("groupAuthCode")  String groupAuthCode,
            @JsonProperty("_id")            String _id
            ) {
        this.name = name;
        this.groupAuthCode = groupAuthCode;
        this._id = _id;
    }
    
    @JsonProperty("groupAuthCode")
    protected String getGroupAuthCode() {
        return groupAuthCode;
    }
    
    @JsonIgnore
    public boolean isGroupAuthCode(String code) {
        return code.equals(groupAuthCode);
    }
    

    @Override @JsonProperty("name")
    public String getName() {
       return name;
    }

    @Override @Id @ObjectId
    public String get_id() {
        return _id;
    }
    
    @Id @ObjectId
    public void set_id(String _id) {
        this._id = _id;
    }
        
    @JsonIgnore
    private String generateAuthCode() {
        return new BigInteger(130, new SecureRandom()).toString(32);
    }
    
    @Override
    public String toString() {
        return "Name: " + name + " authCode: " + groupAuthCode;
    }

}
