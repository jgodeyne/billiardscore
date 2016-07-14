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
public class PlayerTournamentResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private int points = 0;
    private int innings = 0;
    private int highestRun = 0;
    private int matchPoints = 0;
    private final long tournamentId;
    private final String playerLic;

    public PlayerTournamentResult(long tournamentId, String playerLic) {
        this.tournamentId=tournamentId;
        this.playerLic=playerLic;
    }

    public long getTournamentId() {
        return tournamentId;
    }

    public String getPlayerLic() {
        return playerLic;
    }

    public int getPoints() {
        return points;
    }

    public int getInnings() {
        return innings;
    }

    public float getAverage() {
        return points / innings;
    }

    public int getHighestRun() {
        return highestRun;
    }

    public int getMatchPoints() {
        return matchPoints;
    }

    public void addResult(PlayerMatchResult result) {
        this.points += result.getPoints();
        this.innings += result.getInnings();
        this.matchPoints += result.getMatchPoints();
        if (this.highestRun < result.getHighestRun()) {
            this.highestRun = result.getHighestRun();
        }
    }

}
