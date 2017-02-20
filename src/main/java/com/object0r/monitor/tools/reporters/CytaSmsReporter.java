package com.object0r.monitor.tools.reporters;

import com.object0r.monitor.tools.base.BaseReporter;
import com.object0r.toortools.helpers.CytaSmsHelper;

public class CytaSmsReporter extends BaseReporter
{
    private String username, secretKey;

    public CytaSmsReporter(String username, String secretKey)
    {
        this.username = username;
        this.secretKey = secretKey;
    }

    @Override
    public boolean report(String subject, String body)
    {
        for (String recipient : recipients)
        {
            try
            {
                CytaSmsHelper.sendSms(subject + " - " + body, recipient, username, secretKey);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return true;
    }

    protected boolean verifyRecipient(String recipient)
    {
        try
        {
            Integer.parseInt(recipient);
            if (recipient.charAt(0) != '9')
            {
                System.out.println(recipient + " is not a valid cyprus phone number (First digit is not 9)");
                return false;
            }
            if (recipient.length() != 8)
            {
                System.out.println(recipient + " is not a valid cyprus phone number (Total length is not 8)");
                return false;
            }
            return true;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(recipient + " is not a valid cyprus phone number (" + e.toString() + ")");

            return false;
        }
    }
}
