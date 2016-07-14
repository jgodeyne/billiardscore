/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

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
    private String league = "";

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

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }
}
