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
    
    @JsonProperty("_id")
    private String _id;
    private String groupAuthCode;
    
    @JsonIgnore
    public Group(String _id) {
        this._id = _id;
        this.groupAuthCode = generateAuthCode();
    }
    
    public Group() {
    }
    
    @JsonCreator
    public Group (
            @JsonProperty("_id")            String _id,
            @JsonProperty("groupAuthCode")  String groupAuthCode
            ) {
        this._id = _id;
        this.groupAuthCode = groupAuthCode;
    }
    
    @JsonProperty("groupAuthCode")
    public void setGroupAuthCode(String groupAuthCode) {
        this.groupAuthCode = groupAuthCode;
    }
    
    @JsonProperty("groupAuthCode")
    public String getGroupAuthCode() {
        return groupAuthCode;
    }
    
    @JsonIgnore
    public boolean isGroupAuthCode(String code) {
        return code.equals(groupAuthCode);
    }
    
    @Override @Id
    public String get_id() {
        return _id;
    }
    
    @Id
    public void set_id(String _id) {
        this._id = _id;
    }
        
    @JsonIgnore
    private String generateAuthCode() {  // random string
        return new BigInteger(130, new SecureRandom()).toString(32);
    }
    
    @Override
    public String toString() {
        return "_id: " + _id + " authCode: " + groupAuthCode;
    }

    @Override
    public int hashCode() { // generated by eclipse
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_id == null) ? 0 : _id.hashCode());
        result = prime * result
                + ((groupAuthCode == null) ? 0 : groupAuthCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) { // generated by eclipse
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Group other = (Group) obj;
        if (_id == null) {
            if (other._id != null)
                return false;
        } else if (!_id.equals(other._id))
            return false;
        if (groupAuthCode == null) {
            if (other.groupAuthCode != null)
                return false;
        } else if (!groupAuthCode.equals(other.groupAuthCode))
            return false;
        return true;
    }
    
    

}
