/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.common.hazelcast;

import billiard.common.InitAppConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jean
 */
public class SyncManager {
    private static final Logger LOGGER = Logger.getLogger(SyncManager.class.getName());
    private static HazelcastInstance hazelcastInstance;
    private static boolean hazelcastEnabled = false;

    
    public static void start() throws UnknownHostException {
        LOGGER.log(Level.FINEST, "start => Start");
        if (isNetworkAvailable()) {
            LOGGER.log(Level.FINEST, "start => NetworkAvailable");
            if (hazelcastInstance==null) {
                Config config = new Config();
                config.setConfigurationFile(InitAppConfig.getHazelcastConfigFile());
                hazelcastInstance = Hazelcast.newHazelcastInstance(config);
                hazelcastEnabled = true;
            }
        }        
        LOGGER.log(Level.FINEST, "start => End");
    }
    
    public static void stop() {
        LOGGER.log(Level.FINEST, "stop => Start");
        if(hazelcastEnabled) {
            hazelcastInstance.shutdown();
            hazelcastEnabled=false;
        }
        LOGGER.log(Level.FINEST, "stop => End");
    }
 
    public static HazelcastInstance getHazelCastInstance() {
        return hazelcastInstance;
    }

    private static boolean isNetworkAvailable() throws UnknownHostException {
        if(InetAddress.getLocalHost() instanceof Inet4Address) {
            return true;
        }
        LOGGER.log(Level.WARNING, "isNetworkAvailable => No Network");
        return false;
    }

    public static boolean isHazelcastEnabled() {
        return hazelcastEnabled;
    }
}
