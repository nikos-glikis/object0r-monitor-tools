package com.object0r.monitor.tools.helpers;

import com.object0r.toortools.Utilities;
import com.object0r.toortools.http.HttpHelper;
import com.object0r.toortools.http.HttpRequestInformation;
import com.object0r.toortools.http.HttpResult;
import org.openqa.selenium.internal.Base64Encoder;

public class JenkinsHelper
{
    public static int getLatestJobId(String jenkinsUrl, String jobName) throws Exception
    {
        return getLatestJobId(jenkinsUrl, jobName, null, null);
    }

    public static String getJobResult(String jenkinsUrl, String jobName, int jobId, String user, String token) throws Exception
    {
        try
        {
            HttpRequestInformation httpRequestInformation = new HttpRequestInformation();
            httpRequestInformation.setUrl("http://" + jenkinsUrl + "/job/" + jobName + "/" + jobId + "/api/json");
            httpRequestInformation.setMethodPost();
            httpRequestInformation.setHeader("Content-Type", "application/json");
            if (!(token == null) && !token.equals("") && !(user == null) && !user.equals(""))
            {
                httpRequestInformation.setHeader("Authorization", "Basic " + new String(new Base64Encoder().encode((user + ":" + token).getBytes()).getBytes(), "UTF-8"));
                ;
            }
            httpRequestInformation.setThrowExceptions(true);
            HttpResult httpResult = HttpHelper.request(httpRequestInformation);
            return Utilities.cut(",\"result\":", ",", httpResult.getContentAsString());
        }
        catch (Exception e)
        {
            return "Error: " + e.toString();
        }
    }

    public static String getJobDuration(String jenkinsUrl, String jobName, int jobId, String user, String token) throws Exception
    {
        try
        {
            HttpRequestInformation httpRequestInformation = new HttpRequestInformation();
            httpRequestInformation.setUrl("http://" + jenkinsUrl + "/job/" + jobName + "/" + jobId + "/api/json");
            httpRequestInformation.setMethodPost();
            httpRequestInformation.setHeader("Content-Type", "application/json");
            if (!(token == null) && !token.equals("") && !(user == null) && !user.equals(""))
            {
                httpRequestInformation.setHeader("Authorization", "Basic " + new String(new Base64Encoder().encode((user + ":" + token).getBytes()).getBytes(), "UTF-8"));
            }
            httpRequestInformation.setThrowExceptions(true);
            HttpResult httpResult = HttpHelper.request(httpRequestInformation);
            return Utilities.cut("\"duration\":", ",", httpResult.getContentAsString()).trim();
        }
        catch (Exception e)
        {
            return "Exception: " + e.toString();
        }
    }

    public static int getLatestJobId(String jenkinsUrl, String jobName, String user, String token) throws Exception
    {
        HttpRequestInformation httpRequestInformation = new HttpRequestInformation();
        httpRequestInformation.setUrl("http://" + jenkinsUrl + "/job/" + jobName + "/lastBuild/api/json");
        httpRequestInformation.setMethodPost();
        httpRequestInformation.setHeader("Content-Type", "application/json");
        if (!(token == null) && !token.equals("") && !(user == null) && !user.equals(""))
        {
            //httpRequestInformation.setBody("token=" + token);
            httpRequestInformation.setHeader("Authorization", "Basic " + new String(new Base64Encoder().encode((user + ":" + token).getBytes()).getBytes(), "UTF-8"));
            ;
        }
        httpRequestInformation.setThrowExceptions(true);
        HttpResult httpResult = HttpHelper.request(httpRequestInformation);
        return Integer.parseInt(Utilities.cut("\",\"id\":\"", "\"", httpResult.getContentAsString()));
    }
}
