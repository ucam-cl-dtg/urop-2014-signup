/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 *
 */
package uk.ac.cam.cl.signups.interfaces;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;

import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.NotAllowedException;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public interface WebInterface {
    
    /* 
     * TODO: Redo all other classes to match this interface. In particular, 
     * the SignupsService and all the beans (probably from scratch).
     */
    
    /* TODO: Change authCode from a CookieParam to be included in the body of the request */
    /* TODO: Double check paths - check they make sense and check there are no conflicts */
    /* TODO: Check (and probably redo most) method prototypes */
    
    /* TODO: Write addGroup method */
    /* TODO: Write removeGroup method*/
    /* TODO: Write listGroups and listSheets methods (even if just for testing) */

    /**
     * Creates a sheet object, stores it in the database, and returns
     * both a URL giving sign-up only access to the sheet and an
     * authentication code granting admin access to it.
     * @param sheet The sheet to be added
     * @return Object containing the URL and the authentication code
     * @throws DuplicateNameException 
     */
    @PUT
    @Path("/groups/{groupName}/sheets")
    public SheetInfo addSheet(@PathParam("group") String groupName,
            Sheet sheet) throws DuplicateNameException;

    /* Listing sheets is not needed - we only want people to sign up if they have the URL */

    /**
     * Adds a column to the specified sheet, initially filled with the
     * given slots.
     * @param groupName
     * @param sheetID The ID of the sheet to add a column to
     * @param authCode Authentication code returned by createSheet
     * @param initalSlots
     * @throws ItemNotFoundException, NotAllowedException 
     */
    @PUT
    @Path("/groups/{groupName}/sheets/{sheetID}")
    public void addColumn(@PathParam("group") String groupName,
            @PathParam("sheetID") String sheetID,
            @CookieParam("authCode") String authCode, Column column)
                    throws ItemNotFoundException, NotAllowedException;

    /**
     * Adds the slot to the column in the sheet.
     * @param sheetID
     * @param authCode Authentication code returned by createSheet
     * @param columnName
     * @param startTime
     * @param duration Length of slot in minutes
     * @throws ItemNotFoundException 
     * @throws NotAllowedException 
     */
    @PUT
    @Path("/groups/{groupName}/sheets/{sheetID}/{columnName}")
    public void addSlot(@PathParam("group") String groupName,
            @PathParam("sheetID") String sheetID,
            @CookieParam("authCode") String authCode,
            @PathParam("columnName") String columnName, Slot slot)
                    throws ItemNotFoundException, NotAllowedException;

    /**
     * Deletes the specified column from the sheet.
     * @param sheetID
     * @param authCode Authentication code returned by createSheet
     * @param column
     * @throws ItemNotFoundException 
     * @throws NotAllowedException 
     */
    @DELETE
    @Path("/groups/{groupName}/sheets/{sheetID}/{column}")
    public void deleteColumn(@PathParam("group") String groupName,
            @PathParam("sheetID") String sheetID,
            @CookieParam("authCode") String authCode,
            @PathParam("column") String column)
                    throws NotAllowedException, ItemNotFoundException;
    
    /**
     * Deletes the specified slot.
     * @param sheetID
     * @param authCode
     * @param column
     * @param startTime
     * @throws ItemNotFoundException 
     * @throws NotAllowedException 
     */
    @DELETE
    @Path("/groups/{groupName}/sheets/{sheetID}/{column}/{time}")
    public void deleteSlot(
            @PathParam("group") String groupName,
            @PathParam("sheetID") String sheetID, 
            @CookieParam("authCode") String authCode,
            @PathParam("column") String column,
            @PathParam("time") Date startTime)
                    throws ItemNotFoundException, NotAllowedException;

    /**
     * Books the specified slot for the given user, but only if it is empty
     * and the user has permission to do so.
     * @param details Contains the CRSID of the user who wants to book it, and the
     * sheet, column and time of the slot they want to book.
     * @throws NotAllowedException The booking failed.
     * @throws ItemNotFoundException 
     */
    @POST
    @Path("/groups/{groupName}/sheets/{sheetID}/{columnName}/{time}")
    public void bookSlot(SlotBookingBean details)
            throws NotAllowedException, ItemNotFoundException;
    
    /*
     * TODO: Merge bookSlot and assignSlot. First, check if an authcode is provided. If it isn't,
     * make sure to check the slot is empty and the user is whitelisted to make the booking. If
     * it is, make sure to check it is valid.
     */

    /**
     * Books the specified slot for the given user no matter what - admin privileges.
     * @param details Contains the CRSID of the user who wants to book it, and the
     * sheet, column and time of the slot they want to book.
     * @param authCode
     * @throws NotAllowedException 
     * @throws ItemNotFoundException 
     */
    @POST
    @Path("/groups/{groupName}/sheets/{sheetID}/{columnName}/{time}")
    public void assignSlot(SlotBookingBean details,
            @CookieParam("authCode") String authCode)
                    throws ItemNotFoundException, NotAllowedException;

    /**
     * Unbooks the specified slot for the given user, but only if the passed CRSID
     * is actually booked into the given slot. Will not allow the unbooking if the start
     * time has passed.
     * @param details Contains the CRSID of the user to unbook, and the
     * sheet, column and time of the slot they want to unbook.
     * @throws NotAllowedException The unbooking failed.
     * @throws ItemNotFoundException 
     */
    @DELETE
    @Path("/groups/{groupName}/sheets/{sheetID}/{columnName}/{time}")
    public void unbookSlot(SlotBookingBean details)
            throws NotAllowedException, ItemNotFoundException;
    
    /*
     * TODO: Merge unbookSlot and unassignSlot. First check if authcode is provided.
     * If it isn't, check that the unbook is allowed. If it is, check that it is valid.
     */

    /**
     * Unbooks the specified slot for the given user no matter what -
     * admin privileges.
     * @param details Contains the CRSID of the user to unbook, and the
     * sheet, column and time of the slot they want to unbook.
     * @param authCode
     * @throws NotAllowedException 
     * @throws ItemNotFoundException 
     */
    @POST
    @Path("/groups/{groupName}/sheets/{sheetID}/{columnName}/{time}")
    public void unassignSlot(SlotBookingBean details,
            @CookieParam("authCode") String authCode)
                    throws ItemNotFoundException, NotAllowedException;

    /**
     * @param sheetID
     * @param column
     * @return A list of the slots in the specified column.
     * @throws ItemNotFoundException 
     */
    @GET
    @Path("/groups/{groupName}/sheets/{sheetID}/{column}")
    public List<Slot> listSlots(@PathParam("group") String groupName, 
            @PathParam("sheetID") String sheetID,
            @PathParam("column") String column) throws ItemNotFoundException;
    
    /**
     * @param sheet
     * @return A list of all columns in the sheet
     * @throws ItemNotFoundException 
     */
    @GET
    @Path("/groups/{groupName}/sheets/{sheetID}")
    public List<Column> listColumns(@PathParam("group") String groupName,
            @PathParam("sheetID") String sheetID)
            throws ItemNotFoundException;

    /**
     * Allows the user to user to sign up using the specified column
     * for each given comment. If an entry for a comment already exists,
     * the relevant column is overwritten.
     * @param groupID
     * @param user
     * @param commentColumnMap A mapping that specifies the column
     * that the user must use to sign up for a specific comment.
     * If it returns null, then the user can use any column for
     * that comment.
     * @param authCode
     */
    @POST
    @Path("/groups/{groupName}/whitelist/{user}")
    public void addToWhitelist(@PathParam("user") String user,
            @PathParam("groupName") String groupName,
            Map<String, String> commentColumnMap,
            @CookieParam("authCode") String authCode);


    /**
     * Forbids the user from signing up using any of the given
     * comments.
     * @param groupID
     * @param user
     * @param comments The comments to be forbidden.
     * @param authCode
     */
    @DELETE
    @Path("/groups/{groupName}/whitelist/{user}")
    public void removeFromWhitelist(@PathParam("user") String user,
            @PathParam("groupName") String groupName,
            Collection<String> comments,
            @CookieParam("authCode") String authCode);

}
