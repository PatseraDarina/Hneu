package org.hneu.domain;

import java.io.Serializable;

public class DimTovar implements Serializable {

    private int tovarId;
    private String tovar;
    private int price;
    private int purchasePrice;

    public int getTovarId() {
        return tovarId;
    }

    public void setTovarId(int tovarId) {
        this.tovarId = tovarId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(int purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getTovar() {
        return tovar;
    }

    public void setTovar(String tovar) {
        this.tovar = tovar;
    }

}
