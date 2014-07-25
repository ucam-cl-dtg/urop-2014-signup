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
public interface WebInterface {
    
    /**
     * Stores the given group object in the database, and returns
     * both a URL giving sign-up only access to the sheet and an
     * authentication code granting admin access to it.
     * @param sheet
     * @return URL and authentication code TODO: and sheetID?
     * @throws DuplicateNameException
     */
    @POST
    @Path("/sheets")
    public SheetInfo addSheet(Sheet sheet) throws DuplicateNameException;
    
    @GET
    @Path("/sheets")
    public List<Sheet> listSheets();
    
    /**
     * Deletes sheet from database. Needs sheet admin permission.
     * @param sheetID
     * @param authCode
     * @throws ItemNotFoundException
     * @throws NotAllowedException
     */
    @DELETE
    @Path("/sheets/{sheetID}")
    public void deleteSheet(
            @PathParam("sheetID") String sheetID,
            String authCode /* in body of request */)
                    throws ItemNotFoundException, NotAllowedException;
    
    @GET
    @Path("/sheets/{sheetID}")
    public List<Column> listColumns(@PathParam("sheetID") String sheetID)
            throws ItemNotFoundException;
    
    @POST
    @Path("/sheets/{sheetID}")
    public void addColumn(@PathParam("sheetID") String sheetID,
            ColumnBean bean /* contains Column to add and sheet authCode */)
                    throws ItemNotFoundException, NotAllowedException;
    
    @DELETE
    @Path("/sheets/{sheetID}/{columnName}")
    public void deleteColumn(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName,
            String authCode /* in request body*/)
                    throws NotAllowedException, ItemNotFoundException;
    
    @GET
    @Path("/sheets/{sheetID}/{columnName}")
    public List<Slot> listSlots(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName) throws ItemNotFoundException;
    
    @POST
    @Path("/sheets/{sheetID}/{columnName}")
    public void addSlot(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName,
            SlotBean bean /* contains Slot to add and sheet authCode */)
                    throws ItemNotFoundException, NotAllowedException;
    
    @DELETE
    @Path("/sheets/{sheetID}/{columnName}/{time}")
    public void deleteSlot(
            @PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName,
            @PathParam("time") Date startTime,
            String authCode /* in request body */)
                    throws ItemNotFoundException, NotAllowedException;
    
    @GET
    @Path("/sheets/{sheetID}/{columnName}/{time}")
    public BookingInfo showBooking(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName,
            @PathParam("time") Date startTime)
                    throws ItemNotFoundException;
    
    /**
     * Method that allows both normal and admin booking and unbooking
     * of slots.
     * <p>
     * If a sheet authCode is provided, and is correct, then
     * the specified booking is always modified to the given user and
     * comment. If a sheet authCode is provided, but is incorrect, then
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
    public void book(@PathParam("sheetID") String sheetID,
            @PathParam("columnName") String columnName,
            @PathParam("time") Date startTime,
            SlotBookingBean bookingBean)
                    throws ItemNotFoundException, NotAllowedException;
    
    @POST
    @Path("/groups")
    public GroupInfo addGroup(Group group) throws DuplicateNameException;
    
    @GET
    @Path("/groups")
    public List<Group> listGroups();
    
    @DELETE
    @Path("/groups/{groupName}")
    public void deleteGroup(
            @PathParam("groupName") String groupName,
            String groupAuthCode)
                    throws ItemNotFoundException, NotAllowedException;
    
    @GET
    @Path("/groups/{groupName}/whitelist/{user}")
    public Map<String, String> /* comments --> columns */ getPermissions(
            @PathParam("groupName") String groupName,
            @PathParam("user") String user)
                    throws ItemNotFoundException;
    
    /**
     * Allows the user to user to sign up using the specified column
     * for each given comment. If an entry for a comment already exists,
     * the relevant column is overwritten.
     */
    @POST
    @Path("/groups/{groupName}/whitelist/{user}")
    public void addPermissions(
            @PathParam("groupName") String groupName,
            @PathParam("user") String user,
            PermissionsBean bean /* contains comment-column map and group authCode*/)
                    throws NotAllowedException, ItemNotFoundException;
    
    /**
     * Forbids the user from signing up using any of the given comments.
     */
    @DELETE
    @Path("/groups/{groupName}/whitelist/{user}")
    public void removePermissions(
            @PathParam("groupName") String groupName,
            @PathParam("user") String user,
            PermissionsBean bean /* contains comment-column map and group authCode*/)
/* we actually only need a collection of comments and authCode - consider new type of bean? */
                    throws NotAllowedException, ItemNotFoundException;
    
    @POST
    @Path("/groups/{groupName}/sheets")
    public void addSheet(@PathParam("groupName") String groupName,
            GroupSheetBean bean /* contains group and sheet authCodes and sheetID*/)
                    throws ItemNotFoundException, NotAllowedException;
    
    @GET
    @Path("/groups/{groupName}/sheets")
    public List<String> listSheetIDs(@PathParam("groupName") String groupName)
            throws ItemNotFoundException;
    
    @DELETE
    @Path("/groups/{groupName}/sheets/{sheetID}")
    public void removeSheetFromGroup(
            @PathParam("groupName") String groupName,
            @PathParam("sheetID") String sheetID,
            String groupAuthCode) throws ItemNotFoundException, NotAllowedException;
    
}
