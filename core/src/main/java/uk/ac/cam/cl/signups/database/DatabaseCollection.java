/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.database;

import java.util.List;
import java.util.Map;

import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.interfaces.DatabaseItem;

/**
 * @author ird28
 *
 */
public interface DatabaseCollection<T extends /*ie implements*/ DatabaseItem> {

    /**
     * Adds a new item to the database collection.
     *
     * @param item The item to be added
     * @throws DuplicateidException An item with this id already exists.
     */
    public void insertItem(T item) throws DuplicateNameException;

    /**
     * Updates the given item.
     *
     * @param item The updated item (there must also be a
     * item with this id in the collection already).
     */
    public void updateItem(T item) throws ItemNotFoundException;

    /**
     * @return A list of the items in the collection
     */
    public List<T> listItems();
        
    /**
     * Returns true iff there is an item with the given id in the collection.
     */
    public boolean contains(String id);

    /**
     * Returns the item with the given id in the
     * database collection.
     * 
     * @param id The id of the item
     * @return The requested item
     */
    public T getItem(String id) throws ItemNotFoundException;    

    /**
     * Removes all items from the database collection.
     */
    public void removeAll();

    /**
     * Removes the item with the given id from the database.
     * 
     * @param id The id of the item to remove
     */
    public void removeItem(String id) throws ItemNotFoundException;
}
