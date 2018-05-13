package org.hneu.domain;

import java.io.Serializable;
import java.util.Date;

public class FactSale implements Serializable {

    private Date date;
    private int tovarId;
    private int dataId;
    private int manufacturerId;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTovarId() {
        return tovarId;
    }

    public void setTovarId(int tovarId) {
        this.tovarId = tovarId;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public int getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(int manufacturerId) {
        this.manufacturerId = manufacturerId;
    }
}
