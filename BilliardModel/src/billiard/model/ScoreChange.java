/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

/**
 *
 * @author jean
 */
public class ScoreChange {
    private final Player player;
    private final int inning;
    private final int points;

    public ScoreChange(Player player, int inning, int points) {
        this.player = player;
        this.inning = inning;
        this.points = points;
    }

    public Player getPlayer() {
        return player;
    }

    public int getInning() {
        return inning;
    }

    public int getPoints() {
        return points;
    }    
}
