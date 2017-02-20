package com.object0r.monitor.tools.helpers;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.object0r.monitor.tools.datatypes.MyHtmlUnitDriver;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.logging.Level;


public class SeleniumHelper
{
    static public WebDriver getNewHtmlUnitDriver()
    {
//silence

        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

        HtmlUnitDriver htmlUnitDriver = new MyHtmlUnitDriver(BrowserVersion.FIREFOX_38);
        htmlUnitDriver.setJavascriptEnabled(true);

/*        if (getProxyInfo().getType().equals(ProxyInfo.PROXY_TYPES_SOCKS4) || getProxyInfo().getType().equals(ProxyInfo.PROXY_TYPES_SOCKS5))
        {
            htmlUnitDriver.setSocksProxy(getProxyInfo().getHost(), Integer.parseInt(getProxyInfo().getPort()));
        }
        else
        {
            htmlUnitDriver.setHTTPProxy(getProxyInfo().getHost(), Integer.parseInt(getProxyInfo().getPort()) , new ArrayList<String>());
        }*/

        return htmlUnitDriver;
    }
}
