/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data.conversion.nto;

import billiard.data.TeamItem;

/**
 *
 * @author jean
 */
public class Team extends TeamItem {
    private String club = "";
    private String competition = "";

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }
}
