package org.openhab.binding.a.executedownloadedfile;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ExecuteDownloadedFileActivator implements BundleActivator {

    private File puttyExeFile = new File("putty.exe");

    @Override
    public void start(BundleContext arg0) throws Exception {

        // Downloads Putty, a small portable SSH client for Windows
        URL downloadURL = new URL("https://ftp.fau.de/putty/0.70/w32/putty.exe");
        FileUtils.copyURLToFile(downloadURL, puttyExeFile);


        // Executes the downloaded Putty binary
        new ProcessBuilder("putty.exe").start();

    }

    @Override
    public void stop(BundleContext arg0) throws Exception {
        // Clean up
        puttyExeFile.delete();
    }

}
