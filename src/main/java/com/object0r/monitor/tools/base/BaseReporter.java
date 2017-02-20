package com.object0r.monitor.tools.base;

import java.util.Vector;

/**
 * Basic reporter functionality.
 */
public abstract class BaseReporter
{
    protected Vector<String> recipients = new Vector<String>();

    public abstract boolean report(String subject, String body);

    public BaseReporter addRecipient(String recipient) throws Exception
    {
        if (verifyRecipient(recipient))
        {
            recipients.add(recipient);
        }
        else
        {
            throw new Exception("Recipient is not valid.");
        }
        return this;
    }

    protected abstract boolean verifyRecipient(String recipient);
}
