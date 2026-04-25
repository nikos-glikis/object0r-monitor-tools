package com.object0r.monitor.tools.base;

import com.object0r.toortools.Utilities;

import java.util.concurrent.TimeUnit;

public abstract class AbstractPersonalTest extends BaseTest
{
    public AbstractPersonalTest(BaseReporter reporters, boolean forceRun)
    {
        super(reporters, forceRun);
    }

    public AbstractPersonalTest(BaseReporter reporter)
    {
        super(reporter);
    }

    protected abstract String getPersonalMonitorUrl();

    protected abstract String getPersonalMonitorUser();

    public void checkPersonal(String variableName, int timeUnits, TimeUnit timeUnit)
    {
        checkPersonal(variableName, timeUnits, timeUnit, "");
    }

    public void checkPersonal(String variableName, int timeUnits, TimeUnit timeUnit, String extraMessage)
    {
        try
        {
            if (!extraMessage.matches("[a-zA-Z0-9\\s\\p{Punct}]*"))
            {
                throw new IllegalArgumentException("Extra message contains invalid characters.");
            }

            String monitorUrl = getPersonalMonitorUrl();
            String monitorUser = getPersonalMonitorUser();
            String baseEndpoint = monitorUrl + (monitorUrl.contains("?") ? "&" : "?") + "user=" + monitorUser;
            String value = Utilities.readUrl(baseEndpoint + "&action=get&variable=" + variableName);
            if (!checkIfValueHasChanged(variableName, value, timeUnits, timeUnit))
            {
                if (!extraMessage.trim().equals(""))
                {
                    extraMessage = " Extra Message: " + extraMessage;
                }
                errors.add("Personal value:" + variableName + " has expired. Mark completed here: " + baseEndpoint + "&action=set&variable=" + variableName + "&message=" + extraMessage);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errors.add(getTestName() + " Error while checking personal: " + variableName + " Exception is: " + e);
        }
    }
}
