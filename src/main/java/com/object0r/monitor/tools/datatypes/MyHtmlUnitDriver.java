package com.object0r.monitor.tools.datatypes;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class MyHtmlUnitDriver extends HtmlUnitDriver
{
    public MyHtmlUnitDriver(BrowserVersion browserVersion)
    {
        super(browserVersion);
    }

    @Override
    protected WebClient modifyWebClient(WebClient client)
    {
        //currently does nothing, but may be changed in future versions
        WebClient modifiedClient = super.modifyWebClient(client);

        modifiedClient.getOptions().setThrowExceptionOnScriptError(false);
        return modifiedClient;
    }
}
