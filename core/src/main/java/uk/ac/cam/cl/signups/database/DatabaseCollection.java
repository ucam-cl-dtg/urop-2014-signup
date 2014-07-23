/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.database;

import java.util.List;

/**
 * @author ird28
 *
 */
public interface DatabaseCollection<T> {

    /**
     * Adds a new item to the database collection.
     *
     * @param item The item to be added
     * @throws DuplicateNameException An item with this name already exists.
     */
    public void insertItem(T item) throws DuplicateNameException;

    /**
     * Updates the given item.
     *
     * @param repo The updated item (there must also be a
     * item with this name in the collection already).
     */
    public void updateItem(T item) throws ItemNotFoundException;

    /**
     * @return A list of the items in the collection
     */
    public List<T> listItems();
    
    /**
     * Returns true iff there is an item with the given name in the collection.
     */
    public boolean contains(String name);

    /**
     * Returns the item with the given name in the
     * database collection.
     * 
     * @param name The name of the item
     * @return The requested item
     */
    public T getItem(String name) throws ItemNotFoundException;    

    /**
     * Removes all items from the database collection.
     */
    public void removeAll();

    /**
     * Removes the item with the given name from the database.
     * 
     * @param name The name of the item to remove
     */
    public void removeItem(String name) throws ItemNotFoundException;
}
