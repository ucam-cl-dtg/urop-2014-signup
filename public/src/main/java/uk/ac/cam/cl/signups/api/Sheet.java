/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import java.util.Collection;

/**
 * @author ird28
 *
 */
public class Sheet {
    
    private Collection<Column> columns;
    private String authCode;
    private String URL;
    private String groupID;
    
    private String title;
    private String description;
    private String location;
    
    public Sheet(String title, String description, String location,
            Collection<Column> columns, String groupID) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.columns = columns;
        this.groupID = groupID;
        /*
         * TODO: generate URL and authCode
         */
    }

    public Collection<Column> getColumns() {
        return columns;
    }

    public String getURL() {
        return URL;
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
