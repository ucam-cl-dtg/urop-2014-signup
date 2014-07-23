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

/**
 * @author ird28
 *
 */
public interface WebInterface {

    /**
     * Creates a sheet object, stores it in the database, and returns
     * both a URL giving sign-up only access to the sheet and an
     * authentication code granting admin access to it.
     * @param details The information needed to create the sheet
     * @return The URL and the authentication code
     */
    @PUT
    @Path("/sheets")
    public Object createSheet(Sheet sheet);

    /* Listing sheets is not needed - we only want people to sign up if they have the URL */

    /**
     * Adds a column to the specified sheet, initially filled with the
     * given slots.
     * @param sheet The ID of the sheet to add a column to
     * @param authCode Authentication code returned by createSheet
     * @param initalSlots
     */
    @PUT
    @Path("/sheets/{sheet}")
    public void addColumn(@PathParam("sheet") String sheet,
            @CookieParam("authCode") String authCode, Column column);

    /**
     * Adds the slot to the column in the sheet.
     * @param sheet
     * @param authCode Authentication code returned by createSheet
     * @param column
     * @param startTime
     * @param duration Length of slot in minutes
     */
    @PUT
    @Path("/sheets/{sheet}/{column}")
    public void addSlot(@PathParam("sheet") String sheet, @CookieParam("authCode") String authCode,
            @PathParam("column") String column, Slot slot);

    /**
     * Deletes the specified column from the sheet.
     * @param sheet
     * @param authCode Authentication code returned by createSheet
     * @param column
     */
    @DELETE
    @Path("/sheets/{sheet}/{column}")
    public void deleteColumn(@PathParam("sheet") String sheet,
            @CookieParam("authCode") String authCode,
            @PathParam("column") String column);
    
    /**
     * Deletes the specified slot.
     * @param sheet
     * @param authCode
     * @param column
     * @param startTime
     */
    @DELETE
    @Path("/sheets/{sheet}/{column}/{time}")
    public void deleteSlot(@PathParam("sheet") String sheet, 
            @CookieParam("authCode") String authCode,
            @PathParam("column") String column, @PathParam("time") Date startTime);

    /**
     * Books the specified slot for the given user, but only if it is empty
     * and the user has permission to do so.
     * @param details Contains the CRSID of the user who wants to book it, and the
     * sheet, column and time of the slot they want to book.
     * @throws NotAllowedException The booking failed.
     */
    @POST
    @Path("/book")
    public void bookSlot(SlotBookingBean details) throws NotAllowedException;

    /**
     * Books the specified slot for the given user no matter what - admin privileges.
     * @param details Contains the CRSID of the user who wants to book it, and the
     * sheet, column and time of the slot they want to book.
     * @param authCode
     */
    @POST
    @Path("/assign")
    public void assignSlot(SlotBookingBean details,
            @CookieParam("authCode") String authCode);

    /**
     * Unbooks the specified slot for the given user, but only if the passed CRSID
     * is actually booked into the given slot. Will not allow the unbooking if the start
     * time has passed.
     * @param details Contains the CRSID of the user to unbook, and the
     * sheet, column and time of the slot they want to unbook.
     * @throws NotAllowedException The unbooking failed.
     */
    @POST
    @Path("/unbook")
    public void unbookSlot(SlotBookingBean details) throws NotAllowedException;

    /**
     * Unbooks the specified slot for the given user no matter what -
     * admin privileges.
     * @param details Contains the CRSID of the user to unbook, and the
     * sheet, column and time of the slot they want to unbook.
     * @param authCode
     */
    @POST
    @Path("/unassign")
    public void unbookSlot(SlotBookingBean details,
            @CookieParam("authCode") String authCode);

    /**
     * @param sheet
     * @param column
     * @return A list of the slots in the specified column.
     */
    @GET
    @Path("/sheets/{sheet}/{column}")
    public List<Slot> listSlots(@PathParam("sheet") String sheet,
            @PathParam("column") String column);
    
    /**
     * @param sheet
     * @return A list of all columns in the sheet
     */
    @GET
    @Path("/sheets/{sheet}")
    public List<String> listColumns(@PathParam("sheet") String sheet);

    /**
     * Allows the user to user to sign up using the specified column
     * for each given comment. If an entry for a comment already exists,
     * the relevant column is overwritten.
     * @param groupID
     * @param CRSID
     * @param commentColumnMap A mapping that specifies the column
     * that the user must use to sign up for a specific comment.
     * If it returns null, then the user can use any column for
     * that comment.
     * @param authCode
     */
    @POST
    @Path("/whitelist/{CRSID}/{groupID:.*}")
    public void addToWhitelist(@PathParam("CRSID") String CRSID,
            @PathParam("groupID") String groupID,
            Map<String, String> commentColumnMap,
            @CookieParam("authCode") String authCode);


    /**
     * Forbids the user from signing up using any of the given
     * comments.
     * @param groupID
     * @param CRSID
     * @param comments The comments to be forbidden.
     * @param authCode
     */
    @DELETE
    @Path("/whitelist/{CRSID}/{groupID:.*}")
    public void removeFromWhitelist(@PathParam("CRSID") String CRSID,
            @PathParam("groupID") String groupID,
            Collection<String> comments,
            @CookieParam("authCode") String authCode);

}
