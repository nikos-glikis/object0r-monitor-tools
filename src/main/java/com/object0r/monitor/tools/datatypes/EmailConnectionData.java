package com.object0r.monitor.tools.datatypes;


public class EmailConnectionData
{
    private String username;
    private String password;
    private String host;
    private EmailType emailType = EmailType.IMAP;

    public enum EmailType
    {
        POP3,
        IMAP,
        MAPI
    }

    public String getEmailTypeString()
    {
        if (emailType == EmailType.IMAP) {
            return "imap";
        } else if (emailType ==  EmailType.MAPI) {
            return "mapi";
        } else {
            return  "pop3";
        }
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public EmailType getEmailType()
    {
        return emailType;
    }

    public void setEmailType(EmailType emailType)
    {
        this.emailType = emailType;
    }
}
