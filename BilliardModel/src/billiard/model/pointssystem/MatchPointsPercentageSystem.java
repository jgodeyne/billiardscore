/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model.pointssystem;

import billiard.model.TeamCompetition;
import billiard.model.Match;

/**
 *
 * @author jean
 */
public class MatchPointsPercentageSystem implements PointsSystemInterface {

    @Override
    public void determineMatchPoints(Match match) {
        int percPlayer1 = (match.getPlayer1Result().getPoints() * 10) / match.getPlayer1().getTsp();
        int percPlayer2 = (match.getPlayer2Result().getPoints() * 10) / match.getPlayer2().getTsp();
        if(match.getWinner()==0) {
            match.getPlayer1Result().setMatchPoints(1+percPlayer1);
            match.getPlayer2Result().setMatchPoints(1+percPlayer2);
        } else if (match.getWinner()==1) {
            match.getPlayer1Result().setMatchPoints(2+percPlayer1);
            match.getPlayer2Result().setMatchPoints(0+percPlayer2);            
        } else {
            match.getPlayer1Result().setMatchPoints(0+percPlayer1);
            match.getPlayer2Result().setMatchPoints(2+percPlayer2);
        }
    }

    @Override
    public void determineCompetitionPoints(TeamCompetition competition) {
        int intPointsT1 = 0;
        int intPointsT2 = 0;
        for (Match match : competition.getMatches()) {
            if (competition.getTeam1().isPlayerOfTeam(match.getPlayer1())) {
                intPointsT1 += match.getPlayer1Result().getMatchPoints();
            } else if (competition.getTeam2().isPlayerOfTeam(match.getPlayer1())) {
                intPointsT2 += match.getPlayer1Result().getMatchPoints();
            }
            if (competition.getTeam1().isPlayerOfTeam(match.getPlayer2())) {
                intPointsT1 += match.getPlayer2Result().getMatchPoints();
            } else if (competition.getTeam2().isPlayerOfTeam(match.getPlayer2())) {
                intPointsT2 += match.getPlayer2Result().getMatchPoints();
            }
        }
        competition.getTeam1Result().setMatchPoints(intPointsT1);
        competition.getTeam2Result().setMatchPoints(intPointsT2);
    }    
}
