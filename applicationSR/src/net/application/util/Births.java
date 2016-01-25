package net.application.util;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Markus
 */
public class Births implements Serializable {

    private static final long serialVersionUID = -6693851535463524178L;
    protected String year;
    protected Integer amount;
    protected Date date;

    public Date getDate() {
        return date;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Births() {
    }

    public Births(String year, Integer amount) {
        this.year = year;
        this.amount = amount;
    }

    /**
     *
     * @param date the value of date
     * @param amount the value of amount
     */
    public Births(Date date, Integer amount) {
        this.amount = amount;
        this.date = date;
    }

}

