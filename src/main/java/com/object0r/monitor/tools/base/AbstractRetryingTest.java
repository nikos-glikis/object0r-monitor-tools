package com.object0r.monitor.tools.base;

import java.util.Vector;

public abstract class AbstractRetryingTest extends BaseTest
{
    public AbstractRetryingTest(BaseReporter reporter)
    {
        super(reporter);
    }

    @FunctionalInterface
    public interface RetryableCheck
    {
        Vector<String> run() throws Exception;
    }

    protected int getMaxRetries()
    {
        return 3;
    }

    protected int getRetryDelayMs()
    {
        return 2500;
    }

    protected void retry(RetryableCheck action)
    {
        retry(action, getMaxRetries(), getRetryDelayMs());
    }

    protected void retry(RetryableCheck action, int maxRetries, int delayMs)
    {
        retry(action, maxRetries, delayMs, false, false, false);
    }

    protected void retry(RetryableCheck action, int maxRetries, int delayMs, boolean increasingDelay)
    {
        retry(action, maxRetries, delayMs, increasingDelay, false, false);
    }

    protected void retry(RetryableCheck action, int maxRetries, int delayMs, boolean increasingDelay, boolean exponential)
    {
        retry(action, maxRetries, delayMs, increasingDelay, exponential, false);
    }

    protected void retry(RetryableCheck action, int maxRetries, int delayMs, boolean increasingDelay, boolean exponential, boolean zeroFirstIncreasingDelay)
    {
        if (action == null)
        {
            String caller = getRetryCaller();
            errors.add(getTestName() + " - retry action is null from " + caller);
            return;
        }
        if (maxRetries <= 0)
        {
            String caller = getRetryCaller();
            errors.add(getTestName() + " - invalid retry config from " + caller + ": maxRetries=" + maxRetries);
            return;
        }
        if (delayMs < 0)
        {
            String caller = getRetryCaller();
            errors.add(getTestName() + " - invalid retry config from " + caller + ": delayMs=" + delayMs);
            return;
        }

        Vector<String> lastErrors = new Vector<String>();

        for (int attempt = 0; attempt < maxRetries; attempt++)
        {
            try
            {
                Vector<String> attemptErrors = action.run();
                if (attemptErrors == null)
                {
                    String caller = getRetryCaller();
                    lastErrors = new Vector<String>();
                    lastErrors.add(getTestName() + " - retry returned null errors vector from " + caller + " on attempt " + (attempt + 1) + "/" + maxRetries);
                }
                else if (attemptErrors.isEmpty())
                {
                    return;
                }
                else
                {
                    lastErrors = new Vector<String>(attemptErrors);
                }
            }
            catch (Throwable t)
            {
                String caller = getRetryCaller();
                t.printStackTrace();
                lastErrors = new Vector<String>();
                lastErrors.add(getTestName() + " - error happened while running retry from " + caller + " on attempt " + (attempt + 1) + "/" + maxRetries + ": " + t.toString());
            }

            if (attempt < maxRetries - 1)
            {
                try
                {
                    int sleepMs = delayMs;
                    if (exponential)
                    {
                        sleepMs = delayMs * (1 << attempt);
                    }
                    else if (increasingDelay)
                    {
                        sleepMs = delayMs * (attempt + 1);
                        if (zeroFirstIncreasingDelay)
                        {
                            sleepMs = delayMs * attempt;
                        }
                    }
                    Thread.sleep(sleepMs);
                }
                catch (InterruptedException e)
                {
                    String caller = getRetryCaller();
                    Thread.currentThread().interrupt();
                    errors.addAll(lastErrors);
                    errors.add(getTestName() + " - retry sleep interrupted from " + caller + " on attempt " + (attempt + 1) + "/" + maxRetries + ": " + e.getMessage());
                    return;
                }
            }
        }

        errors.addAll(lastErrors);
    }

    private String getRetryCaller()
    {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String retryClassName = AbstractRetryingTest.class.getName();
        for (StackTraceElement element : stackTrace)
        {
            String className = element.getClassName();
            if (!className.equals(Thread.class.getName()) && !className.equals(retryClassName))
            {
                return className + "." + element.getMethodName() + ":" + element.getLineNumber();
            }
        }
        return "unknown caller";
    }
}
