package com.object0r.monitor.tools.helpers;

import com.object0r.monitor.tools.datatypes.HistoricValue;

import java.util.concurrent.TimeUnit;

public class HistoricSignal
{
    private final String key;

    public HistoricSignal(String key)
    {
        this.key = key;
    }

    public HistoricValue get()
    {
        return HistoricValuesManager.getSaved(key);
    }

    public boolean exists()
    {
        return get() != null;
    }

    public void touch()
    {
        HistoricValuesManager.saveValue(new HistoricValue("seen"), key);
    }

    public void saveValue(String value)
    {
        HistoricValuesManager.saveValue(new HistoricValue(value), key);
    }

    public boolean hasValue(String value)
    {
        HistoricValue historicValue = get();
        return historicValue != null && historicValue.getValue().equals(value);
    }

    public boolean wasSeenRecently(int time, TimeUnit timeUnit)
    {
        if (!exists())
        {
            return false;
        }

        return getAge(timeUnit) <= time;
    }

    public long getAge(TimeUnit timeUnit)
    {
        HistoricValue historicValue = get();
        if (historicValue == null)
        {
            return Long.MAX_VALUE;
        }

        long diffMillis = System.currentTimeMillis() - historicValue.getTime().getTime();
        return timeUnit.convert(diffMillis, TimeUnit.MILLISECONDS);
    }
}
