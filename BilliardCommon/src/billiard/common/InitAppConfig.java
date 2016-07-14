/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.common;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;

/**
 *
 * @author jean
 */
public class InitAppConfig {
    private static final Logger LOGGER = Logger.getLogger(InitAppConfig.class.getName());
    private static final String USER_HOME = "user.home";
    private static final String PROPERTIES_FILE = "app.properties";
    private static final String SCORE_CSS = "default.css";
    private static final String HAZELCAST_CONFIG_FILE = "hazelcast.xml";
    private static final String LOG_PROPERTIES_FILE = "logging.properties";
    private static final String EXAMPLE_CONFIG_PATH = "/config_example/";
    private static final String DEFAULT_BANNER_NAME = "banner.jpg";
    private static final String DEFAULT_BANNER_RESOURCE = "/billiard/images/" + DEFAULT_BANNER_NAME;
    
    private static String userHome;
 
    public static void initAppConfig(Application app) throws Exception {
        LOGGER.log(Level.FINEST, "initAppConfig => Start");
        userHome = System.getProperty(USER_HOME);
        LOGGER.log(Level.FINEST, "initAppConfig => userHome: {0}", userHome);
        // Properties File
        File propfile = getPropertiesFile();
        LOGGER.log(Level.FINEST, "initAppConfig => propFile: {0}", propfile.toPath());
        if(!propfile.exists()) {
            propfile.mkdirs();
            InputStream input = app.getClass().getResourceAsStream(EXAMPLE_CONFIG_PATH + PROPERTIES_FILE);
            Files.copy(input, propfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        
        // CSS File
        File cssfile = getCSSFile();
        LOGGER.log(Level.FINEST, "initAppConfig => cssFile: {0}", cssfile.toPath());
        if(!cssfile.exists()) {
            cssfile.mkdirs();
            InputStream input = app.getClass().getResourceAsStream(EXAMPLE_CONFIG_PATH + SCORE_CSS);
            Files.copy(input, cssfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Hazelcast.xml
        File hazelcastfile = getHazelcastConfigFile();
        LOGGER.log(Level.FINEST, "initAppConfig => hazelcastfile: {0}", hazelcastfile.toPath());
        if(!hazelcastfile.exists()) {
            hazelcastfile.mkdirs();
            InputStream input = app.getClass().getResourceAsStream(EXAMPLE_CONFIG_PATH + HAZELCAST_CONFIG_FILE);
            Files.copy(input, hazelcastfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        
        // logging.properties
        File logpropfile = getLogPropFile();
        LOGGER.log(Level.FINEST, "initAppConfig => logpropfile: {0}", logpropfile.toPath());
        if(!logpropfile.exists()) {
            logpropfile.mkdirs();
            InputStream input = app.getClass().getResourceAsStream(EXAMPLE_CONFIG_PATH + LOG_PROPERTIES_FILE);
            Files.copy(input, logpropfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        // default banner
        File defaultBanner = getDefaultBannerFile();
        LOGGER.log(Level.FINEST, "initAppConfig => defaultbanner: {0}", defaultBanner.toPath());
        if(!defaultBanner.exists()) {
            InputStream input = app.getClass().getResourceAsStream(DEFAULT_BANNER_RESOURCE);
            Path output = Paths.get(AppProperties.getInstance().getBannerLocation(),DEFAULT_BANNER_NAME);
            output.toFile().mkdirs();
            Files.copy(input, output, StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
    public static File getPropertiesFile() {
        return new File(userHome +"/."+ PermittedValues.APP_NAME + "/" + PROPERTIES_FILE);
    }
    
    public static File getHazelcastConfigFile() {
        return new File(userHome +"/."+ PermittedValues.APP_NAME + "/" + HAZELCAST_CONFIG_FILE);
    }

    public static File getCSSFile() {
        return new File(userHome +"/."+ PermittedValues.APP_NAME + "/" + SCORE_CSS);
    }

    public static File getLogPropFile() {
        return new File(userHome +"/."+ PermittedValues.APP_NAME + "/" + LOG_PROPERTIES_FILE);
    }

    public static File getDefaultBannerFile() {
        return new File(userHome +"/."+ PermittedValues.APP_NAME + "/banner/" + DEFAULT_BANNER_RESOURCE);
    } 
}
