/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author jean
 */
public class Team implements Serializable{
    private static final long serialVersionUID = 1L;
    private final String name;
    private ArrayList<Player> players = new ArrayList<>();

    public Team(String name, ArrayList<Player> players) {
        this.name = name;
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public boolean isPlayerOfTeam(Player player) {
        for (Player teamPlayer : players) {
            if(player.getLicentie().equals(teamPlayer.getLicentie())) {
                return true;
            }
        }
        return false;
    }    
}
