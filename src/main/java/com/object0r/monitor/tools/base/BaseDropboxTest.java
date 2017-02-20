package com.object0r.monitor.tools.base;

import com.object0r.monitor.tools.helpers.DropboxHelper;

import java.util.concurrent.TimeUnit;

public abstract class BaseDropboxTest extends BaseTest
{
    public BaseDropboxTest(BaseReporter reporter, boolean forceRun)
    {
        super(reporter, forceRun);
    }

    public BaseDropboxTest(BaseReporter reporter)
    {
        super(reporter);
    }

    protected void checkIfDropboxFileHasChanged(String file, int timeUnitCount, TimeUnit timeUnit, String dropboxToken)
    {
        try
        {
            String page = DropboxHelper.downloadFileAsBase64String(dropboxToken, file);
            if (!checkIfValueHasChanged("_dropbox_checker_"+file, page, timeUnitCount, timeUnit, false))
            {
                errors.add("BookDropboxTests: " + file + " has not be changed for " + timeUnitCount + " " + timeUnit + " - https://www.dropbox.com/home/" + file + " ");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errors.add("Error Checking BookDropboxTests Jobs file " + file + ": " + e.toString());
        }
    }

    protected void checkIfDropboxFileMetadataHasChanged(String file, int hours, TimeUnit timeUnit, String dropboxToken)
    {
        try
        {
            String page = DropboxHelper.getFileMetadata(dropboxToken, file);
            if (!checkIfValueHasChanged("_dropbox_checker_"+file, page, hours, timeUnit, false))
            {
                errors.add("BookDropboxTests: " + file + " has not be changed for " + hours + " " + timeUnit + " - https://www.dropbox.com/home/" + file + " ");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errors.add("Error Checking BookDropboxTests Jobs file " + file + ": " + e.toString());
        }
    }

}
