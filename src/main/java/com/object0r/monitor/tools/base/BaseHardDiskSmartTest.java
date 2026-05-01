package com.object0r.monitor.tools.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

abstract public class BaseHardDiskSmartTest extends BaseTest
{
    public BaseHardDiskSmartTest(BaseReporter reporter)
    {
        super(reporter);
    }

    protected void checkHardDiskFailures(String ip, String user, Map<String, Integer> knownErrorsMap)
    {
        checkHardDiskFailures(ip, 22, user, knownErrorsMap);
    }

    protected void checkHardDiskFailures(String ip, String user, String serverName)
    {
        Map<String, Integer> map = new HashMap<String, Integer>();
        checkHardDiskFailures(ip, 22, user, serverName, map);
    }

    protected void checkHardDiskFailures(String ip, int port, String user, Map<String, Integer> knownErrorsMap)
    {
        checkHardDiskFailures(ip, port, user, null, knownErrorsMap);
    }

    protected void checkHardDiskFailures(String ip, int port, String user, String serverName, Map<String, Integer> knownErrorsMap)
    {
        try
        {
            BaseHardDiskSmart.checkHardDiskFailures(ip, port, user, serverName, knownErrorsMap);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errors.add(getTestName() + " - Error while checking failed hard disks - Exception (" + ip + "): " + e.getMessage());
        }
    }

    protected void checkHardDiskFailures(String ip, String user)
    {

        //Declare a hashmap, with key string and value integer
        Map<String, Integer> map = new HashMap<String, Integer>();
        checkHardDiskFailures(ip, user, map);
    }

    public Vector<String> getDrivesFromText(String text)
    {
        return BaseHardDiskSmart.getDrivesFromText(text);
    }

    protected void runRaidTests(String ip, int port, String device, int deviceCount)
    {
        try
        {
            BaseHardDiskSmart.runRaidTests(ip, port, device, deviceCount);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errors.add(getTestName() + " - Error while checking raid (" + ip + ":" + device + " " + deviceCount + ") tests: " + e.getMessage());
        }


    }
}
