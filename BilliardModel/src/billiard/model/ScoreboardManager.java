/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

import billiard.common.hazelcast.SyncManager;
import com.hazelcast.core.IList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jean
 */
public class ScoreboardManager {
    private static final String SCOREBOARDS_LIST = "scoreboards";

    private static IList<String> scoreboards;
    private static List<String> localScoreboards;
    
    private static ScoreboardManager instance;

    private ScoreboardManager() {
        if(SyncManager.hazelcastEnabled) {
            scoreboards = SyncManager.getHazelCastInstance().getList(SCOREBOARDS_LIST);
        } else {
            localScoreboards = new ArrayList();
        }
    }
    
    public static ScoreboardManager getInstance() {
        if(instance==null) {
            instance = new ScoreboardManager();
        }
        return instance;
    }

    public void addScoreboard(String scoreboardId) {
        if(SyncManager.isHazelcastEnabled()) {
            scoreboards.add(scoreboardId);
        } else {
            localScoreboards.add(scoreboardId);
        }
    }
    
    public void removeScoreboard(String id) {
        if(SyncManager.hazelcastEnabled) {
            scoreboards.remove(id);
        }else{
            localScoreboards.remove(id);
        }
    }
    
    public ArrayList<String> listScoreboards() {
        if(SyncManager.hazelcastEnabled) {
            return new ArrayList(scoreboards);
        } else {
            return new ArrayList(localScoreboards);
        }
    }
}
