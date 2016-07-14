/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author jean
 */
public class IndividualCompetitionItem {
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty contactName = new SimpleStringProperty("");
    private final StringProperty contactEmail = new SimpleStringProperty("");
    private final StringProperty discipline = new SimpleStringProperty("");
    private final StringProperty tableFormat = new SimpleStringProperty("");
    private final StringProperty league = new SimpleStringProperty("");
    HashMap<String, PlayerItem> players = new HashMap<>();

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }
    
    public StringProperty getNameProp() {
        return name;
    }

    public String getContactName() {
        return contactName.getValue();
    }

    public void setContactName(String contactName) {
        this.contactName.setValue(contactName);
    }
    
    public StringProperty getContactNameProp() {
        return contactName;
    }

    public String getContactEmail() {
        return contactEmail.getValue();
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail.setValue(contactEmail);
    }
    
    public StringProperty getContactEmailProp() {
        return contactEmail;
    }
    
    public String getDiscipline() {
        return discipline.getValue();
    }

    public void setDiscipline(String discipline) {
        this.discipline.setValue(discipline);
    }
    
    public StringProperty getDisciplineProp() {
        return discipline;
    }

    public StringProperty getTableFormatProp() {
        return tableFormat;
    }

    public String getTableFormat() {
        return this.tableFormat.getValue();
    }
    public void setTableFormat(String tableFormat) {
        this.tableFormat.setValue(tableFormat);
    }

    public String getLeague() {
        return league.getValue();
    }
    
    public void setLeague(String league) {
        this.league.setValue(league);
    }
    
    public StringProperty getLeagueProp() {
        return league;
    }

    public HashMap<String, PlayerItem> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<String, PlayerItem> players) {
        this.players = players;
    }
    
    public PlayerItem getPlayer(String lic) {
        return this.players.get(lic);
    }
    
    public void putPlayer(PlayerItem player) {
        players.put(player.getLic(), player);
    }
    
    public void removePlayer(PlayerItem player) {
        players.remove(player.getLic());
    }

    public IndividualCompetitionItem copy() {
        IndividualCompetitionItem competitionClone = new IndividualCompetitionItem();
        competitionClone.setContactEmail(this.getContactEmail());
        competitionClone.setContactName(this.getContactName());
        competitionClone.setLeague(this.getLeague());
        competitionClone.setName(this.getName());
        
        HashMap<String, PlayerItem> playerCloneList = new HashMap<>();
        for (Map.Entry<String, PlayerItem> entry : players.entrySet()) {
            String key = entry.getKey();
            PlayerItem value = entry.getValue().copy();
            playerCloneList.put(key, value);
        }
        competitionClone.setPlayers(playerCloneList);
        return competitionClone;
    }
}
