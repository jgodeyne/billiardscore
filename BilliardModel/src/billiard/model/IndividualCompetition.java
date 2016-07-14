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
public class IndividualCompetition extends Competition implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Player player1;
    private final Player player2;
    private String tableNumber;
    private PlayerMatchResult player1Result;
    private PlayerMatchResult player2Result;
    private final long matchId;

    public IndividualCompetition(String name, String tableFormat, String discipline, String matchNumber, Player player1, Player player2) {
        super(name, discipline, tableFormat);
        this.player1 = player1;
        this.player2 = player2;
        Match match = new Match(super.getId(), matchNumber, discipline, player1, player2);
        this.matchId = match.getId();
        MatchManager.getInstance().putMatch(match);
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public PlayerMatchResult getPlayer1Result() {
        return player1Result;
    }

    public void setPlayer1Result(PlayerMatchResult player1Result) {
        this.player1Result = player1Result;
    }

    public PlayerMatchResult getPlayer2Result() {
        return player2Result;
    }

    public void setPlayer2Result(PlayerMatchResult player2Result) {
        this.player2Result = player2Result;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Match getMatch() {
        return MatchManager.getInstance().getMatch(matchId);
    }
}
