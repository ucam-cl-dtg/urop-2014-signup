package uk.ac.cam.cl.signups.database;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.interfaces.DatabaseItem;

/**
 * This class can be used to replace a MongoCollection for testing purposes.
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class HashMapCollection<T extends DatabaseItem> implements DatabaseCollection<T> {

    private HashMap<String, T> collection = new HashMap<String, T>(); 

     
    public void insertItem(T item) throws DuplicateNameException {
        if (collection.containsKey(item.getID())) {
            throw new DuplicateNameException(item.getID());
        }
        collection.put(item.getID(), item);
    }

     
    public void updateItem(T item) throws ItemNotFoundException {
        if (!collection.containsKey(item.getID())) {
            throw new ItemNotFoundException(item.getID());
        }
        collection.put(item.getID(), item);
    }
     
    public List<T> listItems() {
        LinkedList<T> rtn = new LinkedList<T>();
        for (T item : collection.values()) {
            rtn.add(item);
        }
        return rtn;
    }

     
    public boolean contains(String name) {
        return collection.containsKey(name);
    }

     
    public T getItem(String name) throws ItemNotFoundException {
        if (!collection.containsKey(name)) {
            throw new ItemNotFoundException(name);
        }
        return collection.get(name);
    }

     
    public void removeAll() {
        collection.clear();
    }

     
    public void removeItem(String name) throws ItemNotFoundException {
        if (!collection.containsKey(name)) {
            throw new ItemNotFoundException(name);
        }
        collection.remove(name);
    }
    
}
