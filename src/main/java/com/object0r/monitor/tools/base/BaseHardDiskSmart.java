package com.object0r.monitor.tools.base;

import com.object0r.toortools.os.OsCommandOutput;
import com.object0r.toortools.os.OsHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

public class BaseHardDiskSmart
{
    public static void checkHardDiskFailures(String ip, String user, Map<String, Integer> knownErrorsMap)
    {
        checkHardDiskFailures(ip, 22, user, knownErrorsMap);
    }

    public static void checkHardDiskFailures(String ip, String user, String serverName)
    {
        Map<String, Integer> map = new HashMap<String, Integer>();
        checkHardDiskFailures(ip, 22, user, serverName, map);
    }

    public static void checkHardDiskFailures(String ip, int port, String user, Map<String, Integer> knownErrorsMap)
    {
        checkHardDiskFailures(ip, port, user, null, knownErrorsMap);
    }

    public static void checkHardDiskFailures(String ip, int port, String user, String serverName, Map<String, Integer> knownErrorsMap)
    {
        try
        {
            OsCommandOutput osCommandOutput = OsHelper.runRemoteCommandRetries(ip, port, "lsblk | awk '{print $1}'", user, "/", "id_rsa", 3, 2000);
            if (osCommandOutput.getExitCode() != 0)
            {
                throw failure("Error while checking hard disk failure (" + ip + ") " + osCommandOutput.getErrorOutput());
            }
            else
            {
                Vector<String> drives = getDrivesFromText(osCommandOutput.getStandardOutput());
                for (String drive : drives)
                {
                    String smartctlCommand = "smartctl /dev/" + drive + " -a";
                    osCommandOutput = OsHelper.runRemoteCommandRetries(ip, port, smartctlCommand, "root", "/", "id_rsa", 3, 2000);
                    String output = osCommandOutput.getStandardOutput();
                    String errorOutput = osCommandOutput.getErrorOutput();
                    if (osCommandOutput.getExitCode() != 0)
                    {
                        if (!knownErrorsMap.containsKey(drive))
                        {
                            throw failure("Error while checking hard disk failure - drive (" + ip + ":" + drive + ") command [" + smartctlCommand + "]: " + getVisibleOutput(output, errorOutput));
                        }
                        else
                        {
                            checkJsonDiskFailures(ip, port, drive, knownErrorsMap);
                        }

                    }
                    else
                    {
                        if (!output.contains("SMART overall-health self-assessment test result: PASSED") || output.contains("FAILED!"))
                        {
                            throw failure("Smart drive has failed: (" + ip + "-/dev/" + drive + ") command [" + smartctlCommand + "] " + getVisibleOutput(output, errorOutput));
                        }
                    }
                }
            }
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw failure("Error while checking failed hard disks - Exception (" + ip + "): " + e.toString(), e);
        }
    }

    public static void checkHardDiskFailures(String ip, String user)
    {

        //Declare a hashmap, with key string and value integer
        Map<String, Integer> map = new HashMap<String, Integer>();
        checkHardDiskFailures(ip, user, map);
    }

    private static void checkJsonDiskFailures(String ip, String drive, Map<String, Integer> knownErrorsMap) throws Exception
    {
        checkJsonDiskFailures(ip, 22, drive, knownErrorsMap);
    }

    private static void checkJsonDiskFailures(String ip, int port, String drive, Map<String, Integer> knownErrorsMap) throws Exception
    {
        int expectedErrorsCount = knownErrorsMap.get(drive);
        OsCommandOutput osCommandOutput = OsHelper.runRemoteCommandRetries(ip, port, "smartctl /dev/" + drive + " -j  -a", "root", "/", "id_rsa", 3, 2000);
        String output = osCommandOutput.getErrorOutput();

        //smart_status
        JSONObject jsonObject = new JSONObject(output);
        JSONObject smartStatus = jsonObject.getJSONObject("smart_status");
        boolean passed = smartStatus.getBoolean("passed");
        if (!passed)
        {
            throw failure("Error while checking hard disk failure - drive (" + ip + ":" + drive + ") - not passed: " + osCommandOutput.getErrorOutput());
        }

        //ata_smart_data
        JSONObject smartData = jsonObject.getJSONObject("ata_smart_data");
        JSONObject selfTest = smartData.getJSONObject("self_test");
        JSONObject startStatus = selfTest.getJSONObject("status");
        passed = startStatus.getBoolean("passed");
        if (!passed)
        {
            throw failure("Error while checking hard disk failure - drive (" + ip + ":" + drive + ") - not passed 2: " + osCommandOutput.getErrorOutput());
        }

        //ata_smart_error_log
        JSONObject ataSmartErrorLog = jsonObject.getJSONObject("ata_smart_error_log");
        JSONObject summary = ataSmartErrorLog.getJSONObject("summary");
        int errorCount = summary.getInt("count");
        if (errorCount != expectedErrorsCount)
        {
            throw failure("Error while checking hard disk failure - drive (" + ip + ":" + drive + ") - not passed 3: " + osCommandOutput.getErrorOutput());
        }

        //ata_smart_self_test_log
        ataSmartErrorLog = jsonObject.getJSONObject("ata_smart_self_test_log");

        JSONObject standard = ataSmartErrorLog.getJSONObject("standard");

        JSONArray jsonArray = standard.getJSONArray("table");
        if (jsonArray.length() == 0)
        {
            throw failure("Error while checking hard disk failure - drive (" + ip + ":" + drive + ") - not passed 4: " + osCommandOutput.getErrorOutput());
        }
        errorCount = standard.getInt("error_count_total");
        if (errorCount > 0)
        {
            throw failure("Error while checking hard disk failure - drive (" + ip + ":" + drive + ") - not passed 5: " + osCommandOutput.getErrorOutput());
        }
        for (int i = 0; i < jsonArray.length(); i++)
        {
            jsonObject = (JSONObject) jsonArray.get(i);
            JSONObject status = jsonObject.getJSONObject("status");
            passed = status.getBoolean("passed");
            if (!passed)
            {
                throw failure("Error while checking hard disk failure - drive (" + ip + ":" + drive + ") - not passed 5: " + osCommandOutput.getErrorOutput());
            }
        }
    }

    public static Vector<String> getDrivesFromText(String text)
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

    public static void runRaidTests(String ip, int port, String device, int deviceCount)
    {
        try
        {
            OsCommandOutput osCommandOutput = OsHelper.runRemoteCommandRetries(ip, port, "mdadm -D " + device, "root", "/", "id_rsa", 3, 2000);
            if (osCommandOutput.getExitCode() != 0)
            {
                throw failure("Error while checking raid tests: " + ip + ":" + device + " " + deviceCount);
            }
            else
            {
                if (
                        !osCommandOutput.getStandardOutput().contains("Active Devices : " + deviceCount) ||
                                !osCommandOutput.getStandardOutput().contains("Failed Devices : 0") ||
                                !osCommandOutput.getStandardOutput().contains("Working Devices : " + deviceCount)
                )
                {
                    throw failure("Error while checking raid tests." + ip + ":" + device + " " + deviceCount + " Output: \n" + osCommandOutput.getStandardOutput());
                }
            }
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw failure("Error while checking raid (" + ip + ":" + device + " " + deviceCount + ") tests: " + e.toString(), e);
        }
    }

    private static RuntimeException failure(String message)
    {
        return new IllegalStateException(message);
    }

    private static RuntimeException failure(String message, Exception cause)
    {
        return new IllegalStateException(message, cause);
    }

    private static String getVisibleOutput(String standardOutput, String errorOutput)
    {
        String std = standardOutput == null ? "" : standardOutput.trim();
        String err = errorOutput == null ? "" : errorOutput.trim();
        if (!std.isEmpty() && !err.isEmpty())
        {
            return std + "\n" + err;
        }
        if (!std.isEmpty())
        {
            return std;
        }
        return err;
    }
}
