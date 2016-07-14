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
public class CompetitionPointsSystem implements PointsSystemInterface {

    @Override
    public void determineMatchPoints(Match match) {
        if(match.getWinner()==0) {
            match.getPlayer1Result().setMatchPoints(1);
            match.getPlayer2Result().setMatchPoints(1);
        } else if (match.getWinner()==1) {
            match.getPlayer1Result().setMatchPoints(2);
            match.getPlayer2Result().setMatchPoints(0);            
        } else {
            match.getPlayer1Result().setMatchPoints(0);
            match.getPlayer2Result().setMatchPoints(2);
        }
    }

    @Override
    public void determineCompetitionPoints(TeamCompetition competition) {
        int mpTeam1 = competition.getTeam1Result().getMatchPoints();
        int mpTeam2 = competition.getTeam2Result().getMatchPoints();
        if(mpTeam1 == mpTeam2) {
            competition.getTeam1Result().setMatchPoints(1);
            competition.getTeam2Result().setMatchPoints(1);
        } else if (mpTeam1 > mpTeam2) {
            competition.getTeam1Result().setMatchPoints(2);
            competition.getTeam2Result().setMatchPoints(0);            
        } else {
            competition.getTeam1Result().setMatchPoints(0);
            competition.getTeam2Result().setMatchPoints(2);            
        }
    }    
}
