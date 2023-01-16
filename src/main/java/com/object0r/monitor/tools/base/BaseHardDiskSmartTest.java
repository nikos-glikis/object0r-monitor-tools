package com.object0r.monitor.tools.base;

import com.object0r.toortools.os.OsCommandOutput;
import com.object0r.toortools.os.OsHelper;

import java.util.Scanner;
import java.util.Vector;

abstract public class BaseHardDiskSmartTest extends BaseTest
{
    public BaseHardDiskSmartTest(BaseReporter reporter)
    {
        super(reporter);
    }

    protected void checkHardDiskFailures(String ip, String user)
    {
        try
        {
            OsCommandOutput osCommandOutput = OsHelper.runRemoteCommand(ip, "lsblk | awk '{print $1}'", user, "/", "id_rsa");
            if (osCommandOutput.getExitCode() != 0)
            {
                errors.add(getTestName() + " - Error while checking hard disk failure (" + ip + ") " + osCommandOutput.getErrorOutput());
            }
            else
            {
                Vector<String> drives = getDrivesFromText(osCommandOutput.getStandardOutput());
                for (String drive : drives)
                {
                    osCommandOutput = OsHelper.runRemoteCommand(ip, "smartctl /dev/" + drive + " -a", "root", "/", "id_rsa");
                    if (osCommandOutput.getExitCode() != 0)
                    {
                        errors.add(getTestName() + " - Error while checking hard disk failure - drive (" + ip + ":" + drive + "): " + osCommandOutput.getErrorOutput());
                    }
                    else
                    {
                        String output = osCommandOutput.getErrorOutput();
                        if (!output.contains("SMART overall-health self-assessment test result: PASSED") || output.contains("FAILED!"))
                        {
                            errors.add(getTestName() + " - Smart drive has failed: (" + ip + "-/dev/" + drive + ") " + osCommandOutput.getStandardOutput() + "\n" + osCommandOutput.getErrorOutput());
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errors.add(getTestName() + " - Error while checking failed hard disks - Exception (" + ip + "): " + e.toString());
        }
    }

    public Vector<String> getDrivesFromText(String text)
    {
        Vector<String> drives = new Vector<>();
        Scanner sc = new Scanner(text);
        boolean firstLine = true;
        while (sc.hasNext())
        {
            String line = sc.nextLine();
            if (!line.startsWith("├─") && !line.startsWith("│") && !line.startsWith("└") && !firstLine && !line.startsWith("loop"))
            {
                drives.add(line.trim());
            }
            firstLine = false;
        }
        return drives;
    }

    protected void runRaidTests(String ip, String device, int deviceCount)
    {
        try
        {
            OsCommandOutput osCommandOutput = OsHelper.runRemoteCommand(ip, "mdadm -D /dev/md/" + device, "root", "/", "id_rsa");
            if (osCommandOutput.getExitCode() != 0)
            {
                errors.add(getTestName() + " - Error while checking raid tests: " + ip + ":" + device + " " + deviceCount);
            }
            else
            {
                if (
                        !osCommandOutput.getStandardOutput().contains("Active Devices : " + deviceCount) ||
                                !osCommandOutput.getStandardOutput().contains("Failed Devices : 0") ||
                                !osCommandOutput.getStandardOutput().contains("Working Devices : " + deviceCount)
                )
                {
                    errors.add(getTestName() + " - Error while checking raid tests." + ip + ":" + device + " " + deviceCount + " Output: \n" + osCommandOutput.getStandardOutput());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errors.add(getTestName() + "Error while checking raid (" + ip + ":" + device + " " + deviceCount + ") tests: " + e.toString());
        }


    }
}
