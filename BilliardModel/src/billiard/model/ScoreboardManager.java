/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

import billiard.common.hazelcast.SyncManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jean
 */
public class ScoreboardManager {
    private static final String SCOREBOARDS_LIST = "scoreboards";

    private static String scoreboardId;
    private static List<String> scoreboards;
    
    private static ScoreboardManager instance;

    private ScoreboardManager() {
        if(SyncManager.hazelcastEnabled) {
            scoreboards = SyncManager.getHazelCastInstance().getList(SCOREBOARDS_LIST);
        } else {
            scoreboards = new ArrayList();
        }
    }
    
    public static ScoreboardManager getInstance() {
        if(instance==null) {
            instance = new ScoreboardManager();
        }
        return instance;
    }

    public void setScoreboardId(String scoreboardId) {
        ScoreboardManager.scoreboardId = scoreboardId;
        scoreboards.add(scoreboardId);
    }
    
    public void removeScoreboard(String id) {
        scoreboards.remove(id);
    }
    
    public ArrayList<String> listScoreboards() {
        return new ArrayList(scoreboards);
    }
}
