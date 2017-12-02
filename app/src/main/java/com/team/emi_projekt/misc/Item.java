package com.team.emi_projekt.misc;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Item implements Serializable {
    private String
        label, //contains more than one name (%name1% | %name2%)
        comment;
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
        this.amount = 0.;
        this.amountMonth = 0.;
        this.amountYear = 0.;
        this.amountTotal = 0.;
        this.lastAdded = new Date();
        this.lastAddedOnline = new Date();
        this.lastBuyed = new Date();
    }

    public Item(String label, String comment) {
        this.label = label;
        this.comment = comment;
    }

    public Item(List<String> list) {

        DateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

        this.label = list.get(0);
        this.comment = list.get(1);

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

    public void setItem(Item item) {
        this.label = item.getLabel();
        this.comment = item.getComment();
        this.amount =  item.getAmount();
        this.amountMonth = item.getAmountMonth();
        this.amountYear = item.getAmountYear();
        this.amountTotal = item.getAmountTotal();
        this.lastAdded = item.getLastAdded();
        this.lastAddedOnline = item.getLastAddedOnline();
        this.lastBuyed = item.getLastBuyed();
    }

    private void clear() {

        this.label = null;
        this.comment = null;
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
