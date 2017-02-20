package com.object0r.monitor.tools.datatypes;

import java.io.Serializable;
import java.util.Date;

public class HistoricValue implements Serializable
{
    public HistoricValue(String value)
    {
        this.value = value;
        this.time = new Date();
    }

    public HistoricValue(String value, Date time)
    {
        this.value = value;
        this.time = time;
    }


    private String value;
    private Date time;

    public String getValue()
    {
        return value;
    }

    public double getValueAsDouble()
    {
        return Double.parseDouble(value);
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public String toString()
    {
        return "Value: " + value + " time " + time;
    }
}
