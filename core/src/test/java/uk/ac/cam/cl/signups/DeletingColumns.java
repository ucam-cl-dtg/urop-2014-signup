/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;

import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.beans.ColumnBean;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class DeletingColumns {
    
    private WebInterface service =
            Guice.createInjector(new DatabaseModule())
            .getInstance(WebInterface.class);
    
    private Sheet sheet;
    private Column column;
    private String id;
    private String auth;
    
    @Before
    public void setUp() throws DuplicateNameException {
        sheet = Get.sheet();
        column = new Column("test-column", Get.slotList());
        sheet.addColumn(column);
        SheetInfo info = service.addSheet(sheet); // We assume this function is fine
        id = info.getSheetID();
        auth = info.getAuthCode();
    }
    
    @After
    public void removeDatabaseEntry() throws ItemNotFoundException, NotAllowedException {
        service.deleteSheet(id, auth); // We assume this function is fine
    }
    
    @Test
    public void nonExistentSheetTest() {
        try {
            service.deleteColumn("don't find me", column.getName(), auth);
            fail("The sheet should not exist");
        } catch (ItemNotFoundException e) {
            /* The given sheet should not be found */
        } catch (NotAllowedException e) {
            fail("The sheet should not have been found");
        }
    }
    
    @Test
    public void incorrectAuthCodeTest() throws DuplicateNameException {    
        try {
            service.deleteColumn(id, column.getName(), "wrong auth code");
            fail("The authCode should almost certainly incorrect");
        } catch (ItemNotFoundException e) {
            fail("The sheet should be found");
        } catch (NotAllowedException e) {
            /* This should happen - the authCode should be incorrect */
        }
    }
    
    @Test
    public void deleteColumn() {
        try {
            System.out.println("Testing deleting column from sheet");
            System.out.println("Columns before:");
            System.out.println(service.listColumns(id));
            service.deleteColumn(id, column.getName(), auth);
            System.out.println("Columns after (should now not contain a column called \"test-column\"):");
            System.out.println(service.listColumns(id));
            //assertFalse(service.listColumns(id).contains(column)); TODO: change List<Slot> to Set<Slot> in Column so that we can properly check for equality of the set of slots
            System.out.println();
        } catch (ItemNotFoundException e) {
            fail("The sheet should be found");
        } catch (NotAllowedException e) {
            fail("The authCode should be correct");
        }
    }
    
}
