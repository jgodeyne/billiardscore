/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data.conversion.nto;

import billiard.data.PlayerItem;

/**
 *
 * @author jean
 */
public class Player extends PlayerItem {
    private String teamName = "";
    private String avg = "";

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamNbr) {
        this.teamName = teamNbr;
    }

    public String getAvg() {
        return avg;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }
}
