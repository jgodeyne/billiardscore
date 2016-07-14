/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author jean
 */
public class TeamCompetitionItem {
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty contactName = new SimpleStringProperty("");
    private final StringProperty contactEmail = new SimpleStringProperty("");
    private final StringProperty discipline = new SimpleStringProperty("");
    private final StringProperty pointsSystem = new SimpleStringProperty("");
    private final StringProperty tableFormat = new SimpleStringProperty("");
    private final StringProperty group = new SimpleStringProperty("");
    private final StringProperty nbrOfPlayers = new SimpleStringProperty("");
    private final StringProperty tspPlayers = new SimpleStringProperty("");
    private final StringProperty disciplinePlayers = new SimpleStringProperty("");
    private final StringProperty league = new SimpleStringProperty("");
    private HashMap<String, TeamItem> teams = new HashMap<>();

    public HashMap<String, TeamItem> getTeams() {
        return teams;
    }

    public void setTeams(HashMap<String, TeamItem> teams) {
        this.teams = teams;
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }    

    public Property<String> getNameProp() {
        return this.name;
    }
    
    public String getPointsSystem() {
        return pointsSystem.getValue();
    }

    public void setPointsSystem(String pointsSystem) {
        this.pointsSystem.setValue(pointsSystem);
    }
    
    public StringProperty getPointSystemProp() {
        return pointsSystem;
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

    public String getTableFormat() {
        return tableFormat.getValue();
    }

    public void setTableFormat(String tableFormat) {
        this.tableFormat.setValue(tableFormat);
    }
    
    public StringProperty getTableFormatProp() {
        return tableFormat;
    }

    public String getGroup() {
        return group.getValue();
    }

    public void setGroup(String group) {
        this.group.setValue(group);
    }
    
    public StringProperty getGroupProp() {
        return group;
    }

    public String getNbrOfPlayers() {
        return nbrOfPlayers.getValue();
    }

    public void setNbrOfPlayers(String nbrOfPlayers) {
        this.nbrOfPlayers.setValue(nbrOfPlayers);
    }
    
    public StringProperty getNbrOfPlayersProp() {
        return nbrOfPlayers;
    }

    public String getTspPlayers() {
        return tspPlayers.getValue();
    }

    public void setTspPlayers(String tsps) {
        this.tspPlayers.setValue(tsps);
    }
    
    public StringProperty getTspPlayersProp() {
        return tspPlayers;
    }

    public String getDisciplinePlayers() {
        return disciplinePlayers.getValue();
    }

    public void setDisciplinePlayers(String disciplinePlayers) {
        this.disciplinePlayers.setValue(disciplinePlayers);
    }
    
    public StringProperty getDisciplinePlayersProp() {
        return disciplinePlayers;
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

    public StringProperty getContactNameProp() {
        return contactName;
    }

    public String getContactName() {
        return contactName.getValue();
    }

    public void setContactName(String contactName) {
        this.contactName.setValue(contactName);
    }

    public StringProperty getContactEmailProp() {
        return contactEmail;
    }

    public String getContactEmail() {
        return contactEmail.getValue();
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail.setValue(contactEmail);
    }

    public ArrayList getTeamNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Map.Entry<String, TeamItem> entry : teams.entrySet()) {
            String key = entry.getKey();
            TeamItem value = entry.getValue();
            names.add(value.getName());
        }
        return names;
    }

    public TeamItem getTeam(String name) {
        return teams.get(name);
    }
    
    public void putTeam(TeamItem team) {
        teams.put(team.getName(), team);
    }
    
    public void removeTeam(TeamItem team)  {
        teams.remove(team.getName());
    }

    public TeamCompetitionItem copy() throws CloneNotSupportedException {
        TeamCompetitionItem teamCompetitionClone = new TeamCompetitionItem();
        teamCompetitionClone.setDiscipline(this.getDiscipline());
        teamCompetitionClone.setDisciplinePlayers(this.getDisciplinePlayers());
        teamCompetitionClone.setGroup(this.getGroup());
        teamCompetitionClone.setLeague(this.getLeague());
        teamCompetitionClone.setName(this.getName());
        teamCompetitionClone.setNbrOfPlayers(this.getNbrOfPlayers());
        teamCompetitionClone.setPointsSystem(this.getPointsSystem());
        teamCompetitionClone.setTableFormat(this.getTableFormat());
        teamCompetitionClone.setTspPlayers(this.getTspPlayers());
 
        HashMap<String, TeamItem> teamCloneList = new HashMap<>();
        for (Map.Entry<String, TeamItem> entry : teams.entrySet()) {
            String key = entry.getKey();
            TeamItem value = entry.getValue().copy();
            teamCloneList.put(key, value);
        }
        teamCompetitionClone.setTeams(teamCloneList);
        
        return teamCompetitionClone;
    }
}
