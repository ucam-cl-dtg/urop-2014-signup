package uk.ac.cam.cl.signups;

import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uk.ac.cam.cl.signups.api.Column;
import uk.ac.cam.cl.signups.api.Sheet;
import uk.ac.cam.cl.signups.api.Slot;

/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * 
 * Provides randomly generated objects for use in testing.
 */
public class Get {
    
    private static List<String> generated = new ArrayList<String>();
    private static Random random = new Random();
    private static List<Slot> sheetSlots;
    
    static {
        generated.add("");
    }
    
    public static Sheet sheetWithEmptyCols() {
        String sid = name();
        return new Sheet(sid, name(), name(), emptyColumnCollection());
    }
    
    public static Collection<Column> emptyColumnCollection() {
        LinkedList<Column> out = new LinkedList<Column>();
        for (int i = 0; i < random.nextInt(6); i++) {
            out.add(emptyColumn());
        }
        return (Collection<Column>) out;
    }

    public static Column emptyColumn() {
        return new Column(name(), new ArrayList<String>());
    }

    public static Sheet sheet() {
        sheetSlots = new ArrayList<Slot>();
        String sid = name();
        return new Sheet(sid, name(), name(), columnCollection(sid));
    }
    
    public static Column column(String sheetID) {
        String name = name();
        List<Slot> slots = slotList(sheetID, name);
        List<String> ids = new ArrayList<String>();
        for (Slot s : slots) {
            ids.add(s.get_id());
        }
        return new Column(name, ids);
    }
    
    public static Column column(String sheetID, String columnName) {
        List<Slot> slots = slotList(sheetID, columnName);
        List<String> ids = new ArrayList<String>();
        for (Slot s : slots) {
            ids.add(s.get_id());
        }
        return new Column(columnName, ids);
    }
    
    public static Slot slot(String sheetID, String columnName) {
        if (random.nextBoolean())
            return new Slot(sheetID, columnName, new Date((long) random.nextInt()), random.nextInt(60));
        else
            return new Slot(sheetID, columnName, new Date((long) random.nextInt()), random.nextInt(60), name(), name());
    }
    
    public static Collection<Column> columnCollection(String sheetID) {
        LinkedList<Column> out = new LinkedList<Column>();
        for (int i = 0; i < random.nextInt(6); i++) {
            out.add(column(sheetID));
        }
        return (Collection<Column>) out;
    }
    
    public static List<Slot> slotList(String sheetID, String columnName) {
        LinkedList<Slot> out = new LinkedList<Slot>();
        for (int i = 0; i < random.nextInt(6); i++) {
            out.add(slot(sheetID, columnName));
        }
        return out;
    }
    
    public static List<Slot> getSheetSlots() {
        return sheetSlots;
    }

    public static String name() {
        String out = "";
        while (generated.contains(out)) {
            out = Integer.toString(Math.abs(random.nextInt()));
        }
        return out;
    }

}
