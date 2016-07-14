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
public class TeamCompetition extends Competition implements Serializable {
    private static final long serialVersionUID = 1L;
    private String number = "";
    private final Team team1;
    private final Team team2;
    private TeamResult team1Result;
    private TeamResult team2Result;
    private String pointsSystem = "";
    private String summarySheetHTML = "";

    public TeamCompetition(String name, String discipline, String tableFormat, Team team1, Team team2) {
        super(name, discipline, tableFormat);
        this.team1 = team1;
        this.team2 = team2;
        for (int i = 0; i < team1.getPlayers().size(); i++) {
            String tmpDiscipline = discipline;
            if(tmpDiscipline.isEmpty()) {
                if(team1.getPlayers().get(i).getDiscipline().equals(team2.getPlayers().get(i).getDiscipline()))
                tmpDiscipline = team1.getPlayers().get(i).getDiscipline();
            }
            MatchManager.getInstance().putMatch(new Match(super.getId(),""+(i+1), tmpDiscipline,team1.getPlayers().get(i), team2.getPlayers().get(i)));
        }
    }

    public Team getTeam1() {
        return team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public TeamResult getTeam1Result() {
        return team1Result;
    }

    public TeamResult getTeam2Result() {
        return team2Result;
    }

    public ArrayList<Match> getMatches() {
        return MatchManager.getInstance().listMatches(this.getId());
    }
    
    public Match getMatch(long matchId) {
        return MatchManager.getInstance().getMatch(matchId);
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPointsSystem() {
        return pointsSystem;
    }

    public void setPointsSystem(String pointsSystem) {
        this.pointsSystem = pointsSystem;
    }

    public void setTeam1Result(TeamResult team1Result) {
        this.team1Result = team1Result;
    }

    public void setTeam2Result(TeamResult team2Result) {
        this.team2Result = team2Result;
    }

    public String getSummarySheetHTML() {
        return summarySheetHTML;
    }

    public void setSummarySheetHTML(String summarySheetHTML) {
        this.summarySheetHTML = summarySheetHTML;
    }

    public boolean isAllMatchedEnded () {
        boolean result = true;
        ArrayList<Match> matches = this.getMatches();
        for (Match match : matches) {
            if(!match.isEnded()) {
                result=false;
                break;
            }
        }
        return result;
    }
    
    public PlayerMatchResult getPlayerResult(Player player) {
        ArrayList<Match> matches = this.getMatches();
        for (Match match : matches) {
            if(match.getPlayer1().getLicentie().equals(player.getLicentie())) {
                return match.getPlayer1Result();
            } else if (match.getPlayer2().getLicentie().equals(player.getLicentie())) {
                return match.getPlayer2Result();
            }
        }
        return null;
    }
    
    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append("lic;name;tsp;points;innings;highest run;matchpoints\n");
        // TeamCompetition
        sb.append(this.getName()).append(";");
        sb.append(this.getNumber()).append("\n");
        
        //Team 1
        sb.append(team1.getName()).append("\n");
        
        //Team 1 Players result
        for(Player player: team1.getPlayers()) {
            PlayerMatchResult result = getPlayerResult(player);
            sb.append(player.getLicentie()).append(";");
            sb.append(player.getName()).append(";");
            sb.append(player.getTsp()).append(";");
            sb.append(result.getPoints()).append(";");            
            sb.append(result.getInnings()).append(";");
            sb.append(result.getHighestRun()).append(";");
            sb.append(result.getMatchPoints()).append("\n");
        }

        //Team 2
        sb.append(team2.getName()).append("\n");

        //Team 2 Players result
        for(Player player: team2.getPlayers()) {
            PlayerMatchResult result = getPlayerResult(player);
            sb.append(player.getLicentie()).append(";");
            sb.append(player.getName()).append(";");
            sb.append(player.getTsp()).append(";");
            sb.append(result.getPoints()).append(";");            
            sb.append(result.getInnings()).append(";");
            sb.append(result.getHighestRun()).append(";");
            sb.append(result.getMatchPoints()).append("\n");
        }

        return sb.toString();
    }
}
