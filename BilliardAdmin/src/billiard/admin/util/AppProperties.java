/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.admin.util;

import billiard.common.InitAppConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author jean
 */
public class AppProperties extends Properties{
    private static File file;
    private static AppProperties prop;
    
    private static final String LOCALE = "locale";
    private static final String DEF_LEAGUE = "default_league";
    
    protected AppProperties(String appName) throws Exception {
        super();
        file = InitAppConfig.getPropertiesFile();
        InputStream input = new FileInputStream(file);
        this.load(input);
        input.close();
    }

    public static AppProperties getInstance(String appName) throws Exception {
        if (prop==null) {
            prop = new AppProperties(appName);
        }
        return prop;
    }
    
    public String getLocale() {
        return getProperty(LOCALE);
    }
    
    public void setLocale(String locale) {
        this.setProperty(LOCALE, locale);
    }
    
    public String getDefaultLeague() {
        return getProperty(DEF_LEAGUE);
    }
    
    public void setDefaultLeague(String league) {
        this.setProperty(DEF_LEAGUE, league);
    }
    
    public void save() throws Exception {
        this.store(new FileOutputStream(file), "");
    }
}
