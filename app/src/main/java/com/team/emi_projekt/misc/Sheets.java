package com.team.emi_projekt.misc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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

    public Item getItem(String sheetLabel, String itemLabel) {
        if (current.containsKey(sheetLabel)) {
            int pos = findItemInVector(current.get(sheetLabel), itemLabel);
            if(pos > -1)
                return current.get(sheetLabel).get(pos);
        }
        return null;
    }

    public void setItem(String sheetLabel, String itemLabel, Item toSetItem) {
        if (current.containsKey(sheetLabel)) {
            int pos = findItemInVector(current.get(sheetLabel), itemLabel);
            if(pos > -1)
                current.get(sheetLabel).get(pos).setItem(toSetItem);
        }
    }

    private int findItemInVector(Vector<Item> items, String label) {
        label = label.toLowerCase();
        for (Item item:items) {
            if (item.getLabel().toLowerCase().contains(label))
                return items.indexOf(item);
        }
        return -1;
    }

    public void removeAll(Item item) {
        //TODO: rework this
        for(String sheet:current.keySet())
            for(Item temp:current.get(sheet))
                if (temp.isEqual(item))
                    current.get(sheet).remove(temp);
    }

    public boolean hasSheet(String sheetLabel) {
        return current.containsKey(sheetLabel);
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
