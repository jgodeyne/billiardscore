/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author jean
 */
public class Player implements Serializable{
    private static final long serialVersionUID = 1L;
    private String licentie;
    private String name;
    private String club;
    private Integer tsp;
    private String discipline;

    public Player(String name, Integer tsp) {
        this(name, "", tsp, "", UUID.randomUUID().toString());
    }

    public Player(String name, String club, Integer tsp, String discipline, String licentie) {
        this.name = name;
        this.club = club;
        this.tsp = tsp;
        this.discipline = discipline;
        this.licentie = licentie;
    }

    public String getName() {
        return name;
    }

    public String getClub() {
        return club;
    }

    public Integer getTsp() {
        return tsp;
    }

    public String getDiscipline() {
        return discipline;
    }

    public String getLicentie() {
        return licentie;
    }

    public void setLicentie(String licentie) {
        this.licentie = licentie;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public void setTsp(Integer tsp) {
        this.tsp = tsp;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    @Override
    public String toString() {
        return name;
    }
}
