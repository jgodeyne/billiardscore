/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author jean
 */
public final class AppProperties extends Properties{
    private static File file;
    private static AppProperties prop;
    
    private static final String TITLE = "title";
    private static final String SUBTITLE = "subtitle";
    private static final String CLUB = "club";
    private static final String SCOREBOARD_ID = "scoreboard_id";
    private static final String LOCALE = "locale";
    private static final String EMAIL_SERVER = "emailserver";
    private static final String EMAIL_UID = "emailuid";
    private static final String EMAIL_PWD = "emailpwd";
    private static final String EMAIL_SENDER = "emailsender";
    private static final String DEF_LEAGUE = "default_league";
    private static final String BANNER_LOC = "banner_location";
    private static final String ARCHIVE_LOCATION = "archive_location";
    private static final String DATA_LOCATION = "data_location";
    private static final String USER_HOME = "user.home";
    
    private boolean emailConfigured = false;
   
    protected AppProperties() throws Exception {
        super();
        file = InitAppConfig.getPropertiesFile();
        try (InputStream input = new FileInputStream(file)) {
            super.load(input);
        }
        if(getEmailSender() != null && !getEmailSender().isEmpty() 
                && getEmailServer()!=null && !getEmailServer().isEmpty()) {
            emailConfigured = true;
        }
    }

    public static AppProperties getInstance() throws Exception {
        if (prop==null) {
            prop = new AppProperties();
        }
        return prop;
    }
    
    public String getTitle() {
        return this.getProperty(TITLE);
    }
    
    public void setTitle(String title) {
        this.setProperty(TITLE, title);
    }
    
    public String getSubTitle() {
        return this.getProperty(SUBTITLE);
    }
    
    public void setSubTitle(String subtitle) {
        this.setProperty(SUBTITLE, subtitle);
    }
    
    public String getClub() {
        return this.getProperty(CLUB);
    }
    
    public void setClub(String club) {
        this.setProperty(CLUB, club);
    }
    
    public String getScoreboardId() {
        return this.getProperty(SCOREBOARD_ID);
    }
    
    public void setScoreboardId(String scoreboardId) {
        this.setProperty(SCOREBOARD_ID, scoreboardId);
    }
    
    public String getLocale() {
        return getProperty(LOCALE);
    }
    
    public void setLocale(String locale) {
        this.setProperty(LOCALE, locale);
    }
    
    public String getEmailServer() {
        return getProperty(EMAIL_SERVER);
    }
    
    public void setEmailServer(String server) {
        this.setProperty(EMAIL_SERVER, server);
    }
    
    public String getEmailUID() {
        return getProperty(EMAIL_UID);
    }
    
    public void setEmailUID(String uid) {
        this.setProperty(EMAIL_UID, uid);
    }
    
    public String getEmailPWD() {
        return getProperty(EMAIL_PWD);
    }
    
    public void setEmailPWD(String pwd) {
        this.setProperty(EMAIL_PWD, pwd);
    }
    
    public String getEmailSender() {
        return getProperty(EMAIL_SENDER);
    }
    
    public void setEmailSender(String sender) {
        this.setProperty(EMAIL_SENDER, sender);
    }

    public boolean isEmailConfigured() {
        return emailConfigured;
    }
    
    public String getDefaultLeague() {
        return getProperty(DEF_LEAGUE);
    }
    
    public void setDefaultLeague(String league) {
        this.setProperty(DEF_LEAGUE, league);
    }
    
    public String getBannerLocation() {
        return System.getProperty(USER_HOME)+"/."+PermittedValues.APP_NAME+"/"+this.getProperty(BANNER_LOC);
    }
    
    public String getArchiveLocation() {
        return System.getProperty(USER_HOME)+"/."+PermittedValues.APP_NAME+"/"+this.getProperty(ARCHIVE_LOCATION);
    }
    
    public String getDataPath() {
        return System.getProperty(USER_HOME)+"/."+PermittedValues.APP_NAME+"/"+this.getProperty(DATA_LOCATION);
    }

    public String getLogoLocation() {
        return System.getProperty(USER_HOME) + "/." + PermittedValues.APP_NAME + "/logo.png";

    }
    public void save() throws Exception {
        this.store(new FileOutputStream(file), "");
    }
}
