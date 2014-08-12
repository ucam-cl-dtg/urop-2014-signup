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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;

import uk.ac.cam.cl.signups.SignupService;
import uk.ac.cam.cl.signups.api.Group;
import uk.ac.cam.cl.signups.api.Slot;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * 
 * Used for storing and accessing Slots in the database.
 */
public class MongoSlots {
    
    /* For logging */
    private static final Logger log = LoggerFactory.getLogger(MongoSlots.class);

    private JacksonDBCollection<Slot, String> collection =
            JacksonDBCollection.wrap(
                    Mongo.getDB().getCollection("slots")
                    , Slot.class
                    , String.class);

    public void insertSlot(Slot slot) throws DuplicateNameException {
        try {
            collection.insert(slot);
        } catch (com.mongodb.DuplicateKeyException dupKey) {
            log.debug("Duplicate Key Exception caught when inserting slot " +
                    "of ID " + slot.get_id() + " - throwing DuplicateNameException");
            throw new DuplicateNameException(slot.get_id());
        } catch (com.mongodb.MongoException e) {
            log.error("A MongoException was caught when inserting a slot " +
                    "of ID " + slot.get_id() + " - throwing RuntimeException " +
                    "because the only MongoException that is okay is DuplicateKey");
            throw new RuntimeException(e);
        }
    }

    public void updateSlot(Slot slot) throws ItemNotFoundException {
        if (!contains(slot.get_id())) {
            log.debug("The item of ID " + slot.get_id()
                    + " of type " + slot.getClass().getSimpleName() + " was not found in the database");
            throw new ItemNotFoundException("The item of ID " + slot.get_id()
                    + " of type " + slot.getClass().getSimpleName() + " was not found in the database");
        }
        collection.updateById(slot.get_id(), slot);
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
        if (!contains(id)) {
            log.debug("The slot " + id + " was not found in the database");
            throw new ItemNotFoundException("The slot " + id +
                    " was not found in the database");
        }
        return collection.findOneById(id);
    }
    
    public Slot getSlot(String sheetID, String columnName, Date startTime) throws ItemNotFoundException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("sheetID", sheetID);
        map.put("columnName", columnName);
        map.put("startTime", startTime);
        Slot toReturn = collection.findOne(new BasicDBObject(map));
        if (toReturn == null) {
            log.debug("The slot specified below was not found in the database:\n" +
                    "Sheet " + sheetID + " Column" + columnName + " Start Time " + startTime);
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
        if (!contains(id)) {
            log.debug("The slot with id " + id +
                    " was not found in the database");
            throw new ItemNotFoundException("The slot with id " + id +
                    " was not found in the database");
        }
        collection.removeById(id);
        return id;
    }
    
    /**
     * @return ID of the slot that was deleted
     */
    public String removeByTime(String sheetID, String columnName, Date startTime) throws ItemNotFoundException {
        return removeSlot(getSlot(sheetID, columnName, startTime).get_id());
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
    
    public List<Slot> listByUser(String user) {
        List<Slot> rtn = new LinkedList<Slot>();
        Iterator<Slot> allItems = collection.find(new BasicDBObject("bookedUser", user));
        while (allItems.hasNext())
            rtn.add(allItems.next());
        return rtn;
    }

}
