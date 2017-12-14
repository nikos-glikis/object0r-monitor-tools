package com.object0r.monitor.tools.reporters;


import com.object0r.toortools.Utilities;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

import java.net.URI;

public class MapiEmailReporter extends EmailReporter
{

    private String from;
    private ExchangeService service;

    public MapiEmailReporter(String from, String username, String password)
    {
        this.from = from;
        this.service = initService(username, password);
    }


    /**
     * @param username - username for exchange service
     * @param password -  password for exchange service
     * @return - the exchange service that was created
     */
    public static ExchangeService initService(String username, String password)
    {
        try
        {
            ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
            ExchangeCredentials credentials = new WebCredentials(username, password);
            service.setCredentials(credentials);

            //this url must be updated based on your mail URI
            //e.g service.setUrl(new URI("https://outlook.com/ews/Exchange.asmx"));
            service.setUrl(new URI("https://YOUR-DOMAIN/ews/Exchange.asmx"));
            return service;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize exchange service");
        }

    }

    /**
     * @param from      - who is sending the email
     * @param recipient - who is gonna receive the email
     * @param subject   - subject of the mail
     * @param body      - body of the mail
     */
    private void sendEmail(String from,
                           String recipient,
                           String subject,
                           String body)
    {
        try
        {
            EmailMessage msg = new EmailMessage(this.service);
            EmailAddress fromEmailAddress = new EmailAddress(from);
            msg.setFrom(fromEmailAddress);
            EmailAddress recepientEmailAddress = new EmailAddress(recipient);
            msg.getToRecipients().add(recepientEmailAddress);
            msg.setSubject(subject);
            msg.setBody(MessageBody.getMessageBodyFromText(body));
            msg.send();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean report(String subject, String body)
    {
        for (String recipient : recipients)
        {
            //recipients
            sendEmail(this.from, recipient, subject, body);
        }
        return true;
    }
}
