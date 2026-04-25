package com.object0r.monitor.tools.helpers;

import com.object0r.toortools.os.OsHelper;

import java.io.File;
import java.util.Vector;

public class Helper
{
    public static void deleteFolderContentsRecursive(File folder) throws Exception
    {
        Vector<String> filenames = OsHelper.getDirectoryContents(folder.getAbsolutePath(), true);

        for (String filename : filenames)
        {
            if (!filename.contains(".gitkeep"))
            {
                System.out.println("Deleting " + filename);
                new File(filename).delete();
            }
        }
    }
}
