/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups;

import java.util.Date;
import java.util.LinkedList;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import uk.ac.cam.cl.dtg.teaching.exceptions.RemoteFailureHandler;
import uk.ac.cam.cl.signups.api.Column;
import uk.ac.cam.cl.signups.api.Group;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.SheetInfo;
import uk.ac.cam.cl.signups.api.Slot;
import uk.ac.cam.cl.signups.api.beans.ColumnBean;
import uk.ac.cam.cl.signups.api.beans.CreateColumnBean;
import uk.ac.cam.cl.signups.api.beans.GroupSheetBean;
import uk.ac.cam.cl.signups.api.beans.SlotBean;
import uk.ac.cam.cl.signups.interfaces.SignupsWebInterface;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class DatabasePopulator {
    
    public static void main(String[] args) throws Exception {
        try {
            /* Create proxy */
            ResteasyClient client = new ResteasyClientBuilder().build();
            ResteasyWebTarget target = client.target("http://urop2014.dtg.cl.cam.ac.uk/UROP_SIGNUPS/rest/");
            SignupsWebInterface service = target.proxy(SignupsWebInterface.class);
            
            Sheet sheet = new Sheet("Example sheet 1",
                    "An example sheet, for testing.", "Intel lab");
            Sheet sheet2 = new Sheet("Example sheet 2", "Another example sheet", "Mystery Location");
            SheetInfo info1 = service.addSheet(sheet);
            String id1 = info1.getSheetID();
            String sauth1 = info1.getAuthCode();
            SheetInfo info2 = service.addSheet(sheet2);
            String id2 = info2.getSheetID();
            String sauth2 = info2.getAuthCode();
            
            String groupID = "53e3a05de4b0f3b586f207df";
            String gauth = "m0ldcqfri3rk70b906pa81f1ad"; //service.addGroup(new Group(groupID));
            
/*            
            Column column = new Column("Ticker A", new LinkedList<String>());
            Column column2 = new Column("Ticker B", new LinkedList<String>());
            Column column3 = new Column("Ticker C", new LinkedList<String>());
            
//            service.addColumn(id1, new ColumnBean(column, sauth1));
*///            service.addColumn(id1, new ColumnBean(column2, sauth1));
//            service.addColumn(id1, new ColumnBean(column3, sauth1));
//            service.addColumn(id2, new ColumnBean(column, sauth2));
//            service.addColumn(id2, new ColumnBean(column2, sauth2));
//            service.addColumn(id2, new ColumnBean(column3, sauth2));
            
            service.createColumn(id1, new CreateColumnBean("Ticker A", sauth1,
                    new Date(1420120800000L), new Date(1420128000000L), 300000));
            service.createColumn(id1, new CreateColumnBean("Ticker B", sauth1,
                    new Date(1420120800000L), new Date(1420128000000L), 300000));
            service.createColumn(id1, new CreateColumnBean("Ticker C", sauth1,
                    new Date(1420120800000L), new Date(1420128000000L), 300000));
            
            service.createColumn(id2, new CreateColumnBean("Ticker A", sauth2,
                    new Date(1420128000000L), new Date(1420135200000L), 300000));
            service.createColumn(id2, new CreateColumnBean("Ticker B", sauth2,
                    new Date(1420128000000L), new Date(1420135200000L), 300000));
            service.createColumn(id2, new CreateColumnBean("Ticker C", sauth2,
                    new Date(1420128000000L), new Date(1420135200000L), 300000));
            
            service.addSheetToGroup(groupID, new GroupSheetBean(id1, gauth, sauth1));
            service.addSheetToGroup(groupID, new GroupSheetBean(id2, gauth, sauth2));
            System.out.println("done");
            
        } catch (javax.ws.rs.InternalServerErrorException e) {
            RemoteFailureHandler h = new RemoteFailureHandler();
            Object o = h.readException(e);
            System.out.println(o);
            throw e;
        }
    }

}
