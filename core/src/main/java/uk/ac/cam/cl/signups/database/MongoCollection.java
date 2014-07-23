package uk.ac.cam.cl.signups.database;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.mongojack.JacksonDBCollection;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;

public class MongoCollection<T extends DatabaseItem> implements DatabaseCollection<T> {
    
    @Inject private JacksonDBCollection<T, String> collection;

    public void insertItem(T item) throws DuplicateNameException {
        try {
            collection.ensureIndex(new BasicDBObject("name", 1), null, true); // each repo name must be unique
            collection.insert(item);
        } catch(com.mongodb.MongoException dupKey) {
            throw new DuplicateNameException(item.getName());
        }
    }

    public void updateItem(T item) throws ItemNotFoundException {
        if (!contains(item.getName()))
            throw new ItemNotFoundException();
        collection.updateById(item.get_id(), item);
    }

    public List<T> listItems() {
        List<T> rtn = new LinkedList<T>();
        Iterator<T> allRepos = collection.find();
        while (allRepos.hasNext())
            rtn.add(allRepos.next());
        return rtn;
    }

    public boolean contains(String name) {
        int matchingItems = collection.find(new BasicDBObject("name", name)).count(); //TODO: check that using "name" here is okay
        assert (matchingItems == 0 || matchingItems == 1);
        return (matchingItems == 1);
    }

    public T getItem(String name) throws ItemNotFoundException {
        if (!contains(name))
            throw new ItemNotFoundException();
        return collection.findOne(new BasicDBObject("name", name));
    }

    public void removeAll() {
        collection.remove(new BasicDBObject());
    }

    public void removeItem(String name) throws ItemNotFoundException {
        if (!contains(name))
            throw new ItemNotFoundException();
        collection.remove(new BasicDBObject("name", name)); //TODO: check the name thing, maybe replace T with some interface?
    }

}
