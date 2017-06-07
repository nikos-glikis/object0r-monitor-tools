package com.object0r.monitor.tools.base;

import com.object0r.toortools.os.OsCommandOutput;
import com.object0r.toortools.os.OsHelper;

import java.util.Vector;


abstract public class WordpressWpCliTest extends BaseTest
{
    public WordpressWpCliTest(BaseReporter reporter)
    {
        super(reporter);
    }

    protected Vector<String> runTests()
    {
        try
        {
            OsCommandOutput osCommandOutput = OsHelper.runRemoteCommand(getHost(), getPort(), "wp core check-update --allow-root", getSshUser(), getPath(), getPrivateKeyPath());
            if (osCommandOutput.getExitCode() != 0)
            {
                throw new Exception("Some error happened while runni WpCli tests (update core), error code is not zero, error output is:" + osCommandOutput.getErrorOutput());
            }
            if (!osCommandOutput.getStandardOutput().contains("Success: WordPress is at the latest version."))
            {
                throw new Exception("Some error happened while runni WpCli tests (update core), error code is not zero, standard output is:" + osCommandOutput.getStandardOutput());
            }

            osCommandOutput = OsHelper.runRemoteCommand(getHost(), getPort(), "wp plugin update --allow-root --all", getSshUser(), getPath(), getPrivateKeyPath());
            if (osCommandOutput.getExitCode() != 0)
            {
                throw new Exception("Some error happened while runni WpCli tests (update plugin), error code is not zero, error output is:" + osCommandOutput.getErrorOutput());
            }
            if (!osCommandOutput.getStandardOutput().contains("Success: Plugin already updated."))
            {
                throw new Exception("Some error happened while runni WpCli tests (update plugin), error code is not zero, standard output is:" + osCommandOutput.getStandardOutput());
            }

            osCommandOutput = OsHelper.runRemoteCommand(getHost(), getPort(), "chown -R " + getUser() + ":" + getUser() + " " + getPath(), getSshUser(), getPath(), getPrivateKeyPath());
            if (osCommandOutput.getExitCode() != 0)
            {
                throw new Exception("Some error happened while runni WpCli tests (chown stuff), error code is not zero, error output is:" + osCommandOutput.getErrorOutput());
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            errors.add(getTestName() + " - Some error happened while runni WpCli tests:" + e.getMessage());
        }
        return errors;
    }

    public int getAcceptableUpdatePluginsCount()
    {
        return 0;
    }


    public abstract String getPrivateKeyPath();

    public abstract String getPath();

    public abstract String getUser();

    public abstract String getHost();

    public abstract int getPort();

    public abstract String getSshUser();
}
