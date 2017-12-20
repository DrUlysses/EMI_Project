package com.team.emi_projekt.misc;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Item implements Serializable {
    private String
        label, //contains more than one name (%name1% | %name2%)
        comment,
        sheet;
    private Double
        amount,
        amountMonth,
        amountYear,
        amountTotal;
    private Date
        lastAdded,
        lastAddedOnline,
        lastBuyed;

    public Item() {
        this.label = "";
        this.comment = "";
        this.sheet = "";
        this.amount = 0.;
        this.amountMonth = 0.;
        this.amountYear = 0.;
        this.amountTotal = 0.;
        this.lastAdded = new Date();
        this.lastAddedOnline = new Date();
        this.lastBuyed = new Date();
    }

    public Item(String label, String comment, String sheet) {
        this.label = label;
        this.comment = comment;
        this.sheet = sheet;
    }

    public Item(List<String> list, String sheet) {

        DateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

        this.label = list.get(0);
        this.comment = list.get(1);

        this.sheet = sheet;

        this.amount = Double.parseDouble(list.get(2));
        this.amountMonth = Double.parseDouble(list.get(3));
        this.amountYear = Double.parseDouble(list.get(4));
        this.amountTotal = Double.parseDouble(list.get(5));

        try {
            this.lastAdded = format.parse(list.get(6));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            this.lastAddedOnline = format.parse(list.get(7));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            this.lastBuyed = format.parse(list.get(8));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public List<Object> getData() {
        List<Object> result = new ArrayList<>();

        result.add(this.label);
        result.add(this.comment);

        result.add(Double.toString(this.amount));
        result.add(Double.toString(this.amountMonth));
        result.add(Double.toString(this.amountYear));
        result.add(Double.toString(this.amountTotal));

        result.add(this.lastAdded.toString());
        result.add(this.lastAddedOnline.toString());
        result.add(this.lastBuyed.toString());

        return result;
    }

    public boolean isEqual(Item compareTo) {
        if (Objects.equals(this.sheet, compareTo.getSheet()))
            if (Objects.equals(this.label, compareTo.getLabel()))
                if (Objects.equals(this.comment, compareTo.getComment()))
                    if (this.amountTotal.compareTo(getAmountTotal()) == 0.)
                        return true;
        return false;

    }

    public void merge(Item item) {

        if (!this.label.toLowerCase().contains(item.getLabel().toLowerCase()))
            this.setLabel(this.label + " " + item.getLabel());

        if (!this.comment.toLowerCase().equals(item.getComment().toLowerCase()))
            this.setComment(this.comment + "\n\n" + item.getComment());

        this.amount += item.getAmount();
        this.amountMonth += item.getAmountMonth();
        this.amountYear += item.getAmountYear();
        this.amountTotal += item.getAmountTotal();

        if (this.lastAdded.before(item.getLastAdded()))
            this.lastAdded = item.getLastAdded();

        if (this.lastAddedOnline.before(item.getLastAddedOnline()))
            this.lastAddedOnline = item.getLastAddedOnline();

        if (this.lastBuyed.before(item.getLastBuyed()))
            this.lastBuyed = item.getLastBuyed();

    }

    public void replaceItem(Item item) {
        this.label = item.getLabel();
        this.comment = item.getComment();
        this.sheet = item.getSheet();
        this.amount =  item.getAmount();
        this.amountMonth = item.getAmountMonth();
        this.amountYear = item.getAmountYear();
        this.amountTotal = item.getAmountTotal();
        this.lastAdded = item.getLastAdded();
        this.lastAddedOnline = item.getLastAddedOnline();
        this.lastBuyed = item.getLastBuyed();
    }

    public void clear() {

        this.label = null;
        this.comment = null;
        this.sheet = null;
        this.amount = null;
        this.amountMonth = null;
        this.amountYear = null;
        this.amountTotal = null;
        this.lastAdded = null;
        this.lastAddedOnline = null;
        this.lastBuyed = null;

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSheet() {
        return sheet;
    }

    public void setSheet(String sheet) {
        this.sheet = sheet;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmountMonth() {
        return amountMonth;
    }

    public void setAmountMonth(double amountMonth) {
        this.amountMonth = amountMonth;
    }

    public double getAmountYear() {
        return amountYear;
    }

    public void setAmountYear(double amountYear) {
        this.amountYear = amountYear;
    }

    public double getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(double amountTotal) {
        this.amountTotal = amountTotal;
    }

    public Date getLastAdded() {
        return lastAdded;
    }

    public void setLastAdded(Date lastAdded) {
        this.lastAdded = lastAdded;
    }

    public Date getLastAddedOnline() {
        return lastAddedOnline;
    }

    public void setLastAddedOnline(Date lastAddedOnline) {
        this.lastAddedOnline = lastAddedOnline;
    }

    public Date getLastBuyed() {
        return lastBuyed;
    }

    public void setLastBuyed(Date lastBuyed) {
        this.lastBuyed = lastBuyed;
    }
}
