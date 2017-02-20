package com.object0r.monitor.tools.datatypes;


import java.util.concurrent.TimeUnit;

public class TimeInterval
{
    private TimeUnit timeUnit = TimeUnit.HOURS;
    private long count = 24;

    public TimeInterval(long count, TimeUnit timeUnit)
    {
        this.count = count;
        this.timeUnit = timeUnit;
    }

    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }

    public long getCount()
    {
        return count;
    }
}
