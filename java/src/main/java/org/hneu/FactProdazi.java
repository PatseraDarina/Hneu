package org.hneu;

import java.io.Serializable;

public class FactProdazi implements Serializable {

    private int id;
    private int idTovar;
    private int idManufacturer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTovar() {
        return idTovar;
    }

    public void setIdTovar(int idTovar) {
        this.idTovar = idTovar;
    }

    public int getIdManufacturer() {
        return idManufacturer;
    }

    public void setIdManufacturer(int idManufacturer) {
        this.idManufacturer = idManufacturer;
    }
}
