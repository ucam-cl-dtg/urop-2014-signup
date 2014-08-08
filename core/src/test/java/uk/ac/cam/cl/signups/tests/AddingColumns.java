/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;

import uk.ac.cam.cl.signups.Get;
import uk.ac.cam.cl.signups.ModuleProvider;
import uk.ac.cam.cl.signups.TestDatabaseModule;
import uk.ac.cam.cl.signups.api.*;
import uk.ac.cam.cl.signups.api.beans.ColumnBean;
import uk.ac.cam.cl.signups.api.exceptions.DuplicateNameException;
import uk.ac.cam.cl.signups.api.exceptions.ItemNotFoundException;
import uk.ac.cam.cl.signups.api.exceptions.NotAllowedException;
import uk.ac.cam.cl.signups.database.DatabaseModule;
import uk.ac.cam.cl.signups.interfaces.SignupsWebInterface;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class AddingColumns {
    
    private SignupsWebInterface service =
            Guice.createInjector(ModuleProvider.provide())
            .getInstance(SignupsWebInterface.class);
    
    private Sheet sheet;
    private Column column1;
    private Column sameNameAsColumn1;
    private String id1;
    private String auth1;
    
    @Before
    public void setUp() throws DuplicateNameException {
        sheet = Get.sheet();
        column1 = Get.column(sheet.get_id(), "test-column");
        sameNameAsColumn1 = Get.column(sheet.get_id(), "test-column");
        SheetInfo info1 = service.addSheet(sheet);
        id1 = info1.getSheetID();
        auth1 = info1.getAuthCode();
        System.out.println("SheetID (just added): " + id1);
        System.out.println(service.listSheets());
    }
    
    
    @Test
    public void addColumn_exception_sheetNotFound() {
        try {
            service.addColumn("don't find me", new ColumnBean(column1, auth1));
            fail("The sheet should not exist");
        } catch (ItemNotFoundException e) {
            /* The given sheet should not be found */
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The sheet should not have been found");
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("The column should not already be added to the sheet");
        }
    }
    
    @Test
    public void addColumn_exception_columnAlreadyFound() {
        try {
            /* Add column - should be allowed */
            service.addColumn(id1, new ColumnBean(column1, auth1));
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet should be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The authCode should be correct");
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("The column should not already be added to the sheet");
        }
        try {
            /* Add same column again - expect exception */
            service.addColumn(id1, new ColumnBean(column1, auth1));
            fail("Should not be allowed to add the same column twice");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet should be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The authCode should be correct");
        } catch (DuplicateNameException e) {
            /* Should be caught - column with same name */
        }
        try {
            /* Add column with same name - expect exception */
            service.addColumn(id1, new ColumnBean(sameNameAsColumn1, auth1));
            fail("Should not be allowed to add another column with the same name");
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet should be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The authCode should be correct");
        } catch (DuplicateNameException e) {
            /* Should be caught - column with same name */
        }
    }
    
    @Test
    public void addColumn_exception_wrongAuthCode() throws DuplicateNameException {    
        try {
            service.addColumn(id1, new ColumnBean(column1, "wrong auth code"));
            fail("The authCode should be incorrect");
        } catch (ItemNotFoundException e) {
            fail("The sheet should be found");
        } catch (NotAllowedException e) {
            /* This should happen - the authCode should be incorrect */
        }
    }
    
    @Test
    public void addColumn_success() {        
        try {
            System.out.println("Testing adding a column to a sheet");
            System.out.println("Columns before:");
            System.out.println(service.listColumns(id1));
            service.addColumn(id1, new ColumnBean(column1, auth1));
            System.out.println("Columns after (should now contain a column called \"test-column\"):");
            System.out.println(service.listColumns(id1));
            assertTrue(service.listColumns(id1).contains(column1));
            System.out.println();
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
            fail("The sheet should be found");
        } catch (NotAllowedException e) {
            e.printStackTrace();
            fail("The authCode should be correct");
        } catch (DuplicateNameException e) {
            e.printStackTrace();
            fail("The column should not already be added to the sheet");
        }
    }
    
}
