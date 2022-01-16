/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

import billiard.common.hazelcast.SyncManager;
import com.hazelcast.core.IList;
import com.hazelcast.core.ItemListener;
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
        if(SyncManager.isHazelcastEnabled()) {
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
        if(SyncManager.isHazelcastEnabled() && null != scoreboards) {
            scoreboards.add(scoreboardId);
        } else {
            localScoreboards.add(scoreboardId);
        }
    }
    
    public void removeScoreboard(String id) {
        if(SyncManager.isHazelcastEnabled() && null != scoreboards) {
            scoreboards.remove(id);
        }else{
            localScoreboards.remove(id);
        }
    }
    
    public ArrayList<String> listScoreboards() {
        if(SyncManager.isHazelcastEnabled()) {
            return new ArrayList(scoreboards);
        } else {
            return new ArrayList(localScoreboards);
        }
    }
    
    public void addItemListener(ItemListener listener) {
        if(SyncManager.isHazelcastEnabled() && null != scoreboards) {
            scoreboards.addItemListener(listener, true);
        }
    }
    
    public int nbrOfScoreboards() {
        if(SyncManager.isHazelcastEnabled() && null != scoreboards) {
            return scoreboards.size();
        }
        return 1;
    }
}
