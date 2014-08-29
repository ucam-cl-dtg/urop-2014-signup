/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.interfaces;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;

import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.beans.*;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;

/**
 * Public interface for signups service. Aims to provide a general signup service,
 * but some of these methods have been created with ticking-specific functionality
 * in mind, and so might not be used in a more general service.
 * 
 * The endpoints are in general vaguely restful, but some clearly aren't, sorry.
 * 
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
@Path("/")
@Consumes("application/json")
public interface SignupsWebInterface {
        
    /**
     * @param sheet The sheet to be added to the database
     * @return A SheetInfo object containing the sheetID and
     * admin authorisation code of the sheet.
     * @throws DuplicateNameException The sheet already exists.
     */
    @POST
    @Path("/sheets")
    @Consumes("application/json")
    @Produces("application/json")
    public SheetInfo addSheet(Sheet sheet) throws DuplicateNameException;
    
    /**
     * @param sheetID
     * @param authCode
     * @return The requested sheet object
     * @throws ItemNotFoundException Sheet not found
     * @throws NotAllowedException Incorrect auth code
     */
    @GET
    @Path("/sheetobjects/{sheetID}")
    @Produces("application/json")
    public Sheet getSheet(@PathParam("sheetID") String sheetID, @QueryParam("authCode") String authCode)
            throws ItemNotFoundException, NotAllowedException;
    
    /**
     * Updates the title, location and description of the given sheet.
     * @param sheetID
     * @param bean Contains the new title, description and location, and the auth code
     * @throws NotAllowedException wrong authCode
     * @throws ItemNotFoundException Sheet not found
     */
    @POST
    @Path("/sheetobjects/{sheetID}")
    @Consumes("application/json")
    public void updateSheetInfo(@PathParam("sheetID") String sheetID, UpdateSheetBean bean)
            throws NotAllowedException, ItemNotFoundException;
    
    /**
     * Deletes the specified sheet from database.
     * @param sheetID The ID of the sheet to be deleted
     * @param authCode The admin authorisation code for the sheet specified
     * @throws ItemNotFoundException The sheet was not found in the database
     * @throws NotAllowedException The admin authorisation code was incorrect
     */
    @DELETE
    @Path("/sheets/{sheetID}")
    @Consumes("text/plain")
    public void deleteSheet(
            @PathParam("sheetID") String sheetID, String authCode)
                    throws ItemNotFoundException, NotAllowedException;
    
    /**
     * @param sheetID The ID of a sheet whose columns are to be listed
     * @return A list of the Column objects for the specified sheet, sorted alphabetically
     * @throws ItemNotFoundException The sheet was not found in the database
     */
    @GET
    @Path("/sheets/{sheetID}/")
    @Produces("application/json")
    public List<Column> listColumns(@PathParam("sheetID") String sheetID)
            throws ItemNotFoundException;
    
    /**
     * Updates the database so that the given column is added to the
     * specified sheet.
     * @param sheetID The ID of the sheet to add the Column to
     * @param bean Contains the Column object to be added and the admin authorisation
     * code of the sheet
     * @throws ItemNotFoundException The sheet was not found in the database
     * @throws NotAllowedException The admin authorisation code was incorrect
     * @throws DuplicateNameException A column with that name already exists
     */
    @POST
    @Path("/sheets/{sheetID}/columns/")
    @Consumes("application/json")
    public void addColumn(@PathParam("sheetID") String sheetID, ColumnBean bean)
            throws ItemNotFoundException, NotAllowedException, DuplicateNameException;
    
    /**
     * Adds a new column to the given sheet, populated with empty slots of the given
     * length between the given start and end times.
     * @param sheetID
     * @param bean Contains column name, start and end times, slot length in minutes,
     * and the sheet auth code
     * @throws ItemNotFoundException Sheet not found
     * @throws NotAllowedException Wrong authorisation code
     * @throws DuplicateNameException Column with that name already exists, or slot already exists
     */
    @POST
    @Path("/sheets/{sheetID}/createcolumn/")
    @Consumes("application/json")
    public void createColumn(@PathParam("sheetID") String sheetID, CreateColumnBean bean)
            throws ItemNotFoundException, NotAllowedException, DuplicateNameException;
    
    /**
     * Updates the database so that the specified sheet no longer contains
     * the specified column. Also deletes all slots, booked or unbooked, contained
     * in the deleted column.
     * @param sheetID The ID of the sheet to delete the column from
     * @param columnName The name of the column to delete
     * @param authCode The admin authorisation code for the sheet
     * @throws NotAllowedException The admin authorisation code was incorrect
     * @throws ItemNotFoundException The sheet or column specified was not found
     */
    @DELETE
    @Path("/sheets/{sheetID}/columns/{columnName}")
    @Consumes("text/plain")
    public void deleteColumn(@PathParam("sheetID") String sheetID, 
            @PathParam("columnName") String columnName, String authCode)
                    throws NotAllowedException, ItemNotFoundException;
    
    /**
     * Lists the slots in a specified column in a specified sheet, in
     * chronilogical order.
     * @param sheetID
     * @param columnName
     * @return A list of the slot objects in the specified column
     * @throws ItemNotFoundException The sheet or column was not found
     */
    @GET
    @Path("/sheets/{sheetID}/columns/{columnName}")
    @Produces("application/json")
    public List<Slot> listColumnSlots(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName) throws ItemNotFoundException;
    
    /**
     * @param user Name of user whose slots are to be listed
     * @return A chronological list of all slots booked by the user: past, present and future.
     */
    @GET
    @Path("/users/{user}")
    @Produces("application/json")
    public List<Slot> listUserSlots(@PathParam("user") String user);
    
    /**
     * Returns a chronological list of the times that the given user can
     * use to sign up for with the given comment.
     * @param sheetID
     * @return List of Date objects representing allowed times
     * @throws ItemNotFoundException The sheet was not found
     */
    @GET
    @Path("/sheets/{sheetID}/times/{groupID}/{comment}/{user}")
    @Produces("application/json")
    public List<Date> listAllFreeStartTimes(@PathParam("user") String user,
            @PathParam("comment") String comment,
            @PathParam("groupID") String groupID,
            @PathParam("sheetID") String sheetID)
                    throws ItemNotFoundException;
    
    /**
     * @param sheetID
     * @param startTime
     * @return List of the names of the columns with a free slot starting at the given time
     * @throws ItemNotFoundException Sheet not found
     */
    @GET
    @Path("/sheets/{sheetID}/times/{startTime}")
    @Produces("application/json")
    public List<String> listColumnsWithFreeSlotsAt(@PathParam("sheetID") String sheetID,
            @PathParam("startTime") Long startTime)
                    throws ItemNotFoundException;

    /**
     * Adds the given slot to the specified column in the specified sheet in the
     * database.
     * @param sheetID
     * @param columnName
     * @param bean Contains the Slot object and the admin authorisation code for the sheet
     * @throws ItemNotFoundException Sheet or column not found
     * @throws NotAllowedException Incorrect authorisation code
     * @throws DuplicateNameException Slot already exists
     */
    @POST
    @Path("/sheets/{sheetID}/columns/{columnName}")
    @Consumes("application/json")
    public void addSlot(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName, SlotBean bean)
                    throws ItemNotFoundException, NotAllowedException, DuplicateNameException;

    /**
     * If the auth code is correct, adds slots of the given length to all columns
     * in the given sheet, between the given start and end times.
     * Note that NO CHECKS are made as to whether these slots already exist.
     * @param sheetID
     * @param bean Contains start and end times, slot length in minutes, and authorisation code
     * @throws ItemNotFoundException Sheet not found
     * @throws NotAllowedException Wrong authorisation code
     * @throws DuplicateNameException Slot already exists
     */
    @POST
    @Path("/sheets/{sheetID}/batchcreate")
    @Consumes("application/json")
    public void createSlotsForAllColumns(@PathParam("sheetID") String sheetID, BatchCreateBean bean)
            throws ItemNotFoundException, NotAllowedException, DuplicateNameException;

    /**
     * Deletes the specified slot from the specified column in the specified
     * sheet in the database.
     * @param sheetID
     * @param columnName
     * @param startTime
     * @param authCode
     * @throws ItemNotFoundException Sheet, column, or slot not found
     * @throws NotAllowedException The admin authorisation code was incorrect
     */
    @DELETE
    @Path("/sheets/{sheetID}/columns/{columnName}/{time}")
    @Consumes("text/plain")
    public void deleteSlot(
            @PathParam("sheetID") String sheetID, @PathParam("columnName") String columnName,
            @PathParam("time") Long startTime, String authCode)
                    throws ItemNotFoundException, NotAllowedException;

    /**
     * Removes all slots with start time earlier than the given time.
     * @param sheetID
     * @param start
     * @param end
     * @throws ItemNotFoundException
     */
    @POST
    @Path("/sheets/{sheetID}/batchdelete/before")
    @Consumes("application/json")
    public void deleteSlotsBefore(@PathParam("sheetID") String sheetID, BatchDeleteBean bean)
            throws ItemNotFoundException, NotAllowedException;
    
    /**
     * Deletes all slots in the specified sheet with start time later than the given time.
     * @param sheetID
     * @param bean Contains the time and the auth code
     * @throws ItemNotFoundException Sheet not found
     * @throws NotAllowedException Wrong authorisation code
     */
    @POST
    @Path("/sheets/{sheetID}/batchdelete/after")
    @Consumes("application/json")
    public void deleteSlotsAfter(@PathParam("sheetID") String sheetID, BatchDeleteBean bean)
            throws ItemNotFoundException, NotAllowedException;
    
    /**
     * Gives booked user and comment of specified slot.
     * @param sheetID
     * @param columnName
     * @param startTime Identifies the slot
     * @return A BookingInfo object which contains the user currently
     * booked into the slot (null if unbooked) and the comment they are
     * booked with.
     * @throws ItemNotFoundException Slot not found
     */
    @GET
    @Path("/sheets/{sheetID}/columns/{columnName}/{time}")
    @Produces("application/json")
    public BookingInfo showBooking(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName,
            @PathParam("time") Long startTime)
                    throws ItemNotFoundException;

    /**
     * Method that allows both normal and admin booking and unbooking
     * of slots.
     * 
     * If a sheet admin authorisation code is provided, and is correct, then
     * the specified booking is always modified to the given user and
     * comment. If a sheet admin authorisation code is provided, but is incorrect, then
     * a NotAllowedException is raised.
     * 
     * If no sheet authCode is provided, then the call is treated as a
     * user request. If it is a booking, then it is only allowed if the
     * slot is currently unbooked and the user/comment/column combination
     * is whitelisted. If it is an unbooking, then it is only allowed if
     * the slot is booked by the given user and the start time of the slot
     * is in the future.
     * @param sheetID
     * @param columnName
     * @param startTime
     * @param bookingBean Contains currently booked user, user to book, comment,
     * and possibly authorisation code
     * @throws ItemNotFoundException Sheet/column/slot not found
     * @throws NotAllowedException Permission denied for reason given in exception message
     */
    @POST
    @Path("/sheets/{sheetID}/columns/{columnName}/{time}")
    @Consumes("application/json")
    public void book(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName,
            @PathParam("time") Long startTime,
            SlotBookingBean bookingBean)
                    throws ItemNotFoundException, NotAllowedException;
    
    /**
     * Unbooks the given user from all future slots they have booked on the
     * given sheet.
     * @param sheetID 
     * @param user
     * @param authCode
     * @throws NotAllowedException Incorrect auth code
     * @throws ItemNotFoundException Sheet not found
     */
    @DELETE
    @Path("/sheets/{sheetID}/users/{user}")
    @Consumes("text/plain")
    public void removeAllUserBookings(@PathParam("sheetID") String sheetID,
            @PathParam("user") String user, String authCode)
                    throws NotAllowedException, ItemNotFoundException;
    
    /**
     * Adds the given group object to the database.
     * @param group
     * @return The admin authorisation code for the group.
     * @throws DuplicateNameException A group with that name already exists
     * in the database
     */
    @POST
    @Path("/groups")
    @Consumes("application/json")
    @Produces("text/plain")
    public String addGroup(Group group) throws DuplicateNameException;
        
    /**
     * Deletes the given group object from the database. All sheets corresponding to
     * the deleted group remain in the database.
     * @param groupName
     * @param groupAuthCode
     * @throws ItemNotFoundException Group not found
     * @throws NotAllowedException The group admin authorisation code was incorrect
     */
    @DELETE
    @Path("/groups/{groupName}")
    @Consumes("text/plain")
    public void deleteGroup(@PathParam("groupName") String groupName, String groupAuthCode)
            throws ItemNotFoundException, NotAllowedException;

    /**
     * Returns the "whitelist" map from comments to columns for the
     * given group of sheets and the given user. A user is allowed to
     * sign up for a slot in a sheet in the given group only using the
     * column their desired comment maps to (any column if it maps to null,
     * no column if the comment doesn't appear in the map).
     * @param groupName
     * @param user
     * @return The whitelist map
     */
    @GET
    @Path("/groups/{groupName}/whitelist/{user}")
    @Produces("application/json")
    public Map<String, String> getPermissions(
            @PathParam("groupName") String groupName, @PathParam("user") String user);
    
    /**
     * Allows the user to sign up using the specified column
     * for each given comment. If an entry for a comment already exists,
     * the existing column is overwritten. null means any column
     * @param groupName
     * @param user
     * @param bean Contains the comment to column map and the group admin
     * authorisation code
     * @throws NotAllowedException Incorrect auth code
     * @throws ItemNotFoundException Group not found
     */
    @POST
    @Path("/groups/{groupName}/whitelist/{user}")
    @Consumes("application/json")
    public void addPermissions(@PathParam("groupName") String groupName,
            @PathParam("user") String user, AddPermissionsBean bean)
                    throws NotAllowedException, ItemNotFoundException;
    
    /**
     * Forbids the user from signing up using any of the given comments.
     * @param groupName
     * @param user
     * @param bean Contains list of comments and group authCode
     * @throws NotAllowedException Wrong auth code
     * @throws ItemNotFoundException Group not found
     */
    @DELETE
    @Path("/groups/{groupName}/whitelist/{user}")
    @Consumes("application/json")
    public void removePermissions(@PathParam("groupName") String groupName,
            @PathParam("user") String user, RemovePermissionsBean bean)
                    throws NotAllowedException, ItemNotFoundException;
    
    /**
     * Adds the specified sheet to the specified group.
     * @param groupName
     * @param bean Contains the sheet ID and both sheet and group admin authorisation codes
     * @throws ItemNotFoundException Sheet or group not found
     * @throws NotAllowedException Wrong auth code
     */
    @POST
    @Path("/groups/{groupName}/sheets")
    @Consumes("application/json")
    public void addSheetToGroup(@PathParam("groupName") String groupName, GroupSheetBean bean)
            throws ItemNotFoundException, NotAllowedException;
    
    /**
     * @param sheetID
     * @return A list of the IDs of all groups this sheet is a part of
     * @throws ItemNotFoundException Sheet not found
     */
    @GET
    @Path("/sheets/{sheetID}/groups")
    @Produces("application/json")
    public List<String> getGroupIDs(@PathParam("sheetID") String sheetID) throws ItemNotFoundException;
    
    /**
     * @param groupName
     * @param groupAuthCode
     * @return A sorted list of all the sheet objects in the specified group.
     * @throws ItemNotFoundException Group not found
     * @throws NotAllowedException Wrong auth code
     */
    @GET
    @Path("/groups/{groupName}/sheets")
    @Produces("application/json")
    public List<Sheet> listSheets(@PathParam("groupName") String groupName, @QueryParam("groupAuthCode") String groupAuthCode)
            throws ItemNotFoundException, NotAllowedException;
    
    /**
     * Removes the specified sheet from the specified group.
     * @param groupName
     * @param sheetID
     * @param groupAuthCode
     * @throws ItemNotFoundException Sheet or group not found
     * @throws NotAllowedException Wrong auth code
     */
    @DELETE
    @Path("/groups/{groupName}/sheets/{sheetID}")
    @Consumes("text/plain")
    public void removeSheetFromGroup(@PathParam("groupName") String groupName,
            @PathParam("sheetID") String sheetID, String groupAuthCode)
                    throws ItemNotFoundException, NotAllowedException;
    
    /**
     * @param sheetID
     * @param columnName
     * @return true iff the given column has no free slots in the future
     */
    @GET
    @Path("/sheets/{sheetID}/columnisbooked/{columnName}")
    @Produces("text/plain")
    public boolean columnIsFullyBooked(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName);
    
}
