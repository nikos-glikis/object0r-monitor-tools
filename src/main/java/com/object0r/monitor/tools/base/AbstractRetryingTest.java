package com.object0r.monitor.tools.base;

import java.util.Vector;

public abstract class AbstractRetryingTest extends BaseTest
{
    public AbstractRetryingTest(BaseReporter reporter)
    {
        super(reporter);
    }

    @FunctionalInterface
    protected interface RetryableCheck
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
        retry(getMaxRetries(), getRetryDelayMs(), action);
    }

    protected void retry(int maxRetries, int delayMs, RetryableCheck action)
    {
        retry(maxRetries, delayMs, action, false, false);
    }

    protected void retry(int maxRetries, int delayMs, RetryableCheck action, boolean increasingDelay)
    {
        retry(maxRetries, delayMs, action, increasingDelay, false);
    }

    protected void retry(int maxRetries, int delayMs, RetryableCheck action, boolean increasingDelay, boolean exponential)
    {
        retry(maxRetries, delayMs, action, increasingDelay, exponential, false);
    }

    protected void retry(int maxRetries, int delayMs, RetryableCheck action, boolean increasingDelay, boolean exponential, boolean zeroFirstIncreasingDelay)
    {
        if (action == null)
        {
            errors.add(getTestName() + " - Retryable check action is null");
            return;
        }
        if (maxRetries <= 0)
        {
            errors.add(getTestName() + " - Invalid retry config: maxRetries=" + maxRetries);
            return;
        }
        if (delayMs < 0)
        {
            errors.add(getTestName() + " - Invalid retry config: delayMs=" + delayMs);
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
                    lastErrors = new Vector<String>();
                    lastErrors.add(getTestName() + " - Retryable check returned null errors vector on attempt " + (attempt + 1) + "/" + maxRetries);
                }
                else if (attemptErrors.isEmpty())
                {
                    return;
                }
                else
                {
                    lastErrors = attemptErrors;
                }
            }
            catch (Throwable t)
            {
                t.printStackTrace();
                lastErrors = new Vector<String>();
                lastErrors.add(getTestName() + " - Error happened while running retryable check on attempt " + (attempt + 1) + "/" + maxRetries + ": " + t.toString());
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
                    Thread.currentThread().interrupt();
                    errors.addAll(lastErrors);
                    errors.add(getTestName() + " - Retry sleep interrupted on attempt " + (attempt + 1) + "/" + maxRetries + ": " + e.getMessage());
                    return;
                }
            }
        }

        errors.addAll(lastErrors);
    }
}
