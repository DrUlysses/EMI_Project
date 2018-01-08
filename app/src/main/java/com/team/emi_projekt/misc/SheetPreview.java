package com.team.emi_projekt.misc;

import java.util.List;

public class SheetPreview {

    private String sheetName;
    private List<String> itemNames;
    private List<String> itemComments;

    SheetPreview(String sheetName, List<String> itemNames, List<String> itemComments) {
        this.sheetName = sheetName;
        this.itemNames = itemNames;
        this.itemComments = itemComments;
    }

    public String getSheetName() {
        return sheetName;
    }


    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
    }

    public void setItemNameAndComment(String currentName, String newName, String newComment) {
        int index = itemNames.indexOf(currentName);
        if (index != -1) {
            itemNames.set(index, newName);
            itemComments.set(index, newComment);
        }
    }

    public List<String> getItemComments() {
        return itemComments;
    }

    public void setItemComments(List<String> itemComments) {
        this.itemComments = itemComments;
    }


}
