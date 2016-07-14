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
public class TeamResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private int points = 0;
    private int innings = 0;
    private int highestRun = 0;
    private int matchPoints = 0;
    private float percentage = 0;

    public TeamResult(int points, int innings, int highestRun, int matchPoints, 
            float percentage) {
        this.points = points;
        this.innings = innings;
        this.highestRun = highestRun;
        this.matchPoints = matchPoints;
        this.percentage = percentage;
    }

    public int getPoints() {
        return points;
    }

    public int getInnings() {
        return innings;
    }

    public int getHighestRun() {
        return highestRun;
    }

    public int getMatchPoints() {
        return matchPoints;
    }

    public float getPercentage() {
        return percentage;
    }

    public double getAverage() {
        return (double)points / innings;
    }

    public void setMatchPoints(int matchPoints) {
        this.matchPoints = matchPoints;
    }    
}
