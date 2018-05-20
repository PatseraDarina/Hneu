package org.hneu.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DimData implements Serializable {

    private int dataId;
    private String date;
    private int year;
    private int numMonth;
    private int dayMonth;

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getNumMonth() {
        return numMonth;
    }

    public void setNumMonth(int numMonth) {
        this.numMonth = numMonth;
    }

    public int getDayMonth() {
        return dayMonth;
    }

    public void setDayMonth(int dayMonth) {
        this.dayMonth = dayMonth;
    }

}
