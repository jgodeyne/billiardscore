/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.common.hazelcast;

import java.io.Serializable;

/**
 *
 * @author jean
 */
public class StartMatchMessage implements Serializable{
    private static final long serialVersionUID = 1L;
    private long matchId;
    private String scoreBoardId;

    public StartMatchMessage(long matchId, String scoreBoardId) {
        this.matchId = matchId;
        this.scoreBoardId = scoreBoardId;
    }

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public String getScoreBoardId() {
        return scoreBoardId;
    }

    public void setScoreBoardId(String scoreBoardId) {
        this.scoreBoardId = scoreBoardId;
    }
    
    
}
