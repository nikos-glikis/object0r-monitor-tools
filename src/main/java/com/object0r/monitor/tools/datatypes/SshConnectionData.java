package com.object0r.monitor.tools.datatypes;

public class SshConnectionData
{
    public SshConnectionData(String host, int port, String user, String directory, String privateKey)
    {
        this.host = host;
        this.port = port;
        this.user = user;
        this.directory = directory;
        this.privateKey = privateKey;
    }
    private String host;
    private int port = 22;
    private String user = "root";
    private String directory = "/";
    private String privateKey = "id_rsa";

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getDirectory()
    {
        return directory;
    }

    public void setDirectory(String directory)
    {
        this.directory = directory;
    }

    public String getPrivateKey()
    {
        return privateKey;
    }

    public void setPrivateKey(String privateKey)
    {
        this.privateKey = privateKey;
    }
}
