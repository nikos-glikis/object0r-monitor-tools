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
            OsCommandOutput osCommandOutput = OsHelper.runRemoteCommandRetries
                    (
                            getHost(), getPort(), "su -  " + getUser() + " -c 'cd " + getPath() + " && wp core update'", getSshUser(), getPath(), getPrivateKeyPath()
                            , 5, 500
                    );
            if (osCommandOutput.getExitCode() != 0)
            {
                throw new Exception(getTestName() + " - Some error happened while runni WpCli tests (update core), error code is not zero, error output is:" + osCommandOutput.getErrorOutput());
            }
            if (!osCommandOutput.getStandardOutput().contains("Success: WordPress is up to date."))
            {
                throw new Exception(getTestName() + " - Some error happened while runni WpCli tests (update core), error code is not zero, standard output is:" + osCommandOutput.getStandardOutput());
            }

            osCommandOutput = OsHelper.runRemoteCommandRetries
                    (
                            getHost(), getPort(), "su - " + getUser() + " -c ' cd " + getPath() + " &&  wp plugin update --all'", getSshUser(), getPath(), getPrivateKeyPath(), 5, 500
                    );
            if (osCommandOutput.getExitCode() != 0)
            {
                throw new Exception(getTestName() + " - Some error happened while runni WpCli tests (update plugin), error code is not zero, error output is:" + osCommandOutput.getErrorOutput());
            }
            if (!osCommandOutput.getStandardOutput().contains("Success: Plugin already updated."))
            {
                throw new Exception(getTestName() + " - Some error happened while runni WpCli tests (update plugin), error code is not zero, standard output is:" + osCommandOutput.getStandardOutput());
            }

            osCommandOutput = OsHelper.runRemoteCommandRetries(getHost(), getPort(), "su - " + getUser() + " -c ' cd " + getPath() + " && wp theme update --all'", getSshUser(), getPath(), getPrivateKeyPath(), 5, 500);
            if (osCommandOutput.getExitCode() != 0)
            {
                throw new Exception(getTestName() + " - Some error happened while runni WpCli tests (update theme), error code is not zero, error output is:" + osCommandOutput.getErrorOutput());
            }
            if (!osCommandOutput.getStandardOutput().contains("Success: Theme already updated."))
            {
                throw new Exception(getTestName() + " - Some error happened while runni WpCli tests (update themes), error code is not zero, standard output is:" + osCommandOutput.getStandardOutput());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errors.add(getTestName() + " - Some error happened while running WpCli tests:" + e.getMessage());
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
