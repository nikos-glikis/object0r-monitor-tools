package com.object0r.monitor.tools.helpers;

import com.google.gson.Gson;
import com.object0r.toortools.http.HttpHelper;
import com.object0r.toortools.http.HttpRequestInformation;
import com.object0r.toortools.http.HttpResult;

import java.util.HashMap;


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

    public static String getDirContentsJson(String token, String dir) throws Exception
    {

        HttpRequestInformation httpRequestInformation = new HttpRequestInformation();
        httpRequestInformation.setMethodPost();
        httpRequestInformation.setUrl("https://api.dropboxapi.com/2/files/list_folder");
        httpRequestInformation.setHeader("Authorization", "Bearer " + token);
        httpRequestInformation.setHeader("Content-Type", "application/json");
        httpRequestInformation.setBody("{\n" +
                "    \"path\": \"" + dir + "\",\n" +
                "    \"recursive\": true,\n" +
                "    \"include_media_info\": false,\n" +
                "    \"include_deleted\": false,\n" +
                "    \"include_has_explicit_shared_members\": false\n" +
                "}");
        HttpResult httpResult = HttpHelper.request(httpRequestInformation);
        if (httpResult.isSuccessfull())
        {
            Gson gson = new Gson();
            HashMap values = gson.fromJson(httpResult.getContentAsString(), HashMap.class);
            return values.get("entries").toString();
        }
        else
        {
            System.out.println(httpResult.getContentAsString() + dir);
            throw new Exception(httpResult.getThrownException());
        }
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
