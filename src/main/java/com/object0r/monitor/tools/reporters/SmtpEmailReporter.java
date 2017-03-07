package com.object0r.monitor.tools.reporters;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class SmtpEmailReporter extends EmailReporter
{
    private String fromName;
    private String fromEmail;
    private String username = null;
    private String password = null;
    private String smtpHost;
    private String smtpPort;

    Session session = null;

    //Constructor for SMTP with TLS Authentication

    public SmtpEmailReporter(String fromEmail, String fromName, String username, String password, String smtpHost, String smtpPort)
    {
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.username = username;
        this.password = password;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;

    }

    //Constructor for SMTP without authentication

    public SmtpEmailReporter(String fromEmail, String fromName, String smtpHost, String smtpPort)
    {
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;

    }

    private Session authenticate()
    {
        final String username = this.username;
        final String password = this.password;
        final String smtpHost = this.smtpHost;
        final String smtpPort = this.smtpPort;

        Session session;
        Authenticator auth = null;
        Properties props = new Properties();
        if (username == null || password == null) {

            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort); //TLS Port
        } else {
            props.put("mail.smtp.host", smtpHost); //SMTP Host
            props.put("mail.smtp.port", smtpPort); //TLS Port
            props.put("mail.smtp.auth", "true"); //enable authentication

            props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
            //create Authenticator object to pass in Session.getInstance argument
            auth = new Authenticator()
            {
                //override the getPasswordAuthentication method
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(username, password);
                }
            };
        }

        session = Session.getInstance(props, auth);

        return session;
    }

    private void sendEmail(Session session, String fromEmail, String toEmail, String subject, String body)
    {
        try {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(fromEmail, fromName));
            //msg.setReplyTo(InternetAddress.parse(toEmail, false));
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            Transport.send(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean report(String subject, String body)
    {
        if (this.session == null) {
            session = authenticate();
        }
        for (String recipient : recipients) {
            //recipients
            sendEmail(session, fromEmail, recipient, subject, body);
        }


        return true;
    }
}
