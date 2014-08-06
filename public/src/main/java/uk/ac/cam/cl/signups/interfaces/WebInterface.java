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
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
@Path("/")
@Consumes("application/json")
public interface WebInterface {
    
    // TODO: redo all paths
        
    /**
     * @param sheet The sheet to be added to the database
     * @return A SheetInfo object containing the SheetID and
     * admin authorisation code of the sheet, and the URL which
     * can be used to access the sheet (for signing up only)
     * @throws DuplicateNameException
     */
    @POST
    @Path("/sheets")
    @Consumes("application/json")
    @Produces("application/json")
    public SheetInfo addSheet(Sheet sheet) throws DuplicateNameException;
    
    /**
     * @return A list of all the sheet objects currently stored in the
     * database.
     */
    @GET
    @Path("/sheets")
    @Produces("application/json")
    public List<Sheet> listSheets();
    
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
            @PathParam("sheetID") String sheetID,
            String authCode /* in body of request */)
                    throws ItemNotFoundException, NotAllowedException;
    
    /**
     * @param sheetID The ID of a sheet whose columns are to be listed
     * @return A list of the Column objects for the specified sheet
     * @throws ItemNotFoundException The sheet was not found in the database
     */
    @GET
    @Path("/sheets/{sheetID}")
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
     * @throws DuplicateNameException 
     */
    @POST
    @Path("/sheets/{sheetID}")
    @Consumes("application/json")
    public void addColumn(@PathParam("sheetID") String sheetID,
            ColumnBean bean /* contains Column to add and sheet authCode */)
                    throws ItemNotFoundException, NotAllowedException, DuplicateNameException;
    
    //TODO: javadoc
    @POST
    @Path("/sheets/{sheetID}")
    @Consumes("application/json")
    public void createColumn(@PathParam("sheetID") String sheetID, CreateColumnBean bean)
            throws ItemNotFoundException, NotAllowedException, DuplicateNameException;
    
    /**
     * Updates the database so that the specified sheet no longer contains
     * the specified column. Also deletes all slots, booked or unbooked, contained
     * in the deleted column. TODO: inform people if their slot was deleted?
     * @param sheetID The ID of the sheet to delete the column from
     * @param columnName The name of the column to delete
     * @param authCode The admin authorisation code for the sheet
     * @throws NotAllowedException The admin authorisation code was incorrect
     * @throws ItemNotFoundException The sheet or column specified was not found
     */
    @DELETE
    @Path("/sheets/{sheetID}/{columnName}")
    @Consumes("text/plain")
    public void deleteColumn(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName,
            String authCode /* in request body*/)
                    throws NotAllowedException, ItemNotFoundException;
    
    /**
     * Lists the slots in a specified column in a specified sheet.
     * @param sheetID
     * @param columnName
     * @return A list of the slot objects in the specified column
     * @throws ItemNotFoundException The sheet or column was not found
     */
    @GET
    @Path("/sheets/{sheetID}/{columnName}")
    @Produces("application/json")
    public List<Slot> listColumnSlots(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName) throws ItemNotFoundException;
    
    // TODO: javadoc
    @GET
    @Path("/users/{user}")
    @Produces("application/json")
    public List<Slot> listUserSlots(@PathParam("user") String user);
    
    /**
     * @param sheetID
     * @return If a slot is free, its start time appears in this list.
     * @throws ItemNotFoundException 
     */
    @GET
    @Path("/TODO/{sheetID}")
    @Produces("application/json")
    public List<Date> listAllFreeStartTimes(String user, String comment, String groupID, @PathParam("sheetID") String sheetID) throws ItemNotFoundException;
    
    /**
     * @param startTime
     * @return A list of the names of the columns with a free slot that starts
     * at the given time
     */
    @GET
    @Path("/TODO/{sheetID}/{startTime}")
    @Produces("application/json")
    public List<String> listColumnsWithFreeSlotsAt(@PathParam("sheetID") String SheetID, @PathParam("startTime") Date startTime)
            throws ItemNotFoundException;
    
    /**
     * Adds the given slot to the specified column in the specified sheet in the
     * database.
     * @param sheetID
     * @param columnName
     * @param bean Contains the Slot object and the admin authorisation
     * code for the sheet
     * @throws ItemNotFoundException
     * @throws NotAllowedException
     * @throws DuplicateNameException 
     */
    @POST
    @Path("/sheets/{sheetID}/{columnName}")
    @Consumes("application/json")
    public void addSlot(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName,
            SlotBean bean /* contains Slot to add and sheet authCode */)
                    throws ItemNotFoundException, NotAllowedException, DuplicateNameException;
    
    /**
     * Deletes the specified slot from the specified column in the specified
     * sheet in the database. TODO: notify user booked?
     * @param sheetID
     * @param columnName
     * @param startTime Date object used to identify the slot
     * @param authCode
     * @throws ItemNotFoundException Sheet, column, or slot not found
     * @throws NotAllowedException The admin authorisation code was incorrect
     */
    @DELETE
    @Path("/sheets/{sheetID}/{columnName}/{time}")
    @Consumes("text/plain")
    public void deleteSlot(
            @PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName,
            @PathParam("time") Date startTime,
            String authCode /* in request body */)
                    throws ItemNotFoundException, NotAllowedException;
    
    /**
     * Gives information about the current booking status of the slot.
     * @param sheetID
     * @param columnName
     * @param startTime Identifies the slot
     * @return A BookingInfo object which contains the user currently
     * booked into the slot (null if unbooked) and the comment they are
     * booked with.
     * @throws ItemNotFoundException
     */
    @GET
    @Path("/sheets/{sheetID}/{columnName}/{time}")
    @Produces("application/json")
    public BookingInfo showBooking(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName,
            @PathParam("time") Date startTime)
                    throws ItemNotFoundException;
    
    /**
     * Method that allows both normal and admin booking and unbooking
     * of slots.
     * <p>
     * If a sheet admin authorisation code is provided, and is correct, then
     * the specified booking is always modified to the given user and
     * comment. If a sheet admin authorisation code is provided, but is incorrect, then
     * a NotAllowedException is raised.
     * <p>
     * If no sheet authCode is provided, then the call is treated as a
     * user request. If it is a booking, then it is only allowed if the
     * slot is currently unbooked and the user/comment/column combination
     * is whitelisted. If it is an unbooking, then it is only allowed if
     * the slot is booked by the given user and the start time of the slot
     * is in the future.
     * @param sheetID
     * @param columnName
     * @param startTime
     * @param bookingBean
     * @throws ItemNotFoundException
     * @throws NotAllowedException
     */
    @POST
    @Path("/sheets/{sheetID}/{columnName}/{time}")
    @Consumes("application/json")
    public void book(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName,
            @PathParam("time") Date startTime,
            SlotBookingBean bookingBean)
                    throws ItemNotFoundException, NotAllowedException;
    
    /**
     * Unbooks the given user from all future slots they have booked on the
     * given sheet.
     * @param sheetID 
     * @param user
     * @param authCode
     * @throws NotAllowedException
     * @throws ItemNotFoundException
     */
    @DELETE
    @Path("/sheets/{sheetID}/{user}")
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
     * @return A list of all group objects currently stored in the database
     */
    @GET
    @Path("/groups")
    @Produces("application/json")
    public List<Group> listGroups();
    
    /**
     * Deletes the given group object from the database. All sheets corresponding to
     * the deleted group remain in the database.
     * @param groupName
     * @param groupAuthCode
     * @throws ItemNotFoundException
     * @throws NotAllowedException The group admin authorisation code was incorrect
     */
    @DELETE
    @Path("/groups/{groupName}")
    @Consumes("text/plain")
    public void deleteGroup(
            @PathParam("groupName") String groupName,
            String groupAuthCode)
                    throws ItemNotFoundException, NotAllowedException;
    
    /**
     * Returns the "whitelist" map from comments to columns for the
     * given group of sheets and the given user. A user is allowed to
     * sign up for a slot in a sheet in the given group only using the
     * column their desired comment maps to (any column if it maps to null,
     * no column if the comment doesn't appear in the map).
     * @param groupName
     * @param user
     * @return
     * @throws ItemNotFoundException
     */
    @GET
    @Path("/groups/{groupName}/whitelist/{user}")
    @Produces("application/json")
    public Map<String, String> getPermissions(
            @PathParam("groupName") String groupName,
            @PathParam("user") String user);
    
    /**
     * Allows the user to sign up using the specified column
     * for each given comment. If an entry for a comment already exists,
     * the relevant column is overwritten.
     * @param groupName
     * @param user
     * @param bean Contains the comment to column map and the group admin
     * authorisation code
     * @throws NotAllowedException
     * @throws ItemNotFoundException
     * @throws DuplicateNameException
     */
    @POST
    @Path("/groups/{groupName}/whitelist/{user}")
    @Consumes("application/json")
    public void addPermissions(
            @PathParam("groupName") String groupName,
            @PathParam("user") String user,
            PermissionsBean bean /* contains comment-column map and group authCode*/)
                    throws NotAllowedException, ItemNotFoundException;
    /**
     * Forbids the user from signing up using any of the given comments (the keys in
     * the map).
     * @param groupName
     * @param user
     * @param bean Contains comment-column map and group authCode
     * @throws NotAllowedException
     * @throws ItemNotFoundException
     */
    @DELETE
    @Path("/groups/{groupName}/whitelist/{user}")
    @Consumes("application/json")
    public void removePermissions(
            @PathParam("groupName") String groupName,
            @PathParam("user") String user,
            PermissionsBean bean)
/* we actually only need a collection of comments and authCode - consider new type of bean? */
                    throws NotAllowedException, ItemNotFoundException;
    
    /**
     * Adds the specified sheet to the specified group.
     * @param groupName
     * @param bean Contains the sheet ID and both sheet and group admin authorisation codes
     * @throws ItemNotFoundException
     * @throws NotAllowedException
     */
    @POST
    @Path("/groups/{groupName}/sheets")
    @Consumes("application/json")
    public void addSheetToGroup(@PathParam("groupName") String groupName,
            GroupSheetBean bean /* contains group and sheet authCodes and sheetID*/)
                    throws ItemNotFoundException, NotAllowedException;
    
    
    @GET
    @Path("/todooooo/{sheetID}") //TODO
    @Produces("application/json")
    public List<String> getGroupIDs(@PathParam("sheetID") String sheetID) throws ItemNotFoundException;
    
    /**
     * @param groupName
     * @return A list of the sheet IDs for all the sheets in the specified group.
     * @throws ItemNotFoundException
     */
    @GET
    @Path("/groups/{groupName}/sheets")
    @Produces("application/json")
    public List<Sheet> listSheets(@PathParam("groupName") String groupName)
            throws ItemNotFoundException;
    
    /**
     * Removes the specified sheet from the specified group.
     * @param groupName
     * @param sheetID
     * @param groupAuthCode
     * @throws ItemNotFoundException
     * @throws NotAllowedException
     */
    @DELETE
    @Path("/groups/{groupName}/sheets/{sheetID}")
    @Consumes("text/plain")
    public void removeSheetFromGroup(
            @PathParam("groupName") String groupName,
            @PathParam("sheetID") String sheetID,
            String groupAuthCode) throws ItemNotFoundException, NotAllowedException;
    
}
