/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data.conversion.nidm;

import billiard.data.PlayerItem;

/**
 *
 * @author jean
 */
public class Player extends PlayerItem {
    private String teamNbr = "";

    public String getTeamNbr() {
        return teamNbr;
    }

    public void setTeamNbr(String teamNbr) {
        this.teamNbr = teamNbr;
    }

}
