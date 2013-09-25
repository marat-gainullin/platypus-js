/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.websocket;

import com.eas.server.PlatypusServerCore;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public TaggedFeedEndPoint() {
        super();
    }

    @OnMessage
    public void addPeer(Session aPeer, String aData) throws Exception {
        synchronized (peersByTag) {
            PlatypusServerCore core = PlatypusServerCore.getInstance();
            if (core == null) {
                throw new IllegalStateException("Platypus server core is not initialized");
            }
            delete(aPeer);
            String[] tagsOfInterest = aData.split("\n");
            if (tagsOfInterest != null) {
                for (String tag : tagsOfInterest) {
                    if (tag != null) {
                        tag = tag.trim();
                        if (!tag.isEmpty()) {
                            Principal principal = aPeer.getUserPrincipal();
                            if (core.isUserInApplicationRole(principal != null ? principal.getName() : null, tag)) {
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
                            } else {
                                Logger.getLogger(TaggedFeedEndPoint.class.getName()).log(Level.SEVERE, String.format("User %s is not allowed to sign on tag %s", principal != null ? principal.getName() : String.valueOf(null), tag));
                            }
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
