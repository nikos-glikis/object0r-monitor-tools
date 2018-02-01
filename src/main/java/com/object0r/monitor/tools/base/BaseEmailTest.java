package com.object0r.monitor.tools.base;

import com.object0r.monitor.tools.datatypes.EmailConnectionData;
import com.object0r.monitor.tools.datatypes.HistoricValue;
import com.object0r.monitor.tools.helpers.EmailHelper;
import com.object0r.monitor.tools.helpers.HistoricValuesManager;
import com.object0r.toortools.helpers.DateHelper;

import javax.mail.Message;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class BaseEmailTest extends BaseTest
{
    /**
     * Finds email with subject. If it is sent more than minutesLimit minutes before an error is triggered.
     * If not found at all another error is triggered.
     *
     * @param messages     Email Messages
     * @param subject      - The subject to search for
     * @param minutesLimit - How many minutes the email is valid.
     * @return A vector with errors.
     */
    protected Vector<String> checkFirstEmailDate(Vector<Message> messages, String subject, long minutesLimit)
    {
        Date now = new Date();
        Vector<String> errors = new Vector<String>();
        try
        {
            boolean foundEmailWithSubject = false;
            for (Message message : messages)
            {
                if (message.getSubject().equals(subject))
                {
                    foundEmailWithSubject = true;

                    if (DateHelper.getDateDiff(message.getReceivedDate(), now, TimeUnit.MINUTES) > minutesLimit)
                    {
                        errors.add(subject + " - Email is older than " + minutesLimit + " minutes");
                    }
                    else
                    {
                        return errors;
                    }
                }
            }

            if (!foundEmailWithSubject)
            {
                errors.add(subject + " - didn't found email with this subject (check date).");
            }
        }
        catch (Exception e)
        {
            errors.add("Error checking for date for subject " + subject + ": " + e);
            e.printStackTrace();
        }
        return errors;
    }

    /**
     * Finds email with subject. If it is sent more than minutesLimit minutes before an error is triggered.
     * If not found at all another error is triggered.
     *
     * @param messages Email Messages
     * @param subject  - The subject to search for
     * @return A vector with errors.
     */
    protected Vector<String> checkForStrings(Vector<Message> messages, String subject, String[] stringsToFind)
    {
        Vector<String> errors = new Vector<String>();
        try
        {
            boolean foundEmailWithSubject = false;
            for (Message message : messages)
            {
                if (message.getSubject().equals(subject))
                {
                    foundEmailWithSubject = true;
                    for (String stringToFind : stringsToFind)
                    {
                        if (!message.getContent().toString().contains(stringToFind))
                        {
                            errors.add(subject + " - I didn't find: " + stringToFind + " on email with subject " + subject);
                        }
                    }
                }
            }

            if (!foundEmailWithSubject)
            {
                errors.add(subject + " - didn't found email with this subject.");
            }
        }
        catch (Exception e)
        {
            errors.add("Error checking for email strings " + subject + ": " + e);
            e.printStackTrace();
        }
        return errors;
    }

    /**
     * Overloaded getFirstEmailWithSubject to add regex support
     * Finds email with subject and returns it, or null if it doesn't.
     *
     * @param messages     Email Messages
     * @param subject      - The subject to search for
     * @param minutesLimit - How many minutes the email is valid.
     * @return - the message if it was found
     */
    protected Message getFirstEmailWithSubject(Vector<Message> messages, String subject, long minutesLimit, TimeUnit timeUnit)
    {
        Message message = getFirstEmailWithSubject(messages, subject, minutesLimit, timeUnit, false);
        return message;
    }


    /**
     * Finds email with subject and returns it, or null if it doesn't.
     * <p>
     * Assumes sorted by date DESC emails.
     *
     * @param messages     Email Messages
     * @param subject      - The subject to search for
     * @param minutesLimit - How many minutes the email is valid.
     * @return A vector with errors.
     */
    protected Message getFirstEmailWithSubject(Vector<Message> messages, String subject, long minutesLimit, TimeUnit timeUnit, boolean regex)
    {
        Date now = new Date();
        try
        {
            if (regex)
            {
                subject = Pattern.quote(subject);
            }
            for (Message message : messages)
            {
                if (regex)
                {
                    String subjectRegex = ".*?" + subject + ".*?";
                    String messageSubject = message.getSubject();
                    Pattern pattern = Pattern.compile(subjectRegex);
                    Matcher matcher = pattern.matcher(messageSubject);
                    if (matcher.find())
                    {
                        if (!(DateHelper.getDateDiff(message.getReceivedDate(), now, timeUnit) > minutesLimit))
                        {
                            return message;
                        }
                    }

                }
                else
                {
                    if (message.getSubject().equals(subject))
                    {
                        if (!(DateHelper.getDateDiff(message.getReceivedDate(), now, timeUnit) > minutesLimit))
                        {
                            return message;
                        }
                    }
                }

            }
        }
        catch (Exception e)
        {
            errors.add("Error checking for date for subject (getFirstEmailWithSubject) " + subject + ": " + e);
            e.printStackTrace();
        }
        return null;
    }

    public BaseEmailTest(BaseReporter reporter)
    {
        super(reporter);
    }

    /**
     * If an email it has been received for timeUnitCount timeUnit then it is returned.
     * <p>
     * Otherwise null is returned.
     *
     * @param variableName
     * @param emailConnectionData
     * @param subject
     * @param emailCount
     * @param timeUnitCount
     * @param timeUnit
     * @return
     */
    protected Message checkIfEmailHasBeenReceived(
            String variableName,
            EmailConnectionData emailConnectionData,
            String subject, int emailCount,
            int timeUnitCount,
            TimeUnit timeUnit)
    {
        try
        {
            Vector<Message> emails = EmailHelper.getLatestEmails(emailConnectionData, emailCount);
            return checkIfEmailHasBeenReceived(variableName, emails, subject, timeUnitCount, timeUnit);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    protected Message checkIfEmailHasBeenReceived(
            String variableName,
            Vector<Message> emails,
            String subject,
            int timeUnitCount,
            TimeUnit timeUnit
    )
    {
        return checkIfEmailHasBeenReceived(variableName, emails, subject, timeUnitCount, timeUnit, false);
    }

    protected Message checkIfEmailHasBeenReceived(String variableName, Vector<Message> emails, String subject, int timeUnitCount, TimeUnit timeUnit, boolean regex)
    {
        Message email;
        try
        {
            // using Pattern.quote in order to be able to find [] characters in subject using regex
            if (regex)
            {
                email = getFirstEmailWithSubject(emails, subject, timeUnitCount, timeUnit, regex);
            }
            else
            {
                email = getFirstEmailWithSubject(emails, subject, timeUnitCount, timeUnit);
            }

            if (email != null)
            {
                if (checkIfValueHasChanged(variableName, email.getSentDate().toString(), timeUnitCount, timeUnit))
                {
                    return email;
                }
            }

            HistoricValue historicValue = getStoredValue(variableName);
            if (historicValue != null && HistoricValuesManager.isValueOlderThan(historicValue, timeUnitCount, timeUnit))
            {
                errors.add("Haven't seen email with \"" + subject + "\" for more than " + timeUnitCount + "  " + timeUnit);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
