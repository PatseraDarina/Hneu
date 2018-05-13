package org.hneu.domain;

import java.io.Serializable;

public class DimManufacturer implements Serializable {

    private int id;
    private String manufacturer;

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

}
