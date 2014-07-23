/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.ac.cam.cl.signups.interfaces.DatabaseItem;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class Sheet implements DatabaseItem {
    
    private List<Column> columns;
    private String authCode;
    private String sheetID;
    private String groupID;
    private String _id;
    
    private String title;
    private String description;
    private String location;
    
    public Sheet(String title, String description, String location,
            Collection<Column> columnCollection, String groupID) {
        this.title = title;
        this.description = description;
        this.location = location;
        columns = new LinkedList<Column>(columnCollection);
        this.groupID = groupID;
        /*
         * TODO: generate URL and authCode
         */
    }
    
    public void addColumn(Column column) {
        columns.add(column);
    }
    
    public Column getColumn(String columnName) throws ItemNotFoundException {
        for (Column col : columns) {
            if (col.getName().equals(columnName)) {
                return col;
            }
        }
        throw new ItemNotFoundException("The column was not found in the sheet");
    }
    
    public List<Column> listColumns() {
        return columns;
    }
    
    public void removeColumn(String columnName) {
        columns.remove(columnName);
    }
    
    public boolean isAuthCode(String code) {
        return code.equals(authCode);
    }
    
    protected String getAuthCode() {
        return authCode;
    }
    
    public String getURL() {
        /* TODO: implement */
        return "Needs to be implemented";
    }
    
    public String getName() { /* Used to ensure uniqueness in database */
        return sheetID;
    }
    
    public String get_id() { /* Used by jackson */
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }
 
}
