/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api;

import java.util.ArrayList;
import java.util.List;

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
    
    /* TODO: generate groupAuthCode */
    
    public Group(String name) {
        this.name = name;
    }
    
    public void addSheet(Sheet sheet) {
        sheets.add(sheet);
    }
    
    public void removeSheet(Sheet sheet) {
        sheets.remove(sheet);
    }
    
    protected String getGroupAuthCode() {
        return groupAuthCode;
    }
    
    public boolean isGroupAuthCode(String code) {
        return code.equals(groupAuthCode);
    }
    
    public List<String> getSheetIDs() {
        List<String> toReturn = new ArrayList<String>();
        for (Sheet sheet : sheets) {
            toReturn.add(sheet.getName());
        }
        return toReturn;
    }

    @Override
    public String getName() {
       return name;
    }

    @Override
    public String get_id() {
        return _id;
    }

}
