package com.team.emi_projekt;

import java.util.Date;

public class Item {
    //Can be moved to the constructor
    private String
        label = "",
        comment = "";
    private double
        amount = 0.,
        amountMonth = 0.,
        amountYear = 0.,
        amountTotal = 0.;
    private Date
        lastAdded = new Date(),
        lastAddedOnline = new Date(),
        lastBuyed = new Date();

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

    private void clear() {

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
