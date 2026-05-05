package com.object0r.monitor.tools.helpers;

import java.util.concurrent.TimeUnit;

/**
 * Tracks the first time a named monitor signal entered failure state.
 *
 * A healthy update resets the signal to ok. A failing update stores the first
 * failed timestamp and returns true only after the failure has remained active
 * for the configured duration. Once the duration has passed, it keeps returning
 * true on every failing update until a healthy update resets it.
 */
public class PersistentFailureSignal
{
    private static final String STATE_FAILED = "failed";
    private static final String STATE_OK = "ok";

    private final String key;

    public PersistentFailureSignal(String key)
    {
        this.key = key;
    }

    /**
     * Updates this signal with the current health state and returns whether it
     * has been failing long enough to alert.
     */
    public boolean updateAndCheck(boolean failed, int timeUnitValue, TimeUnit timeUnit)
    {
        return updateAndCheck(key, failed, timeUnitValue, timeUnit);
    }

    /**
     * Updates the signal named by key and returns whether it has been failing
     * long enough to alert.
     */
    public static boolean updateAndCheck(String key, boolean failed, int timeUnitValue, TimeUnit timeUnit)
    {
        HistoricSignal signal = new HistoricSignal(key);
        if (!failed)
        {
            signal.saveValue(STATE_OK);
            return false;
        }

        if (!signal.hasValue(STATE_FAILED))
        {
            signal.saveValue(STATE_FAILED);
            return timeUnitValue == 0;
        }

        return signal.getAge(timeUnit) >= timeUnitValue;
    }
}
