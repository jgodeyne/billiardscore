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
public class Match implements Serializable {
    private static final long serialVersionUID = 1L;
    private final long id;
    private long competitionId=0;
    private String number; 
    private Player player1;
    private Player player2;
    private PlayerMatchResult player1Result;
    private PlayerMatchResult player2Result;
    private int winner = 0;
    private final String discipline;
    private String status;
    private String scoreSheetHTML;

    public Match(String number, String discipline, Player player1, Player player2) {
        this(-1, number,discipline,player1,player2);
    }
    
    public Match(long competitionId, String number, String discipline, Player player1, Player player2) {
        this.id = MatchManager.generateMatchId();
        System.out.println("billiard.model.Match.<init>(): id: " + this.id);
        this.number = number;
        this.discipline = discipline;
        this.player1 = player1;
        this.player2 = player2;
        this.competitionId = competitionId;
        this.status = Status.CREATED;
    }

    public long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getDiscipline() {
        return discipline;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public PlayerMatchResult getPlayer1Result() {
        return player1Result;
    }

    public PlayerMatchResult getPlayer2Result() {
        return player2Result;
    }

    public int getWinner() {
        return winner;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }
    
    public void setResult(int winner, PlayerMatchResult player1Result, PlayerMatchResult player2Result) {
        this.winner = winner;
        this.player1Result = player1Result;
        this.player2Result = player2Result;
        this.status = Status.ENDED;
    }
    
    public void setIntermediatResult(PlayerMatchResult player1Result, PlayerMatchResult player2Result) {
        this.player1Result = player1Result;
        this.player2Result = player2Result;
    }
    
    public void correct() {
        this.status = Status.STARTED;
        this.player1Result = null;
        this.player2Result = null;
    }

    public String getScoreSheetHTML() {
        return scoreSheetHTML;
    }

    public void setScoreSheetHTML(String scoreSheetHTML) {
        this.scoreSheetHTML = scoreSheetHTML;
    }
    
    public boolean isStarted() {
        return (this.status.equals(Status.STARTED));
    }
    
    public boolean isSend() {
        return (this.status.equals(Status.RESERVED));
    }
    
    public boolean isSuspended() {
        return (this.status.equals(Status.SUSPENDED));
    }
    
    public boolean isEnded() {
        return (this.status.equals(Status.ENDED));
    }
    
    public boolean isCreated() {
        return (this.status.equals(Status.CREATED));
    }
    
    public boolean isCompetitionMatch() {
        return (competitionId != -1);
    }
    
    public void switchPlayers() {
        Player player = this.player1;
        this.player1 = this.player2;
        this.player2 = player;
    }
    
    public void start() {
        status = Status.STARTED;
    }
    
    public void cancel() {
        status = Status.CREATED;
    }
    
    public void reserve() {
        status = Status.RESERVED;
    }

    public void close() {
        status = Status.CLOSED;
    }
    
    @Override
    public String toString() {
        return number + ": " + player1 + " - " + player2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            //System.out.println("billiard.model.Match.equals(): obj = null");
            return false;
        }
        if (!getClass().getSimpleName().equals(obj.getClass().getSimpleName())) {
            //System.out.println("billiard.model.Match.equals() class not equal");
            return false;
        }
        //System.out.println("billiard.model.Match.equals(): equal");
        Match other = (Match) obj;
        return this.id == other.getId();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }
        
    private class Status {
        public static final String CREATED="Created";
        public static final String RESERVED="Reserved";
        public static final String STARTED="Started";
        public static final String SUSPENDED="Suspended";
        public static final String ENDED="Ended";
        public static final String CLOSED="Closed";
    }
}
