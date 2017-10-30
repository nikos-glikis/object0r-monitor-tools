package com.object0r.monitor.tools.validation;

import com.object0r.monitor.tools.base.BaseReporter;
import com.object0r.monitor.tools.base.BaseTest;

import java.util.Vector;

public class AlwaysFailTest extends BaseTest
{
    public AlwaysFailTest(BaseReporter reporter)
    {
        super(reporter);
        sendAggregated = true;
    }

    public String getTestName()
    {
        return "Always Fail";
    }

    protected Vector<String> runTests()
    {
        errors.add("This is always fail test.");
        errors.add("This is always fail test2.");
        errors.add("This is always fail test3.");
        return errors;
    }
}