/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.model;

import billiard.common.hazelcast.StartMatchMessage;
import billiard.common.hazelcast.SyncManager;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author jean
 */
public class MatchManager {
    private static final String MATCH_ID = "matchId";
    private static final String START_MATCH_TOPIC = "start_match";
    private static final String MATCHES_MAP = "matches";

    private static MatchManager instance;
    private static IMap<Long, Match> matches;
    private static HashMap<Long, Match> localMatches;
    private static AtomicLong matchId;
    private static ITopic startMatchTopic;

    private MatchManager() {
        if(SyncManager.hazelcastEnabled) {
            matches = SyncManager.getHazelCastInstance().getMap(MATCHES_MAP);
            startMatchTopic = SyncManager.getHazelCastInstance().getTopic(START_MATCH_TOPIC);
        } else {
            localMatches = new HashMap<>();
        }
        matchId = new AtomicLong();
    }
    
    public static MatchManager getInstance() {
        if(instance==null) {
            instance = new MatchManager();
        }
        return instance;
    }

    public void putMatch(Match match) {
        if(SyncManager.hazelcastEnabled) {
            matches.put(match.getId(), match);
        } else {
            localMatches.put(match.getId(), match);
        }
    }
    
    public void updateMatch(Match match) {
        if(SyncManager.hazelcastEnabled) {
            matches.replace(match.getId(), match);
        } else {
            localMatches.replace(match.getId(), match);
        }
    }

    public void removeMatch(Match match) {
        if(SyncManager.hazelcastEnabled) {
            matches.remove(match.getId());
        } else {
            localMatches.remove(match.getId());
        }
    }
    
    public Match getMatch(Match match) {
        if(SyncManager.hazelcastEnabled) {
            return matches.get(match.getId());
        } else {
            return localMatches.get(match.getId());
        }
    }
    
    public Match getMatch(long matchId) {
        if(SyncManager.hazelcastEnabled) {
            return matches.get(matchId);
        } else {
            return localMatches.get(matchId);
        }
    }
    
    public ArrayList listMatches() {
        ArrayList matchList = new ArrayList();
        if(SyncManager.isHazelcastEnabled()) {
            matches = SyncManager.getHazelCastInstance().getMap(MATCHES_MAP);
            if (null!=matches) {
                matchList.addAll(matches.values());
            }
        } else {
            if (null!=localMatches) {
                matchList.addAll(localMatches.values());
            }
        }
        return matchList;
    }
    
    public ArrayList listMatches(long competitionId) {
        ArrayList matchList = new ArrayList();
        if(SyncManager.isHazelcastEnabled()) {
            matches = SyncManager.getHazelCastInstance().getMap(MATCHES_MAP);
            if (null!=matches) {
                for (Map.Entry<Long, Match> entrySet : matches.entrySet()) {
                    Long key = entrySet.getKey();
                    Match value = entrySet.getValue();
                    if(value.getCompetitionId()==competitionId) {
                        matchList.add(value);
                    }
                }
            }
        } else {
            if (null!=localMatches) {
                for (Map.Entry<Long, Match> entrySet : localMatches.entrySet()) {
                    Long key = entrySet.getKey();
                    Match value = entrySet.getValue();
                    if(value.getCompetitionId()==competitionId) {
                        matchList.add(value);
                    }
                }
            }
        }
        return matchList;
    }    

    public void sendMatch(Match match, String scoreboardId) {
        if(startMatchTopic!=null) {
            StartMatchMessage msg = new StartMatchMessage(match.getId(), scoreboardId);
            startMatchTopic.publish(msg);
        }
        match.reserve();
        putMatch(match);
    }

    public ITopic getStartMatchTopic() {
        return startMatchTopic;
    }
    
    public static long generateMatchId() {
        if (SyncManager.isHazelcastEnabled()) {
            return SyncManager.getHazelCastInstance().getIdGenerator(MATCH_ID).newId();
        }
        return matchId.incrementAndGet();
    }
    
    public void addEntryListener(EntryListener listener) {
        if(SyncManager.hazelcastEnabled) {
            ((IMap) matches).addEntryListener(listener, true);
        }
    }
}
