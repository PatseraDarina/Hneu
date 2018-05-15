package org.hneu.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FactSale implements Serializable {

    private int tovarId;
    private int dataId;
    private int manufacturerId;
    private int cost;
    private int quantity;

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

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static void main(String[] args) throws ParseException {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String startDate = "2010-02-12";
        int endYear = 2011;
        int endMonth = 5;
        int endDay = 2;

        for (int i = 0; i < 12; i++) {
            calendar.setTime(sdf.parse(startDate));
            calendar.add(Calendar.DATE, 30);
            Date newDate = calendar.getTime();
            startDate = sdf.format(newDate);
            System.out.println(newDate);
        }

    }
}
