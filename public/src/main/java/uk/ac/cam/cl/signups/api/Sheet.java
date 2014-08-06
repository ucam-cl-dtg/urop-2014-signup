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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.*;

import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.interfaces.DatabaseItem;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class Sheet implements DatabaseItem {
    
    private List<Column> columns;
    private String authCode;
    @JsonProperty("_id")
    private String sheetID;
    
    private String title;
    private String description;
    private String location;
    
    private List<Group> groups;
    
    private static SecureRandom random = new SecureRandom();
    private static MessageDigest digester;
    static {
        try {
            digester = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    @JsonIgnore
    public Sheet(String title, String description, String location) {
        this.title = title;
        this.description = description;
        this.location = location;
        columns = new LinkedList<Column>();
        groups = new LinkedList<Group>();
        sheetID = generateSheetID();
        authCode = generateAuthCode();
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
        , @JsonProperty("_id")          String sheetID
        , @JsonProperty("title")        String title
        , @JsonProperty("description")  String description
        , @JsonProperty("location")     String location
        , @JsonProperty("groups")       List<Group> groups
        )
    {
        this.columns = columns;
        this.authCode = authCode;
        this.sheetID = sheetID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.groups = groups;
        this.sheetID = sheetID;
    }
    
    @JsonIgnore
    public void addColumn(Column column) throws DuplicateNameException {
        for (Column col : columns) {
            if (col.getName().equals(column.getName())) {
                throw new DuplicateNameException(column.getName());
            }
        }
        columns.add(column);
        Collections.sort(columns);
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
    public void removeGroup(String groupID) throws ItemNotFoundException {
        Group toRemove = null;
        for (Group g : groups) {
            if (g.getID().equals(groupID)) {
                toRemove = g;
            }
        }
        if (toRemove == null) {
            throw new ItemNotFoundException("No group with the given id was found");
        }
        groups.remove(toRemove);
    }
    
    @JsonIgnore
    public boolean isPartOfGroup(String groupID) {
        for (Group g : groups) {
            if (g.getID().equals(groupID)) {
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
    
    //@Id @ObjectId @JsonProperty("_id")
    public String getID() { /* Used to ensure uniqueness in database */
        return sheetID;
    }
    
    //@Id @ObjectId @JsonProperty("_id")
    public void setID(String sheetID) { /* Used by mongojack */
        this.sheetID = sheetID;
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
                + "\nsheetID: " + sheetID + "\ntitle: " + title
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sheetID == null) ? 0 : sheetID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Sheet other = (Sheet) obj;
        if (sheetID == null) {
            if (other.sheetID != null)
                return false;
        } else if (!sheetID.equals(other.sheetID))
            return false;
        return true;
    }

     
    
     
}
