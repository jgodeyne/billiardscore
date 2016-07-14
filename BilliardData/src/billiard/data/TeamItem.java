/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author jean
 */
public class TeamItem {
    private HashMap<String, PlayerItem> players = new HashMap<>();
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty fixedPlayers = new SimpleStringProperty("");

    public HashMap<String, PlayerItem> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<String, PlayerItem> players) {
        this.players = players;
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }
    
    public StringProperty getNameProp() {
        return name;
    }

    public String getFixedPlayers() {
        return fixedPlayers.getValue();
    }

    public void setFixedPlayers(String fixedPlayers) {
        this.fixedPlayers.setValue(fixedPlayers);
    }
    
    public StringProperty getFixedPlayersProp() {
        return fixedPlayers;
    }

    public ArrayList getPlayerNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Map.Entry<String, PlayerItem> entry : players.entrySet()) {
            String key = entry.getKey();
            PlayerItem value = entry.getValue();
            names.add(value.getName());
        }
        return names;
    }
    
    public PlayerItem getPlayer(String lic) {
        return players.get(lic);
    }
    
    public PlayerItem getPlayerByName(String name) {
        for (Map.Entry<String, PlayerItem> entry : players.entrySet()) {
            String key = entry.getKey();
            PlayerItem value = entry.getValue();
            if(value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
    
    public PlayerItem getPlayerByOrder(String order) {
        for (Map.Entry<String, PlayerItem> entry : players.entrySet()) {
            String key = entry.getKey();
            PlayerItem value = entry.getValue();
            if(value.getOrder().equals(order)) {
                return value;
            }
        }
        return null;        
    }
    
    public void putPlayer(PlayerItem player) {
        players.put(player.getLic(), player);
    }
    
    public void removePlayer(PlayerItem player) {
        players.remove(player.getLic());
    }
    
    public TeamItem copy() throws CloneNotSupportedException {
        TeamItem teamClone = new TeamItem();
        teamClone.setName(this.getName());
        teamClone.setFixedPlayers(this.getFixedPlayers());
 
        HashMap<String, PlayerItem> playerCloneList = new HashMap<>();
        for (Map.Entry<String, PlayerItem> entry : players.entrySet()) {
            String key = entry.getKey();
            PlayerItem value = entry.getValue().copy();
            playerCloneList.put(key, value);
        }
        teamClone.setPlayers(playerCloneList);
        
        return teamClone;
    }

}
