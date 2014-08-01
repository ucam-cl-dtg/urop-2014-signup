/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;

import uk.ac.cam.cl.signups.Get;
import uk.ac.cam.cl.signups.ModuleProvider;
import uk.ac.cam.cl.signups.TestDatabaseModule;
import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.interfaces.WebInterface;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class DeletingColumns {
    
    private WebInterface service =
            Guice.createInjector(ModuleProvider.provide())
            .getInstance(WebInterface.class);
    
    private Sheet sheet;
    private Column column;
    private String id;
    private String auth;
    
    @Before
    public void setUp() throws DuplicateNameException {
        sheet = Get.sheet();
        column = Get.column(sheet.getID(), "test-column");
        sheet.addColumn(column);
        SheetInfo info = service.addSheet(sheet); // We assume this function is fine
        id = info.getSheetID();
        auth = info.getAuthCode();
    }
    
    @Test
    public void deleteColumn_exception_sheetNotFound() {
        try {
            service.deleteColumn("wrong sheetID", column.getName(), auth);
            fail("The sheet should not exist");
        } catch (ItemNotFoundException e) {
            /* The given sheet should not be found */
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The sheet should not have been found");
        }
    }
    
    @Test
    public void deleteColumn_exception_columnNotFound() {
        try {
            service.deleteColumn(id, "wrong column name", auth);
            fail("The column should not exist");
        } catch (ItemNotFoundException e) {
            /* The given column should not be found */
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The given column should not have been found");
        }
    }
    
    @Test
    public void deleteColumn_exception_wrongAuthCode() throws DuplicateNameException {    
        try {
            service.deleteColumn(id, column.getName(), "wrong auth code");
            fail("The authCode should be incorrect");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet should be found");
        } catch (NotAllowedException e) {
            /* This should happen - the authCode should be incorrect */
        }
    }
    
    @Test
    public void deleteColumn_success() {
        try {
            System.out.println("Testing deleting column from sheet");
            System.out.println("Columns before:");
            System.out.println(service.listColumns(id));
            service.deleteColumn(id, column.getName(), auth);
            System.out.println("Columns after (should now not contain a column called \"test-column\"):");
            System.out.println(service.listColumns(id));
            assertFalse(service.listColumns(id).contains(column));
            System.out.println();
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet should be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The authCode should be correct");
        }
    }
    
}
