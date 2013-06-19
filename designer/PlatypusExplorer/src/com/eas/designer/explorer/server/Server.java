/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.server;

/**
 * Represents server
 * @author vv
 */
public interface Server {
    ServerState getServerState();
    void setServerState(ServerState aServerState);
}
