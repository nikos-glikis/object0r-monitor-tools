package com.object0r.monitor.tools.reporters;

import com.object0r.monitor.tools.base.BaseReporter;
import com.object0r.toortools.helpers.SendEmailHelper;
import com.object0r.toortools.os.OsHelper;
import org.apache.commons.validator.routines.EmailValidator;

public class EmailReporter extends BaseReporter
{
    @Override
    public boolean report(String subject, String body)
    {
        for (String recipient:recipients)
        {
            if (OsHelper.isLinux())
            {
                SendEmailHelper.sendEmail(recipient, formatSubject("osm - " + subject, 500), body);
            }
            else
            {
                System.out.println("System is not linux. Skipping email: " + body);
            }
        }
        return true;
    }

    private String formatSubject(String subject, int maxLength)
    {
        if (subject == null)
        {
            return "";
        }
        if (subject.length() <= maxLength)
        {
            return subject;
        }
        return subject.substring(0, maxLength - 3) + "...";
    }

    protected boolean verifyRecipient(String recipient)
    {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(recipient);
    }
}
