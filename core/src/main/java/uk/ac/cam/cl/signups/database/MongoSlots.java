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
import com.mongodb.MongoException;

import uk.ac.cam.cl.signups.api.Group;
import uk.ac.cam.cl.signups.api.Slot;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class MongoSlots {

    private JacksonDBCollection<Slot, String> collection =
            JacksonDBCollection.wrap(
                    Mongo.getDB().getCollection("slots")
                    , Slot.class
                    , String.class);

    public void insertSlot(Slot slot) throws DuplicateNameException {
        try {
            collection.insert(slot);
        } catch(com.mongodb.MongoException dupKey) {
            System.out.println("Should be duplicate key:");
            dupKey.printStackTrace();
            throw new DuplicateNameException(slot.getID());
        }
    }

    public void updateSlot(Slot slot) throws ItemNotFoundException {
        if (!contains(slot.getID()))
            throw new ItemNotFoundException("The slot " + slot.getID()
                    + " was not found in the database");
        collection.updateById(slot.getID(), slot);
    }

    public List<Slot> listSlots() {
        List<Slot> rtn = new LinkedList<Slot>();
        Iterator<Slot> allItems = collection.find();
        while (allItems.hasNext())
            rtn.add(allItems.next());
        return rtn;
    }

    public boolean contains(String id) {
        Slot matchingItem = collection.findOneById(id);
        return (matchingItem != null);
    }

    public Slot getSlot(String id) throws ItemNotFoundException {
        if (!contains(id))
            throw new ItemNotFoundException("The slot " + id +
                    " was not found in the database");
        return collection.findOneById(id);
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

    /**
     * @return ID of the slot that was deleted
     */
    public String removeSlot(String id) throws ItemNotFoundException {
        if (!contains(id))
            throw new ItemNotFoundException("The item " + id +
                    " was not found in the database");
        collection.removeById(id);
        return id;
    }
    
    /**
     * @return ID of the slot that was deleted
     */
    public String removeByTime(String sheetID, String columnName, Date startTime) throws ItemNotFoundException {
        return removeSlot(getSlot(sheetID, columnName, startTime).getID());
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
