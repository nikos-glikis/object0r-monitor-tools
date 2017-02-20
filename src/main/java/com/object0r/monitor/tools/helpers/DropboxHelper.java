package com.object0r.monitor.tools.helpers;

import com.object0r.toortools.http.HttpHelper;
import com.object0r.toortools.http.HttpRequestInformation;
import com.object0r.toortools.http.HttpResult;


public class DropboxHelper
{
    public static byte[] downloadFile(String token, String file) throws Exception
    {

        HttpRequestInformation httpRequestInformation = new HttpRequestInformation();
        httpRequestInformation.setMethodPost();
        httpRequestInformation.setUrl("https://content.dropboxapi.com/2/files/download");
        httpRequestInformation.setHeader("User-Agent", "api-explorer-client");
        httpRequestInformation.setHeader("Authorization", "Bearer " + token);
        httpRequestInformation.setHeader("Content-Type", "");
        httpRequestInformation.setHeader("Dropbox-API-Arg", "{\"path\":\"" + file + "\"}");
        httpRequestInformation.setBody("");
        HttpResult httpResult = HttpHelper.request(httpRequestInformation);
        if (httpResult.isSuccessfull())
        {
            return httpResult.getContent();
        }
        else
        {
            System.out.println(httpResult.getContentAsString() + file);
            throw new Exception(httpResult.getThrownException());
        }
    }

    public static String downloadFileAsString(String token, String file) throws Exception
    {
        return new String(downloadFile(token, file), "UTF-8");
    }

    public static String downloadFileAsBase64String(String token, String file) throws Exception
    {
        return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(downloadFile(token, file));
    }

    public static String getFileMetadata(String token, String file) throws Exception
    {
        try
        {
            HttpRequestInformation httpRequestInformation = new HttpRequestInformation();
            httpRequestInformation.setMethodGet();
            httpRequestInformation.setUrl("https://api.dropboxapi.com/1/metadata/auto/" + file);
            httpRequestInformation.setHeader("User-Agent", "api-explorer-client");
            httpRequestInformation.setHeader("Authorization", "Bearer " + token);
            httpRequestInformation.setHeader("Content-Type", "");

            HttpResult httpResult;

            httpResult = HttpHelper.request(httpRequestInformation);

            if (httpResult.isSuccessfull())
            {
                return httpResult.getContentAsString();
            }
            else
            {
                System.out.println(httpResult.getContentAsString() + file);
                throw new Exception(httpResult.getThrownException());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }
}
