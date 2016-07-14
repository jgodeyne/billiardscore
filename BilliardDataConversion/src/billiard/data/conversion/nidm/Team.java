/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data.conversion.nidm;

import billiard.data.TeamItem;

/**
 *
 * @author jean
 */
public class Team extends TeamItem {
    private String teamNbr = "";
    private String club = "";

    public String getTeamNbr() {
        return teamNbr;
    }

    public void setTeamNbr(String teamNbr) {
        this.teamNbr = teamNbr;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }
}
