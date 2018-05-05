package org.hneu;

import java.io.Serializable;

public class DimManufacturer implements Serializable {
    private int id;
    private String manufacturer;
    private int dimManufacturer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getDimManufacturer() {
        return dimManufacturer;
    }

    public void setDimManufacturer(int dimManufacturer) {
        this.dimManufacturer = dimManufacturer;
    }
}
