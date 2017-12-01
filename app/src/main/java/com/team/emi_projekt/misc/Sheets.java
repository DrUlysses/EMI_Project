package com.team.emi_projekt.misc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Sheets implements Serializable {
    //TODO: change this to the another data structure
    private HashMap<String, Vector<Item>> current;

    public Sheets() {
        current = new HashMap<>();
    }

    public List<SheetPreview> getPreviews() {
        List<SheetPreview> list = new ArrayList<>();
        for (String temp: current.keySet())
            list.add(new SheetPreview(temp, getLabels(temp), getComments(temp)));
        return list;
    }

    public void addSheet(String name) {
        if (!current.containsKey(name))
            current.put(name, new Vector<Item>());
    }

    private List<String> getLabels(String sheetName) {
        List<String> list = new ArrayList<>();
        if (current.containsKey(sheetName)) {
            for (Item temp : current.get(sheetName))
                list.add(temp.getLabel());
        }
        return list;
    }

    private List<String> getComments(String sheetName) {
        List<String> list = new ArrayList<>();
        if (current.containsKey(sheetName)) {
            for (Item temp : current.get(sheetName))
                list.add(temp.getComment());
        }
        return list;
    }

    public void addItem(String sheet, Item item) {
        if (current.containsKey(sheet))
            if (current.get(sheet).contains(item))
                return;
            else if (searchToAdd(current.get(sheet), item))
                return;
            else
                current.get(sheet).add(item);
    }

    private boolean searchToAdd(Vector<Item> items, Item item) {
        String label = item.getLabel().toLowerCase();
        for (Item temp : items) {
            if (temp.getLabel().toLowerCase().contains(label)) {
                temp.merge(item);
                return true;
            }
        }
        return false;
    }

}
