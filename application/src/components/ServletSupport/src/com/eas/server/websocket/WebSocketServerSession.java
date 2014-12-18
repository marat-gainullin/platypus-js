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
import com.eas.script.ScriptUtils;
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
    protected com.eas.server.Session platypusSession;
    protected Object lock;
    protected Object request;
    protected Object response;
    //
    protected JSObject onClose;
    protected JSObject onError;
    protected JSObject onMessage;

    public WebSocketServerSession(Session aSession, com.eas.server.Session aPlatypusSession) {
        super();
        session = aSession;
        platypusSession = aPlatypusSession;
        request = ScriptUtils.getRequest();
        response = ScriptUtils.getResponse();
    }

    public com.eas.server.Session getPlatypusSession() {
        return platypusSession;
    }

    public Object getLock() {
        return lock;
    }

    public void setLock(Object aValue) {
        lock = aValue;
    }

    public Object getRequest() {
        return request;
    }

    public Object getResponse() {
        return response;
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
        session.getAsyncRemote().sendText(aData);
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

    @ScriptFunction
    public JSObject getOnclose() {
        return onClose;
    }

    @ScriptFunction
    public void setOnclose(JSObject aValue) {
        onClose = aValue;
    }

    @ScriptFunction
    public JSObject getOnerror() {
        return onError;
    }

    @ScriptFunction
    public void setOnerror(JSObject aValue) {
        onError = aValue;
    }

    @ScriptFunction
    public JSObject getOnmessage() {
        return onMessage;
    }

    @ScriptFunction
    public void setOnmessage(JSObject aValue) {
        onMessage = aValue;
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
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
