/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.websocket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author mg
 */
@ServerEndpoint("/taggedfeed")
public class TaggedFeedEndPoint {

    private static final Map<String, Set<Session>> peersByTag = new HashMap<>();
    private static final Map<String, Set<String>> tagsByPeer = new HashMap<>();

    protected static void delete(Session aPeer) {
        Set<String> tags = tagsByPeer.get(aPeer.getId());
        tagsByPeer.remove(aPeer.getId());
        if (tags != null) {
            for (String tag : tags) {
                Set<Session> peers = peersByTag.get(tag);
                peers.remove(aPeer);
                if (peers.isEmpty()) {
                    peersByTag.remove(tag);
                }
            }
        }
    }

    public TaggedFeedEndPoint(){
        super();
    }
    
    @OnMessage
    public void addPeer(Session aPeer, String aData) {
        synchronized (peersByTag) {
            delete(aPeer);
            String[] tagsOfInterest = aData.split("\n");
            if (tagsOfInterest != null) {
                for (String tag : tagsOfInterest) {
                    if (tag != null) {
                        tag = tag.trim();
                        if (!tag.isEmpty()) {
                            Set<Session> taggedPeers = peersByTag.get(tag);
                            if (taggedPeers == null) {
                                taggedPeers = new HashSet<>();
                                peersByTag.put(tag, taggedPeers);
                            }
                            taggedPeers.add(aPeer);

                            Set<String> tags = tagsByPeer.get(aPeer.getId());
                            if (tags == null) {
                                tags = new HashSet<>();
                                tagsByPeer.put(aPeer.getId(), tags);
                            }
                            tags.add(tag);
                        }
                    }
                }
            }
        }
    }

    @OnClose
    public void closed(Session aPeer, CloseReason aReason) {
        synchronized (peersByTag) {
            delete(aPeer);
        }
    }

    public static synchronized void broadcast(String aTag, String aData) {
        if (aTag != null) {
            aTag = aTag.trim();
            if (!aTag.isEmpty()) {
                synchronized (peersByTag) {
                    Set<Session> taggedPeers = peersByTag.get(aTag);
                    if (taggedPeers != null) {
                        for (Session peer : taggedPeers) {
                            peer.getAsyncRemote().sendText(aData);
                        }
                    }
                }
            }
        }
    }
}
