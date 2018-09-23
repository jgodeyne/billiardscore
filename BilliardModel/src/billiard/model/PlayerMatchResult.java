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
public class PlayerMatchResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private int points = 0;
    private double percentage = 0;
    private int innings = 0;
    private int highestRun = 0;
    private int matchPoints = 0;
    private final ArrayList<Integer> scoreList;
    private final ArrayList<Integer> totalList;

    public PlayerMatchResult(int points, int innings, int highestRun, ArrayList<Integer> scoreList, ArrayList<Integer> totalList) {
        this.points = points;
        this.innings = innings;
        this.highestRun = highestRun;
        this.scoreList = scoreList;
        this.totalList = totalList;
    }

    public int getPoints() {
        return points;
    }

    public int getInnings() {
        return innings;
    }

    public double getAverage() {
        return (double) points / innings;
    }

    public int getHighestRun() {
        return highestRun;
    }

    public ArrayList getScoreList() {
        return scoreList;
    }

    public ArrayList getTotalList() {
        return totalList;
    }

    public int getMatchPoints() {
        return matchPoints;
    }

    public void setMatchPoints(int matchPoints) {
        this.matchPoints = matchPoints;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
