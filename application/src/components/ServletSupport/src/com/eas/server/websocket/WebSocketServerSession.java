/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.websocket;

import com.eas.script.LpcTransient;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.Session;

/**
 *
 * @author mg
 */
public class WebSocketServerSession implements LpcTransient {

    protected final Session session;

    public WebSocketServerSession(Session aSession) {
        super();
        session = aSession;
    }

    public void close(Double opCode, String aReason) throws Exception {
        if (opCode == null) {
            opCode = Double.valueOf(CloseReason.CloseCodes.NO_STATUS_CODE.getCode());
        }
        if (aReason == null) {
            aReason = "";
        }
        if (session.isOpen()) {
            session.close(new CloseReason(CloseReason.CloseCodes.getCloseCode(opCode.intValue()), aReason));
        }
    }

    public void send(String aData) {
        if (aData != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(aData);
            } catch (IOException ex) {
                Logger.getLogger(WebSocketServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getId() {
        return session.getId();
    }

    public String getProtocolVersion() {
        return session.getProtocolVersion();
    }

    public String getQuery() {
        return session.getQueryString();
    }

    public String getUri() {
        return session.getRequestURI().toString();
    }
}
