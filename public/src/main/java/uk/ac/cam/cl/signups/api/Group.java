/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import java.util.ArrayList;
import java.util.List;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import com.fasterxml.jackson.annotation.*;

import uk.ac.cam.cl.signups.interfaces.DatabaseItem;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class Group implements DatabaseItem {
    
    private List<Sheet> sheets;
    private String name;
    private String groupAuthCode;
    private String _id;
    
    @JsonIgnore
    public Group (
            List<Sheet> sheets,
            String name,
            String groupAuthCode
            ) {
        this.sheets = sheets;
        this.name = name;
        this.groupAuthCode = "TODO: generate";
    }
    
    @JsonCreator
    public Group (
            @JsonProperty("sheets")         List<Sheet> sheets,
            @JsonProperty("name")           String name,
            @JsonProperty("groupAuthCode")  String groupAuthCode,
            @JsonProperty("_id")            String _id
            ) {
        this.sheets = sheets;
        this.name = name;
        this.groupAuthCode = groupAuthCode;
        this._id = _id;
    }
    
    @JsonIgnore
    public Group(String name) {
        this.name = name;
        this.sheets = new ArrayList<Sheet>();
        this.groupAuthCode = "TODO: generate";
    }
    
    /* TODO: generate groupAuthCode */
    
    @JsonIgnore
    public void addSheet(Sheet sheet) {
        sheets.add(sheet);
    }
    
    @JsonIgnore
    public void removeSheet(Sheet sheet) {
        sheets.remove(sheet);
    }
    
    @JsonProperty("groupAuthCode")
    protected String getGroupAuthCode() {
        return groupAuthCode;
    }
    
    @JsonIgnore
    public boolean isGroupAuthCode(String code) {
        return code.equals(groupAuthCode);
    }
    
    @JsonIgnore
    public List<String> getSheetIDs() {
        List<String> toReturn = new ArrayList<String>();
        for (Sheet sheet : sheets) {
            toReturn.add(sheet.getName());
        }
        return toReturn;
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
    
    @JsonProperty("sheets")
    public List<Sheet> getSheets() {
        return this.sheets;
    }

}
