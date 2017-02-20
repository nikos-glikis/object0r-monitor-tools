package com.object0r.monitor.tools.base;

import java.util.Vector;


abstract public class WordpressUpdatesTest extends BaseTest
{
    public WordpressUpdatesTest(BaseReporter reporter)
    {
        super(reporter);
    }

    protected Vector<String> runTests()
    {
        Vector<String> errors = new Vector<String>();
        WordpressUpdatesCheck wordpressUpdatesCheck = new WordpressUpdatesCheck(getUsername(), getPassword(), getUrl(), getLoginUrl(), getUserLoginField(), getUserPassField(), getSubmitButtonField());
        try
        {
            wordpressUpdatesCheck.login();
        }
        catch (Exception e)
        {
            errors.add(getUrl() + " Login failed." + e.toString());
            return errors;
        }
        errors.addAll(wordpressUpdatesCheck.checkForUpdates(getAcceptableUpdatePluginsCount()));
        return errors;
    }

    public int getAcceptableUpdatePluginsCount()
    {
        return 0;
    }

    public abstract String getUsername();

    public abstract String getPassword();

    public abstract String getUrl();

    public String getLoginUrl()
    {
        return "wp-login.php";
    }

    public String getUserLoginField()
    {
        return "user_login";
    }

    public String getUserPassField()
    {
        return "user_pass";
    }

    public String getSubmitButtonField()
    {
        return "wp-submit";
    }
}
