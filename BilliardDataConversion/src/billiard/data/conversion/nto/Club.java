/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data.conversion.nto;

import java.util.ArrayList;

/**
 *
 * @author jean
 */
public class Club {
    private String lic = "";
    private String name = "";
    private ArrayList<Player> players = new ArrayList<>();

    public String getLic() {
        return lic;
    }

    public String getName() {
        return name;
    }

    public void setLic(String lic) {
        this.lic = lic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> reservePlayers) {
        this.players = reservePlayers;
    }
    
    public void addPlayer(Player player) {
        players.add(player);
    }
}
