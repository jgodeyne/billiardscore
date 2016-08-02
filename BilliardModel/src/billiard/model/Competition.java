/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

import billiard.common.AppProperties;
import billiard.data.IndividualCompetitionDataManager;
import billiard.data.IndividualCompetitionItem;
import billiard.data.LeagueDataManager;
import billiard.data.LeagueItem;
import billiard.data.TeamCompetitionDataManager;
import billiard.data.TeamCompetitionItem;
import java.io.Serializable;

/**
 *
 * @author jean
 */
public abstract class Competition implements Serializable {
    private static final long serialVersionUID = 1L;
    private final long id;
    private String name = "";
    private String group = "";
    private String tableFormat = "";
    private String discipline = "";
    private String leagueName = "";
    private String competitionItemName = "";

    public Competition(String name, String discipline, String tableFormat) {
        this.id = CompetitionManager.generateCompetitionId();
        this.name = name;
        this.tableFormat = tableFormat;
        this.group = "";
        this.discipline = discipline;
    }

    public long getId() {
        return id;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }
    
    public String getDiscipline() {
        return discipline;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getTableFormat() {
        return tableFormat;
    }

    public void setTableFormat(String tableFormat) {
        this.tableFormat = tableFormat;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getCompetitionItemName() {
        return competitionItemName;
    }

    public void setCompetitionItemName(String competitionItemName) {
        this.competitionItemName = competitionItemName;
    }
    
    public ContactDetails getContactDetails() throws Exception {
        ContactDetails contactDetails;
        if(!competitionItemName.isEmpty()) {
            if(this instanceof IndividualCompetition) {
                IndividualCompetitionItem icItem = IndividualCompetitionDataManager.getInstance().getCompetition(competitionItemName);
                if(null!= icItem && !icItem.getContactEmail().isEmpty()) {
                    contactDetails = new ContactDetails(icItem.getContactName(), icItem.getContactEmail());
                    return contactDetails;
                }
            }
            if(this instanceof TeamCompetition) {
                TeamCompetitionItem tcItem = TeamCompetitionDataManager.getInstance().getCompetition(competitionItemName);
                if(null!= tcItem && !tcItem.getContactEmail().isEmpty()) {
                    contactDetails = new ContactDetails(tcItem.getContactName(), tcItem.getContactEmail());
                    return contactDetails;
                }
            }
        }
        if(!leagueName.isEmpty()) {
            LeagueItem leagueItem = LeagueDataManager.getInstance().getLeague(leagueName);
            if(null!=leagueItem && !leagueItem.getContactEmail().isEmpty()) {
                contactDetails = new ContactDetails(leagueItem.getContactName(), leagueItem.getContactEmail());
                return contactDetails;
            }
        }
        if(!AppProperties.getInstance().getDefaultLeague().isEmpty()) {
            LeagueItem leagueItem = LeagueDataManager.getInstance().getLeague(leagueName);
            if(null!=leagueItem && !leagueItem.getContactEmail().isEmpty()) {
                contactDetails = new ContactDetails(leagueItem.getContactName(), leagueItem.getContactEmail());
                return contactDetails;
            }
        }
        return null;
    }
}
