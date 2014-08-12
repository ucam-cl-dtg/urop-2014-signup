package uk.ac.cam.cl.signups.database;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.mongojack.JacksonDBCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.signups.SignupService;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.interfaces.DatabaseItem;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;

public class MongoCollection<T extends DatabaseItem> implements DatabaseCollection<T> {

    /* For logging */
    private static final Logger log = LoggerFactory.getLogger(MongoCollection.class);

    private JacksonDBCollection<T, String> collection;

    @Inject /* We are not actually injecting using Guice, we do it manually */
    protected void setCollection(JacksonDBCollection<T, String> collection) {
        this.collection = collection;
    }

    public void insertItem(T item) throws DuplicateNameException {
        try {
            collection.insert(item);
        } catch (com.mongodb.DuplicateKeyException dupKey) {
            log.debug("Duplicate Key Exception caught when inserting something " +
                    "of ID " + item.get_id() + "and of class "
                    + item.getClass().getSimpleName() + " - throwing DuplicateNameException");
            throw new DuplicateNameException(item.get_id());
        } catch (com.mongodb.MongoException e) {
            log.error("A MongoException was caught when inserting something " +
                    "of ID " + item.get_id() + "and of class "
                    + item.getClass().getSimpleName() + " - throwing RuntimeException " +
                    "because the only MongoException that is okay is DuplicateKey");
            throw new RuntimeException(e);
        }
    }

    public void updateItem(T item) throws ItemNotFoundException {
        if (!contains(item.get_id())) {
            log.debug("The item of ID " + item.get_id()
                    + " of type " + item.getClass().getSimpleName() + " was not found in the database");
            throw new ItemNotFoundException("The item of ID " + item.get_id()
                    + " of type " + item.getClass().getSimpleName() + " was not found in the database");
        }
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

    public T getItem(String id) throws ItemNotFoundException {
        if (!contains(id)) {
            log.debug("The item of ID " + id + " was not found in the database");
            throw new ItemNotFoundException("The item of ID " + id + " was not found in the database");
        }
        return collection.findOneById(id);
    }

    public void removeAll() {
        collection.remove(new BasicDBObject());
    }

    public void removeItem(String id) throws ItemNotFoundException {
        if (!contains(id)) {
            log.debug("The item of ID " + id + " was not found in the database");
            throw new ItemNotFoundException("The item of ID " + id + " was not found in the database");
        }
        collection.removeById(id);
    }

}
