package com.object0r.monitor.tools.base;

import com.object0r.monitor.tools.helpers.SeleniumHelper;
import com.object0r.toortools.Utilities;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Vector;

public class WordpressUpdatesCheck
{
    private String username;
    private String password;
    private String domain;
    private String loginPath = "wp-login.php";
    private WebDriver webDriver = SeleniumHelper.getNewHtmlUnitDriver();

    private String userLoginField = "user_login";
    private String userPassField = "user_pass";
    private String submitButtonId = "wp-submit";

    private String getUserLoginField()
    {
        return userLoginField;
    }

    private String getUserPassField()
    {
        return userPassField;
    }

    private String getSubmitButtonField()
    {
        return submitButtonId;
    }

    private WordpressUpdatesCheck(String username, String password, String domain)
    {
        this.username = username;
        this.password = password;
        if (!domain.startsWith("http"))
        {
            domain = "http://" + domain;
        }
        this.domain = domain;
    }

    public WordpressUpdatesCheck(String username, String password, String domain, String loginPath)
    {
        this(username, password, domain);
        this.setLoginPath(loginPath);
    }

    public WordpressUpdatesCheck(String username, String password, String domain, String loginPath, String userField, String passwordField, String buttonId)
    {
        this(username, password, domain);
        this.setLoginPath(loginPath);
        this.userLoginField = userField;
        this.userPassField = passwordField;
        this.submitButtonId = buttonId;
    }


    public Vector<String> checkForUpdates()
    {
        return checkForUpdates(0);
    }

    public Vector<String> checkForUpdates(int acceptableUpdateCount)
    {
        Vector<String> messages = new Vector<String>();

        String url = domain + "/";
        webDriver.get(url + "wp-admin/update-core.php");

        String page = webDriver.getPageSource();

        if (!page.contains("You have the latest version of WordPress"))
        {
            messages.add(url + " needs update (Wordpress Version).");
        }

        webDriver.get(url + "wp-admin/plugins.php");

        page = webDriver.getPageSource();

        if (!page.contains("plugins.php?action=deactivate"))
        {
            try
            {
                Utilities.writeStringToFile("tmp/plugins.html", page);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            messages.add(url + " Plugins page is not correctly loaded. ");
        }

        String updateString = "update now";
        if (page.contains(updateString))
        {
            if (StringUtils.countMatches(page, updateString) > acceptableUpdateCount)
            {
                messages.add(url + " needs update (plugins).");
            }
        }

        return messages;
    }

    public boolean login() throws Exception
    {
        try
        {
            String url = domain + "/";

            String loginUrl = url + loginPath;
            webDriver.get(loginUrl);

            WebElement webElement = webDriver.findElement(By.id(getUserLoginField()));

            if (webElement == null)
            {
                throw new Exception(getUserLoginField() + " does not exist.");
            }

            webElement.sendKeys(username);

            webElement = webDriver.findElement(By.id(getUserPassField()));

            if (webElement == null)
            {
                throw new Exception(getUserPassField() + " does not exist.");
            }

            webElement.sendKeys(password);

            webElement = webDriver.findElement(By.id(getSubmitButtonField()));

            if (webElement == null)
            {
                throw new Exception("wp-submit does not exist.");
            }
            webElement.submit();
            webDriver.get(url + "wp-admin/");
            String page = webDriver.getPageSource();
            if (!page.contains("Dashboard"))
            {
                //Utilities.writeStringToFile("tmp/invalid_login.html", page);
                throw new Exception("Probably Not valid login.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
        return false;
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

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    public String getLoginPath()
    {
        return loginPath;
    }

    public void setLoginPath(String loginPath)
    {
        this.loginPath = loginPath;
    }


}
