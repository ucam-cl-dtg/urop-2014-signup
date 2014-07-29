/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import com.fasterxml.jackson.annotation.*;

import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.interfaces.DatabaseItem;

/**
 * Represents one signup "session".
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class Sheet implements DatabaseItem {
    
    private List<Column> columns;
    private String authCode;
    private String sheetID;
    private String _id;
    
    private String title;
    private String description;
    private String location;
    
    private List<Group> groups;
    
    private SecureRandom random = new SecureRandom();
    private MessageDigest digester;
    {
        try {
            digester = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    @JsonIgnore
    public Sheet(String title, String description, String location,
            Collection<Column> columnCollection) {
        
        this.title = title;
        this.description = description;
        this.location = location;
        columns = new LinkedList<Column>(columnCollection);
        groups = new LinkedList<Group>();
        sheetID = generateSheetID();
        authCode = generateAuthCode();
        
        /*
         * TODO: generate URL
         */
    }
    
    @JsonCreator
    public Sheet
        ( @JsonProperty("columns")      List<Column> columns
        , @JsonProperty("authCode")     String authCode
        , @JsonProperty("name")         String sheetID
        , @JsonProperty("title")        String title
        , @JsonProperty("description")  String description
        , @JsonProperty("location")     String location
        , @JsonProperty("groups")       List<Group> groups
        , @JsonProperty("_id")          String _id
        )
    {
        this.columns = columns;
        this.authCode = authCode;
        this.sheetID = sheetID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.groups = groups;
        this._id = _id;
    }
    
    @JsonIgnore
    public void addColumn(Column column) {
        columns.add(column);
    }
    
    @JsonIgnore
    public Column getColumn(String columnName) throws ItemNotFoundException {
        for (Column col : columns) {
            if (col.getName().equals(columnName)) {
                return col;
            }
        }
        throw new ItemNotFoundException("The column was not found in the sheet");
    }
    
    @JsonProperty("columns")
    public List<Column> getColumns() {
        return columns;
    }
    
    @JsonIgnore
    public void removeColumn(String columnName) throws ItemNotFoundException {
        Column toRemove = getColumn(columnName);
        columns.remove(toRemove);
    }
    
    @JsonIgnore
    public boolean isAuthCode(String code) {
        return code.equals(authCode);
    }
    
    @JsonProperty("authCode")
    protected String getAuthCode() {
        return authCode;
    }
    
    @JsonIgnore
    public void addGroup(Group group) {
        groups.add(group);
    }
    
    @JsonIgnore
    public void removeGroup(String groupName) throws ItemNotFoundException {
        Group toRemove = null;
        for (Group g : groups) {
            if (g.getName().equals(groupName)) {
                toRemove = g;
            }
        }
        if (toRemove == null) {
            throw new ItemNotFoundException("No group with the given name was found");
        }
        groups.remove(toRemove);
    }
    
    @JsonIgnore
    public boolean isPartOfGroup(String groupName) {
        for (Group g : groups) {
            if (g.getName().equals(groupName)) {
                return true;
            }
        }
        return false;
    }
    
    @JsonProperty("groups")
    public List<Group> getGroups() {
        return groups;
    }
    
    @JsonIgnore
    public String getURL() {
        /* TODO: implement */
        return "Needs to be implemented";
    }
    
    @JsonProperty("name")
    public String getName() { /* Used to ensure uniqueness in database */
        return sheetID;
    }
    
    @Id @ObjectId
    public String get_id() { /* Used by mongojack */
        return _id;
    }
    
    @Id @ObjectId
    public void set_id(String _id) { /* Used by mongojack */
        this._id = _id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @Override @JsonIgnore
    public String toString() {
        return "columns: " + columns + "\nauthCode: " + authCode
                + "\nsheetID: " + sheetID + "\n_id: " + _id + "\ntitle: " + title
                + "\ndescription: " + description + "\nlocation: " + location
                + "\ngroups: " + groups + "\n";
    }
    
    @JsonIgnore
    private String generateSheetID() {
        digester.reset();
        digester.update(title.getBytes());
        digester.update(description.getBytes());
        digester.update(location.getBytes());
        byte[] bytes = digester.digest();
        StringBuilder out = new StringBuilder();
        for (byte b : bytes) {
            out.append(String.format("%02x", b));
        }
        return out.toString().substring(12);
    }
    
    @JsonIgnore
    private String generateAuthCode() {
        return new BigInteger(130, random).toString(32);
    }
     
}
