package com.team.emi_projekt;

import java.util.HashMap;
import java.util.Vector;

public class Sheets {
    //TODO: change this to the another data structure
    HashMap<String, Vector<Item>> current;

    private String
            range, /* range is A1 notation {%SheetName(first visible if nothing wrote)% ! %from% : %until%} */
            id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

}
