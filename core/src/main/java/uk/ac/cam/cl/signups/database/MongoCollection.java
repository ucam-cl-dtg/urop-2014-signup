package uk.ac.cam.cl.signups.database;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mongojack.JacksonDBCollection;

import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.interfaces.DatabaseItem;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;

public class MongoCollection<T extends DatabaseItem> implements DatabaseCollection<T> {
    
    private JacksonDBCollection<T, String> collection;
    
    @Inject /* We are not actually injecting using Guice, we do it manually */
    protected void setCollection(JacksonDBCollection<T, String> collection) {
        this.collection = collection;
    }

    public void insertItem(T item) throws DuplicateNameException {
        try {
            collection.insert(item);
        } catch(com.mongodb.MongoException dupKey) {
            System.out.println("Should be duplicate key:");
            dupKey.printStackTrace();
            throw new DuplicateNameException(item.get_id());
        }
    }

    public void updateItem(T item) throws ItemNotFoundException {
        if (!contains(item.get_id()))
            throw new ItemNotFoundException("The item " + item.get_id()
                    + " of type " + item.getClass() + " was not found in the database");
        collection.updateById(item.get_id(), item);
    }

    public List<T> listItems() {
        List<T> rtn = new LinkedList<T>();
        Iterator<T> allItems = collection.find();
        while (allItems.hasNext())
            rtn.add(allItems.next());
        return rtn;
    }

    public boolean contains(String id) {
        int matchingItems = collection.find(new BasicDBObject("_id", id)).count();
        assert (matchingItems == 0 || matchingItems == 1);
        return (matchingItems == 1);
    }

    public T getItem(String name) throws ItemNotFoundException {
        if (!contains(name))
            throw new ItemNotFoundException("The item " + name +
                    " was not found in the database");
        return collection.findOneById(name);
    }

    public void removeAll() {
        collection.remove(new BasicDBObject());
    }

    public void removeItem(String id) throws ItemNotFoundException {
        if (!contains(id))
            throw new ItemNotFoundException("The item " + id +
                    " was not found in the database");
        collection.removeById(id);
    }

}
