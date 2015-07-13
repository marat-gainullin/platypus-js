/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.websocket;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class WebSocketServerSession implements HasPublished {

    protected JSObject published;
    protected Session session;

    public WebSocketServerSession(Session aSession) {
        super();
        session = aSession;
    }

    @ScriptFunction
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

    @ScriptFunction(params = "data")
    public void send(String aData) {
        if (aData != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(aData);
            } catch (IOException ex) {
                Logger.getLogger(WebSocketServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @ScriptFunction
    public String getId() {
        return session.getId();
    }

    @ScriptFunction
    public String getProtocolVersion() {
        return session.getProtocolVersion();
    }

    @ScriptFunction
    public String getQuery() {
        return session.getQueryString();
    }

    @ScriptFunction
    public String getUri() {
        return session.getRequestURI().toString();
    }

    @Override
    public void setPublished(JSObject aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            JSObject publisher = Scripts.getSpace().getPublisher(this.getClass().getName());
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }
}
