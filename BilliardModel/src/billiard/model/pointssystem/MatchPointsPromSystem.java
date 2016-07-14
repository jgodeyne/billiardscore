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
public class MatchPointsPromSystem implements PointsSystemInterface {

    @Override
    public void determineMatchPoints(Match match) {
        double avg1 = match.getPlayer1Result().getAverage();
        int tsp1 = match.getPlayer1().getTsp();
        double avg2 = match.getPlayer2Result().getAverage();
        int tsp2 = match.getPlayer2().getTsp();
        if(match.getWinner()==0) {
            match.getPlayer1Result().setMatchPoints(1+determineExtraPoint(tsp1, avg1));
            match.getPlayer2Result().setMatchPoints(1+determineExtraPoint(tsp2, avg2));
        } else if (match.getWinner()==1) {
            match.getPlayer1Result().setMatchPoints(2+determineExtraPoint(tsp1, avg1));
            match.getPlayer2Result().setMatchPoints(0+determineExtraPoint(tsp2, avg2));            
        } else {
            match.getPlayer1Result().setMatchPoints(0+determineExtraPoint(tsp1, avg1));
            match.getPlayer2Result().setMatchPoints(2+determineExtraPoint(tsp2, avg2));
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
    
    private int determineExtraPoint(int tsp, double avg) {
        int extra = 0;
        if(tsp==120) {
            if(avg > 19.99) {
                extra=1;
            }
        }else if (tsp==165) {
            if(avg > 29.99) {
                extra=1;
            }
        }else if (tsp==225) {
            if (avg > 44.99) {
                extra = 1;
            }
        }
        return extra;
    }
}
