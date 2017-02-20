package com.object0r.monitor.tools.helpers;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

public class GitHelper
{
    public static String getRepositoryLatestCommitId(String repository) throws IOException, InterruptedException
    {
        String[] command = {"git", "ls-remote", repository, "HEAD"};
        Process p = createCommandInDirectory(command, "./");
        p.waitFor();
        StringWriter stream = new StringWriter();
        IOUtils.copy(p.getInputStream(), stream, "UTF-8");
        return stream.toString().replace("\tHEAD", "").trim();
    }

    public static boolean clone(GitRepositoryCloneInfo gitRepositoryCloneInfo)
    {
        try
        {
            String[] command = {"git", "clone", gitRepositoryCloneInfo.getRepositoryUrl()};
            Process p = createCommandInDirectory(command, gitRepositoryCloneInfo.getDirectory());
            //printProcessResult(p);

            command = new String[]{"git", "reset", "hard"};
            p = createCommandInDirectory(command, gitRepositoryCloneInfo.getDirectory());
            //printProcessResult(p);

            command = new String[]{"git", "checkout", gitRepositoryCloneInfo.getBranch()};
            p = createCommandInDirectory(command, gitRepositoryCloneInfo.getDirectory());
            printProcessResult(p);


            /*
            Process p = Runtime.getRuntime().exec("cd " + gitRepositoryCloneInfo.getDirectory() + "; " +
                    "git clone " + gitRepositoryCloneInfo.getDirectory() + ";" +
                    "cd " + gitRepositoryCloneInfo.getDirectory() + ";" +
                    "git reset --hard; " +
                    "git checkout " + gitRepositoryCloneInfo.getBranch() + "");
            p.waitFor();
            p.getInputStream();
            StringWriter stream = new StringWriter();
            IOUtils.copy(p.getErrorStream(), stream, "UTF-8");
            String errorString = stream.toString();
            stream = new StringWriter();
            IOUtils.copy(p.getInputStream(), stream, "UTF-8");
            String inputString = stream.toString();
            System.out.println("Input: " + inputString);
            System.out.println("Error: " + errorString);*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    private static Process createCommandInDirectory(String[] command, final String directory) throws IOException
    {
        if (!new File(directory).isDirectory())
        {
            new File(directory).mkdirs();
        }
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(new File(directory));

        return builder.start();
    }

    private static void printProcessResult(Process p)
    {
        try
        {
            p.getInputStream();
            StringWriter stream = new StringWriter();
            IOUtils.copy(p.getErrorStream(), stream, "UTF-8");
            String errorString = stream.toString();
            stream = new StringWriter();
            IOUtils.copy(p.getInputStream(), stream, "UTF-8");
            String inputString = stream.toString();
            System.out.println("Input: " + inputString);
            System.out.println("Error: " + errorString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static class GitRepositoryCloneInfo
    {
        String repositoryUrl;
        String branch = "master";
        String directory = null;
        String directoryPrefix = "tmp/repositories";

        public String getRepositoryUrl()
        {
            return repositoryUrl;
        }

        public void setRepositoryUrl(String repositoryUrl)
        {
            this.repositoryUrl = repositoryUrl;
        }

        public String getBranch()
        {
            return branch;
        }

        public void setBranch(String branch)
        {
            this.branch = branch;
        }

        public String getDirectory() throws Exception
        {
            if (directory == null)
            {
                if (repositoryUrl == null)
                {
                    throw new Exception("RepositoryUrl and directory are null.");
                }
                else
                {
                    return directoryPrefix + "/" + repositoryUrl.replaceAll("\\W+", "_");
                }
            }
            else
            {
                return directory;
            }
        }

        public void setDirectory(String directory)
        {
            this.directory = directory;
        }

        public String getDirectoryPrefix()
        {
            return directoryPrefix;
        }

        public void setDirectoryPrefix(String directoryPrefix)
        {
            this.directoryPrefix = directoryPrefix;
        }
    }
}
