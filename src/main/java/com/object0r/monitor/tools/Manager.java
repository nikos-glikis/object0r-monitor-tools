package com.object0r.monitor.tools;


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

    static void loadProperties()
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

    static void updateProperties(String[] args)
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
}
