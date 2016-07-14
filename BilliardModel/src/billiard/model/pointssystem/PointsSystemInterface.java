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
public interface PointsSystemInterface {
    public void determineMatchPoints(Match match);
    public void determineCompetitionPoints(TeamCompetition competition);
}
