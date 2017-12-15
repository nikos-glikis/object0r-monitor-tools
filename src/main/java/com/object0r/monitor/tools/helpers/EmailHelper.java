package com.object0r.monitor.tools.helpers;


import com.object0r.monitor.tools.datatypes.EmailConnectionData;
import com.object0r.monitor.tools.reporters.MapiEmailReporter;
import com.object0r.toortools.Utilities;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.util.MailSSLSocketFactory;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Vector;

public class EmailHelper
{
    /**
     * Deletes all emails from a pop account.
     *
     * @param pop3Host
     * @param user
     * @param password
     * @return
     * @throws Exception
     */
    public static int deletePop(String pop3Host, String user, String password) throws Exception
    {
        try
        {
            Utilities.trustEverybody();
            // get the session object
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "pop3");
            properties.put("mail.pop3.host", pop3Host);
            properties.put("mail.pop3.port", "110");
            //properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);
            // emailSession.setDebug(true);

            // create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("pop3");

            store.connect(pop3Host, user, password);

            int deletedCount = 0;
            Folder[] emailFolders = store.getDefaultFolder().list();
            for (Folder emailFolder : emailFolders)
            {

                emailFolder.open(Folder.READ_WRITE);

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        System.in));
                // retrieve the messages from the folder in an array and print it
                Message[] messages = emailFolder.getMessages();
                //System.out.println("messages.length---" + messages.length);
                for (int i = 0; i < messages.length; i++)
                {
                    deletedCount++;
                    Message message = messages[i];
//                    System.out.println("---------------------------------");
//                    System.out.println("Email Number " + (i + 1));
//                    System.out.println("Subject: " + message.getSubject());
//                    System.out.println("From: " + message.getFrom()[0]);

                    String subject = message.getSubject();

                    // set the DELETE flag to true
                    message.setFlag(Flags.Flag.DELETED, true);
                    //System.out.println("Marked DELETE for message: " + subject);

                }

                // expunges the folder to remove messages which are marked deleted
                emailFolder.close(true);
            }
            store.close();
            return deletedCount;
        }

        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }

    }

    public static Vector<Message> getLatestEmails(EmailConnectionData emailConnectionData, int count) throws Exception
    {
        if (emailConnectionData.getEmailTypeString().equals("imap"))
        {
            return getLatestEmails(emailConnectionData, count, false);
        }
        else if (emailConnectionData.getEmailTypeString().equals("mapi"))
        {
            ExchangeService service = MapiEmailReporter.initService(
                    emailConnectionData.getUsername(),
                    emailConnectionData.getPassword(),
                    emailConnectionData.getHost()
            );

            return getLatestEmails(service, count);
        }
        else
        {
            System.err.println("Invalid email type used on EmailConnectionData.");
            throw new Exception("Invalid email type used on EmailConnectionData.");
        }
    }

    public static Vector<Message> getLatestEmails(EmailConnectionData emailConnectionData, int count, boolean prefetchHeaders) throws Exception
    {

        Vector<Message> messagesVector = new Vector<Message>();
        Properties properties = new Properties();
        properties.put("mail.imap.ssl.enable", "true"); // Use SSL
        MailSSLSocketFactory sf = new MailSSLSocketFactory();

        //Trust Everybody
        sf.setTrustAllHosts(true);
        properties.put("mail.imap.ssl.trust", "*");
        properties.put("mail.imap.ssl.socketFactory", sf);
        //Trust Everybody stop

        Session emailSession = Session.getDefaultInstance(properties);

        //create the POP3 store object and connect with the pop server
        Store store = emailSession.getStore(emailConnectionData.getEmailTypeString());

        store.connect(emailConnectionData.getHost(), emailConnectionData.getUsername(), emailConnectionData.getPassword());

        //create the folder object and open it
        IMAPFolder emailFolder = (IMAPFolder) store.getFolder("INBOX");
        emailFolder.open(Folder.READ_ONLY);

        // retrieve the messages from the folder in an array and print it
      /*  Message messages[] = emailFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        if (messages.length == 0)
        {*/
        Message messages[] = emailFolder.getMessages();
        /*}*/

        if (prefetchHeaders)
        {
            FetchProfile fetchProfile = new FetchProfile();
            fetchProfile.add(FetchProfile.Item.ENVELOPE);
            emailFolder.fetch(messages, fetchProfile);
        }

        int totalCount = 0;
        for (int i = messages.length; i >= 0; i--)
        {
            if (++totalCount > count)
            {
                break;
            }
            if (i > 0)
            {
                Message message = messages[i - 1];
                //System.out.println(message.getSubject());
                messagesVector.add(message);
            }
        }

        //close the store and folder objects
        //emailFolder.close(false);
        //store.close();

        return messagesVector;
    }

    /**
     * reads the last 100 emails from the server
     * Even though we read them the mails are still considered unread
     *
     * @param service -service object that is used to communicate
     * @throws Exception - the exception must be handled from whoever is calling the function
     */
    public static Vector<Message> getLatestEmails(ExchangeService service, int count) throws Exception
    {
        ItemView view = new ItemView(count);

        FindItemsResults findResults = service.findItems(WellKnownFolderName.Inbox, view);

        Vector<Message> messages = new Vector<Message>();

        for (Object objectItem : findResults.getItems())
        {
            Item item = (Item) objectItem;
            item.load(new PropertySet(BasePropertySet.FirstClassProperties, ItemSchema.MimeContent));
            String mimeContent = item.getMimeContent().toString();
            InputStream stream = new ByteArrayInputStream(mimeContent.getBytes(StandardCharsets.UTF_8));
            Message message = new MimeMessage(null, stream);
            messages.add(message);
        }
        return messages;

    }

    public static String getTextFromMessage(Message message) throws Exception
    {
        String result = "";
        if (message.isMimeType("text/plain"))
        {
            result = message.getContent().toString();
        }
        else if (message.isMimeType("multipart/*"))
        {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception
    {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++)
        {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain"))
            {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            }
            else if (bodyPart.isMimeType("text/html"))
            {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            }
            else if (bodyPart.getContent() instanceof MimeMultipart)
            {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }
}
