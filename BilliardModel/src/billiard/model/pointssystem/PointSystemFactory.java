/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model.pointssystem;

import java.util.ArrayList;

/**
 *
 * @author jean
 */
public class PointSystemFactory {
    
    public static PointsSystemInterface getPointSystem(String pointSystemString) {
        System.out.println("billiard.model.pointssystem.PointSystemFactory.getPointSystem(): " + pointSystemString);
        PointSystem pointSystem = PointSystem.valueOf(pointSystemString);
        return getPointSystem(pointSystem);
    }
        
    public static PointsSystemInterface getPointSystem(PointSystem pointSystem) {
        System.out.println("billiard.model.pointssystem.PointSystemFactory.getPointSystem(): " + pointSystem.toString());
        if(pointSystem.equals(PointSystem.PERCENTAGEPOINTS)) {
            return new PercentagePointsSystem();            
        } else if(pointSystem.equals(PointSystem.COMPETITIONPOINTS)) {
            return new CompetitionPointsSystem();
        } else if(pointSystem.equals(PointSystem.MATCHPOINTS_PROM)) {
            return new MatchPointsPromSystem();
        } else if(pointSystem.equals(pointSystem.MATCHPOINTS_PERCENTAGE_BONUS)) {
            return new MatchPointsPercentageBonusSystem();
        } else {
            return new MatchPointsSystem();
        }
    }

    public enum PointSystem {
        MATCHPOINTS, COMPETITIONPOINTS, PERCENTAGEPOINTS, MATCHPOINTS_PROM, MATCHPOINTS_PERCENTAGE_BONUS;
        
        public static ArrayList<String> stringValues() {
            ArrayList<String> values = new ArrayList<>();
            for (PointSystem t : PointSystem.values()) {
                values.add(t.name());
            }
            return values;
        }
    }
}
