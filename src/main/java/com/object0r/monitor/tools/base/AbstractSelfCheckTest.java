package com.object0r.monitor.tools.base;

import com.object0r.monitor.tools.datatypes.TimeInterval;
import com.object0r.toortools.os.OsCommandOutput;
import com.object0r.toortools.os.OsHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSelfCheckTest extends BaseTest
{
    public static class SelfCheckTarget
    {
        private final String dir;
        private final String runFile;

        public SelfCheckTarget(String dir, String runFile)
        {
            this.dir = dir;
            this.runFile = runFile;
        }

        public String getDir()
        {
            return dir;
        }

        public String getRunFile()
        {
            return runFile;
        }
    }

    public AbstractSelfCheckTest(BaseReporter reporter)
    {
        super(reporter);
    }

    @Override
    protected TimeInterval getRunEvery()
    {
        //This exists in both projects
        return new TimeInterval(30, TimeUnit.MINUTES);
    }

    protected abstract String getSelfCheckServerIp();

    protected int getSelfCheckServerPort()
    {
        return 22;
    }

    protected String getSelfCheckServerUser()
    {
        return "root";
    }

    protected String getSelfCheckSshKey()
    {
        return "id_rsa";
    }

    protected String getBuildFailedFileName()
    {
        return "buildfailed.txt";
    }

    protected int getRunFileMaxIdleHours()
    {
        return 1;
    }

    protected long getEarliestValidRunTimestamp()
    {
        // Create a LocalDateTime object for July 7, 2024
        LocalDateTime july7_2024 = LocalDateTime.of(2024, 7, 7, 0, 0);
        // Convert July 7, 2024 to a Unix timestamp
        return july7_2024.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    protected abstract Vector<SelfCheckTarget> getSelfCheckTargets();

    @Override
    protected Vector<String> runTests()
    {
        Vector<SelfCheckTarget> targets = getSelfCheckTargets();
        if (targets == null || targets.isEmpty())
        {
            errors.add(getTestName() + " - no self-check targets configured.");
            return errors;
        }

        for (SelfCheckTarget target : targets)
        {
            if (target == null)
            {
                errors.add(getTestName() + " - null self-check target found.");
                continue;
            }
            checkFailed(target.getDir());
            selfCheckDir(target.getDir(), target.getRunFile());
        }
        return errors;
    }

    private void selfCheckDir(String dir, String file)
    {
        try
        {
            String command = "cat " + dir + "/" + file;
            OsCommandOutput osCommandOutput = OsHelper.runRemoteCommandRetries(
                    getSelfCheckServerIp(),
                    getSelfCheckServerPort(),
                    command,
                    getSelfCheckServerUser(),
                    "/",
                    getSelfCheckSshKey(),
                    5,
                    500);

            if (osCommandOutput.hasError())
            {
                errors.add(getTestName() + " - " + command + " error: " + osCommandOutput.getStandardOutput() + " - " + osCommandOutput.getErrorOutput());
                return;
            }

            String lastBuild = osCommandOutput.getStandardOutput().trim();
            if (!isLatestBuildValid(lastBuild))
            {
                errors.add(getTestName() + " - " + dir + "/" + file + " last date is not valid: " + lastBuild);
            }

            String varName = dir + "_last_build_self_check_" + file;
            if (!checkIfValueHasChanged(varName, lastBuild, getRunFileMaxIdleHours(), TimeUnit.HOURS))
            {
                errors.add(getTestName() + " - " + dir + "/" + file + " has not recently changed: " + lastBuild);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errors.add(getTestName() + " - error while checking self check dir: " + e.getMessage());
        }
    }

    private void checkFailed(String dir)
    {
        try
        {
            String file = getBuildFailedFileName();
            String command = "cat " + dir + "/" + file;
            OsCommandOutput osCommandOutput = OsHelper.runRemoteCommandRetries(
                    getSelfCheckServerIp(),
                    getSelfCheckServerPort(),
                    command,
                    getSelfCheckServerUser(),
                    "/",
                    getSelfCheckSshKey(),
                    5,
                    500);

            if (!osCommandOutput.hasError())
            {
                errors.add(getTestName() + " - " + dir + "/" + file + " exists. This marker usually means a previous build failed; if builds are healthy now, remove the marker and verify it does not reappear. Last marker content: " + osCommandOutput.getStandardOutput());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errors.add(getTestName() + " - error while checking self check dir: " + e.getMessage());
        }
    }

    private boolean isLatestBuildValid(String lastBuild)
    {
        try
        {
            // Convert lastBuild to a long value
            long lastBuildTimestamp = Long.parseLong(lastBuild);
            // Check if lastBuildTimestamp is later than July 7, 2024
            return lastBuildTimestamp > getEarliestValidRunTimestamp();
        }
        catch (NumberFormatException | DateTimeParseException e)
        {
            // Handle the case where lastBuild is not a valid long value or date parsing fails
            e.printStackTrace();
            return false;
        }
    }
}
