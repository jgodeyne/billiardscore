/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.common;

/**
 *
 * @author jean
 */
public class PermittedValues {
    public static final String[] DISCIPLINES = {"Vrijspel (KB)", "Band (KB)", "Drieband (KB)", "K38/2", "K57/2", "Neo K57/2", "K57/1", "Vrijspel (GB)", "Band (GB)", "Drieband (GB)", "K47/2", "K47/1", "K71/2"};
    public static final String[] TABLE_FORMAT = {"2,10m","2,30m","2,85m"};
    public static final String[] COLOR_STARTING_BALL = {"WHITE", "YELLOW"};
    public static final String APP_NAME = "billiardsoftware";

    public enum TurnIndicatorsColor {
        WHITE_YELLOW, YELLOW_WHITE;
    }
    
    public enum Mode {
        READ_ONLY, EDIT, NEW, SELECT;
    }
    
    public enum Action {
        CLOSE, EDIT, NEW, DELETE, SAVE, OK, CANCEL, SELECT, EXPORT, IMPORT;
    }
    
    public enum ActionObject {
        CLUB, MEMBER, LEAGUE, TEAM_COMP, IND_COMP;
    }
}
