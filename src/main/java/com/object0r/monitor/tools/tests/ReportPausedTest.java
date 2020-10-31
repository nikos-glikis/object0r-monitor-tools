package com.object0r.monitor.tools.tests;

import com.object0r.monitor.tools.base.BaseReporter;
import com.object0r.monitor.tools.base.BaseTest;
import com.object0r.monitor.tools.datatypes.TimeInterval;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class ReportPausedTest extends BaseTest
{

    protected TimeInterval getRunEvery()
    {
        return new TimeInterval(6, TimeUnit.HOURS);
    }

    private boolean debug = true;

    public ReportPausedTest(BaseReporter reporter)
    {
        super(reporter);
    }

    public String getTestName()
    {
        return "Report paused test";
    }

    protected Vector<String> runTests()
    {
        ArrayList<String> pausedTests = BaseTest.getPausedTests();
        if (pausedTests.size() > 0)
        {
            StringBuilder errorSb = new StringBuilder();

            errorSb.append("Paused tests: ");

            for (String testName : pausedTests)
            {
                errorSb.append(testName).append(", ");
            }
            String error = errorSb.toString();
            error = error.substring(0, error.length() - 2);
            errors.add(error);
        }
        return errors;
    }
}
