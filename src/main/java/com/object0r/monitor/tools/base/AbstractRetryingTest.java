package com.object0r.monitor.tools.base;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

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
        errors.addAll(getErrorsAfterRetry(action));
    }

    protected void retry(RetryableCheck action, int maxRetries, int delayMs)
    {
        errors.addAll(getErrorsAfterRetry(action, maxRetries, delayMs));
    }

    protected void retry(RetryableCheck action, int maxRetries, int delayMs, boolean increasingDelay)
    {
        errors.addAll(getErrorsAfterRetry(action, maxRetries, delayMs, increasingDelay));
    }

    protected void retry(RetryableCheck action, int maxRetries, int delayMs, boolean increasingDelay, boolean exponential)
    {
        errors.addAll(getErrorsAfterRetry(action, maxRetries, delayMs, increasingDelay, exponential));
    }

    protected void retry(RetryableCheck action, int maxRetries, int delayMs, boolean increasingDelay, boolean exponential, boolean zeroFirstIncreasingDelay)
    {
        errors.addAll(getErrorsAfterRetry(action, maxRetries, delayMs, increasingDelay, exponential, zeroFirstIncreasingDelay));
    }

    private Vector<String> getErrorsAfterRetry(RetryableCheck action)
    {
        return getErrorsAfterRetry(action, getMaxRetries(), getRetryDelayMs());
    }

    private Vector<String> getErrorsAfterRetry(RetryableCheck action, int maxRetries, int delayMs)
    {
        return getErrorsAfterRetry(action, maxRetries, delayMs, false, false, false);
    }

    private Vector<String> getErrorsAfterRetry(RetryableCheck action, int maxRetries, int delayMs, boolean increasingDelay)
    {
        return getErrorsAfterRetry(action, maxRetries, delayMs, increasingDelay, false, false);
    }

    private Vector<String> getErrorsAfterRetry(RetryableCheck action, int maxRetries, int delayMs, boolean increasingDelay, boolean exponential)
    {
        return getErrorsAfterRetry(action, maxRetries, delayMs, increasingDelay, exponential, false);
    }

    private Vector<String> getErrorsAfterRetry(RetryableCheck action, int maxRetries, int delayMs, boolean increasingDelay, boolean exponential, boolean zeroFirstIncreasingDelay)
    {
        if (action == null)
        {
            String caller = getRetryCaller();
            Vector<String> configErrors = new Vector<String>();
            configErrors.add(getTestName() + " - retry action is null from " + caller);
            return configErrors;
        }
        if (maxRetries <= 0)
        {
            String caller = getRetryCaller();
            Vector<String> configErrors = new Vector<String>();
            configErrors.add(getTestName() + " - invalid retry config from " + caller + ": maxRetries=" + maxRetries);
            return configErrors;
        }
        if (delayMs < 0)
        {
            String caller = getRetryCaller();
            Vector<String> configErrors = new Vector<String>();
            configErrors.add(getTestName() + " - invalid retry config from " + caller + ": delayMs=" + delayMs);
            return configErrors;
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
                    return new Vector<String>();
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
                    Vector<String> interruptedErrors = new Vector<String>(lastErrors);
                    interruptedErrors.add(getTestName() + " - retry sleep interrupted from " + caller + " on attempt " + (attempt + 1) + "/" + maxRetries + ": " + e.getMessage());
                    return interruptedErrors;
                }
            }
        }

        return lastErrors;
    }

    protected void retryAndAlertIfFailurePersists(String variableName, int timeUnitValue, TimeUnit timeUnit, RetryableCheck action)
    {
        retryAndAlertIfFailurePersists(variableName, timeUnitValue, timeUnit, action, getMaxRetries(), getRetryDelayMs());
    }

    protected void retryAndAlertIfFailurePersists(String variableName, int timeUnitValue, TimeUnit timeUnit, RetryableCheck action, int maxRetries, int delayMs)
    {
        Vector<String> finalErrors = getErrorsAfterRetry(action, maxRetries, delayMs);
        errors.addAll(getErrorsIfFailurePersists(variableName, finalErrors, timeUnitValue, timeUnit));
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
