package com.object0r.monitor.tools.helpers;


import com.object0r.toortools.os.OsCommandOutput;
import com.object0r.toortools.os.OsHelper;

public class ServicesHelper
{
    public static boolean isServiceRunning(String host, int port, String service, String privateKey) throws Exception
    {
        //runRemoteCommand(String ip, String command, String user, String directory, String privateKeyPath)
        OsCommandOutput osCommandOutput = OsHelper.runRemoteCommand(host, port, "service " + service + " status", "root", "/", privateKey);
        if (osCommandOutput.getStandardOutput() != null && (osCommandOutput.getStandardOutput().contains("active (running)") || osCommandOutput.getStandardOutput().contains(") is running...")))
        {
            return true;
        }
        return false;
    }
}
