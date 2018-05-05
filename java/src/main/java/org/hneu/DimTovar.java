package org.hneu;

import java.io.Serializable;

public class DimTovar implements Serializable {

    private int id;
    private String tovar;
    private double price;
    private double purchasePrice;
    private int dimTovarId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTovar() {
        return tovar;
    }

    public void setTovar(String tovar) {
        this.tovar = tovar;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public int getDimTovarId() {
        return dimTovarId;
    }

    public void setDimTovarId(int dimTovarId) {
        this.dimTovarId = dimTovarId;
    }
}
