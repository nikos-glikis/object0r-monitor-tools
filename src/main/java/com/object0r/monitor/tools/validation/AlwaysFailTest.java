package com.object0r.monitor.tools.validation;

import com.object0r.monitor.tools.base.BaseReporter;
import com.object0r.monitor.tools.base.BaseTest;

import java.util.Vector;

public class AlwaysFailTest extends BaseTest
{
    public AlwaysFailTest(BaseReporter reporter)
    {
        super(reporter);
    }

    public String getTestName()
    {
        return "Always Fail";
    }



    protected Vector<String> runTests()
    {
        Vector<String> errorMessages = new Vector<String>();
        errorMessages.add("This is always fail test.");
        return errorMessages;
    }
}