package com.object0r.monitor.tools;


import com.object0r.monitor.tools.base.BaseReporter;
import com.object0r.monitor.tools.tests.ReportPausedTest;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.StringTokenizer;

public class Manager
{
    public static final String ALWAYS_RUN = "alwaysrun";
    private static Properties prop = new Properties();

    public static String getProperty(String key)
    {
        return prop.getProperty(key);
    }

    public static void loadProperties()
    {
        FileInputStream input;
        try
        {
            input = new FileInputStream("config.properties");
            prop.load(input);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void updateProperties(String[] args)
    {
        for (String arg : args)
        {
            if (arg.contains("="))
            {

                StringTokenizer str = new StringTokenizer(arg, "=");
                prop.setProperty(str.nextToken(), str.nextToken());
                //System.out.println(prop);
            }
        }
    }

    public static void reportPaused(BaseReporter reporter)
    {
        ReportPausedTest test = new ReportPausedTest(reporter);
        test.start();
        try
        {
            test.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
