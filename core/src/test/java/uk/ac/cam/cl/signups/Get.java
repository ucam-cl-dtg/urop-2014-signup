package uk.ac.cam.cl.signups;

import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import uk.ac.cam.cl.signups.api.Column;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.Slot;

/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class Get {
    
    private static List<String> generated = new ArrayList<String>();
    private static Random random = new Random();
    
    static {
        generated.add("");
    }
    
    public static Sheet sheet() {
        return new Sheet(name(), name(), name(), columnCollection());
    }
    
    public static Column column() {
        return new Column(name(), slotList());
    }
    
    public static Slot slot() {
        if (random.nextBoolean())
            return new Slot(new Date((long) random.nextInt()), random.nextInt(60));
        else
            return new Slot(new Date((long) random.nextInt()), random.nextInt(60), name(), name());
    }
    
    public static Collection<Column> columnCollection() {
        LinkedList<Column> out = new LinkedList<Column>();
        for (int i = 0; i < random.nextInt(6); i++) {
            out.add(column());
        }
        return (Collection<Column>) out;
    }
    
    public static List<Slot> slotList() {
        LinkedList<Slot> out = new LinkedList<Slot>();
        for (int i = 0; i < random.nextInt(6); i++) {
            out.add(slot());
        }
        return out;
    }

    public static String name() {
        String out = "";
        while (generated.contains(out)) {
            out = Integer.toString(Math.abs(random.nextInt()));
        }
        return out;
    }

}
