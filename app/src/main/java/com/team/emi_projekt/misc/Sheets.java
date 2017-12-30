package com.team.emi_projekt.misc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

public class Sheets implements Serializable {
    private HashMap<String, Vector<Item>> current;

    public Sheets() {
        current = new HashMap<>();
    }

    public Set<String> getLabels() {
        return current.keySet();
    }

    public List<List<Object>> getItemsData(String sheetLabel) {
        List<List<Object>> result = new ArrayList<>();

        for(Item item : current.get(sheetLabel))
            result.add(item.getData());

        return result;
    }

    private String getSheetData(String sheetLabel) {
        String result = "";

        for (Item item : current.get(sheetLabel))
            result += item.getDataAsString() + "\n"; //TODO: change from += to StringFormat

        return result;
    }

    public String getSheetsData() {
        String result = "";

        for (String sheetLabel : current.keySet())
            result += sheetLabel + "{" + getSheetData(sheetLabel) + "}";

        return result;
    }

    public void addSheetData(String sheetAsString) {
        String temp = "";
        String sheetLabel = "";
        Item tempItem;

        for (char current : sheetAsString.toCharArray()) {
            if (current == '{') {
                sheetLabel = temp;
                addSheet(sheetLabel);
                temp = "";
            }
            else if (current == '\n') {
                List<String> itemData = new ArrayList<String>(Arrays.asList(temp.split("\\|")));
                tempItem = new Item(itemData, sheetLabel);
                addItem(sheetLabel, tempItem);
                temp = "";
            }
            else
                temp += current;
        }
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
            else if (searchToMerge(current.get(sheet), item))
                return;
            else
                current.get(sheet).add(item);
    }

    private boolean searchToMerge(Vector<Item> items, Item item) {
        String label = item.getLabel().toLowerCase();
        for (Item temp : items) {
            if (temp.getLabel().toLowerCase().contains(label)) {
                temp.merge(item);
                return true;
            }
        }
        return false;
    }

    public Item getItem(String sheetLabel, String itemLabel) {
        if (current.containsKey(sheetLabel)) {
            int pos = findItemInVector(current.get(sheetLabel), itemLabel);
            if(pos > -1)
                return current.get(sheetLabel).get(pos);
        }
        return null;
    }

    public void replaceItem(String sheetLabel, String itemLabel, Item toReplaceItem) {
        if (current.containsKey(sheetLabel)) {
            int pos = findItemInVector(current.get(sheetLabel), itemLabel);
            if(pos > -1)
                current.get(sheetLabel).get(pos).replaceItem(toReplaceItem);
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

    public void removeItem(String sheetLabel, String itemLabel, String itemComment) {
        for(Item item : current.get(sheetLabel)) {
            if (Objects.equals(item.getLabel(), itemLabel) && Objects.equals(item.getComment(), itemComment))
                current.get(sheetLabel).remove(item);
        }
    }

    private void removeItem(String fromSheet, String itemLabel) {
        if (current.containsKey(fromSheet)) {
            int pos = findItemInVector(current.get(fromSheet), itemLabel);
            if (pos != -1)
                current.get(fromSheet).remove(pos);
        }
    }

    public void moveItem(Item item, String prevSheet) {
        if (current.containsKey(prevSheet))
            removeItem(prevSheet, item.getLabel());
        if (current.containsKey(item.getSheet()))
            addItem(item.getSheet(), item);
    }

    public boolean hasSheet(String sheetLabel) {
        return current.containsKey(sheetLabel);
    }

    public boolean hasItemLabel(String  sheetLabel, String itemLabel) {
        if (current.containsKey(sheetLabel))
            if (findItemInVector(current.get(sheetLabel), itemLabel) != -1)
                return true;
        return false;
    }
}
