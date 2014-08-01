/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.database;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mongojack.JacksonDBCollection;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;

import uk.ac.cam.cl.signups.api.Slot;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class MongoSlots implements DatabaseCollection<Slot> {
    
private JacksonDBCollection<Slot, String> collection;
    
    @Inject /* We are not actually injecting using Guice, we do it manually */
    protected void setCollection(JacksonDBCollection<Slot, String> collection) {
        this.collection = collection;
    }

    public void insertItem(Slot item) throws DuplicateNameException {
        try {
            collection.insert(item);
        } catch(com.mongodb.MongoException dupKey) {
            System.out.println("Should be duplicate key:");
            dupKey.printStackTrace();
            throw new DuplicateNameException(item.getID());
        }
    }

    public void updateItem(Slot item) throws ItemNotFoundException {
        if (!contains(item.getID()))
            throw new ItemNotFoundException("Slothe item " + item.getID()
                    + " of type " + item.getClass() + " was not found in the database");
        collection.updateById(item.getID(), item);
    }

    public List<Slot> listItems() {
        List<Slot> rtn = new LinkedList<Slot>();
        Iterator<Slot> allItems = collection.find();
        while (allItems.hasNext())
            rtn.add(allItems.next());
        return rtn;
    }

    public boolean contains(String id) {
        int matchingItems = collection.find(new BasicDBObject("_id", id)).count();
        assert (matchingItems == 0 || matchingItems == 1);
        return (matchingItems == 1);
    }

    public Slot getItem(String name) throws ItemNotFoundException {
        if (!contains(name))
            throw new ItemNotFoundException("Slothe item " + name +
                    " was not found in the database");
        return collection.findOneById(name);
    }
    
    public Slot getSlot(String sheetID, String columnName, Date startTime) throws ItemNotFoundException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("sheetID", sheetID);
        map.put("columnName", columnName);
        map.put("startTime", startTime);
        Slot toReturn = collection.findOne(new BasicDBObject(map));
        if (toReturn == null) {
            throw new ItemNotFoundException("Sheet " + sheetID +
                    " Column" + columnName + " Start Time " + startTime);
        }
        return toReturn;
        
    }

    public void removeAll() {
        collection.remove(new BasicDBObject());
    }

    public void removeItem(String id) throws ItemNotFoundException {
        if (!contains(id))
            throw new ItemNotFoundException("Slothe item " + id +
                    " was not found in the database");
        collection.removeById(id);
    }
    
    /**
     * @return ID of the slot that was deleted
     */
    public String removeByTime(Date startTime) {
        return collection.remove(new BasicDBObject("startTime", startTime)).getSavedId();
    }
    
    public List<Slot> listByColumn(String sheetID, String columnName) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sheetID", sheetID);
        map.put("columnName", columnName);
        List<Slot> rtn = new LinkedList<Slot>();
        Iterator<Slot> allItems = collection.find(new BasicDBObject(map));
        while (allItems.hasNext())
            rtn.add(allItems.next());
        return rtn;
    }

}
