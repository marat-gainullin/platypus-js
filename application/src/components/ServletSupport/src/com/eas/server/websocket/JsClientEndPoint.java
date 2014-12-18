/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.websocket;

import com.eas.script.ScriptUtils;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
@ClientEndpoint
public class JsClientEndPoint {

    protected WebSocketClientSession session;
    protected JSObject onopen;
    protected JSObject onclose;
    protected JSObject onerror;
    protected JSObject onmessage;

    public JsClientEndPoint(WebSocketClientSession aSession) {
        super();
        session = aSession;
    }

    @OnMessage
    public void onMessage(Session websocketSession, String aData) {
        session.inContext(() -> {
            if (onmessage != null) {
                JSObject messageEvent = ScriptUtils.makeObj();
                messageEvent.setMember("data", aData);
                onmessage.call(session.getPublished(), new Object[]{messageEvent});
            }
        });
    }

    @OnOpen
    public void onOpen(Session aSession) {
        ScriptUtils.submitTask(() -> {
            ScriptUtils.acceptTaskResult(() -> {
                if (onopen != null) {
                    onopen.call(session.getPublished(), new Object[]{});
                }
            });
        });
    }

    @OnClose
    public void onClose(Session websocketSession, CloseReason aReason) {
        Runnable actor = () -> {
            if (onclose != null) {
                JSObject closeEvent = ScriptUtils.makeObj();
                closeEvent.setMember("wasClean", aReason.getCloseCode() == CloseReason.CloseCodes.NORMAL_CLOSURE);
                closeEvent.setMember("code", aReason.getCloseCode().getCode());
                closeEvent.setMember("reason", aReason.getReasonPhrase());
                onclose.call(session.getPublished(), new Object[]{closeEvent});
            }
        };
        if (ScriptUtils.getLock() == null) {
            session.inContext(actor);
        } else {// already in context
            final Object lock = ScriptUtils.getLock();
            synchronized (lock) {
                actor.run();
            }
        }
    }

    @OnError
    public void onError(Session websocketSession, Throwable aError) {
        Runnable actor = () -> {
            if (onerror != null) {
                JSObject errorEvent = ScriptUtils.makeObj();
                errorEvent.setMember("message", aError.getMessage());
                onerror.call(session.getPublished(), new Object[]{errorEvent});
            }
        };
        if (ScriptUtils.getLock() == null) {
            session.inContext(actor);
        } else {// already in context
            final Object lock = ScriptUtils.getLock();
            synchronized (lock) {
                actor.run();
            }
        }
    }

    public JSObject getOnopen() {
        return onopen;
    }

    public void setOnopen(JSObject aValue) {
        onopen = aValue;
    }

    public JSObject getOnclose() {
        return onclose;
    }

    public void setOnclose(JSObject aValue) {
        onclose = aValue;
    }

    public JSObject getOnerror() {
        return onerror;
    }

    public void setOnerror(JSObject aValue) {
        onerror = aValue;
    }

    public JSObject getOnmessage() {
        return onmessage;
    }

    public void setOnmessage(JSObject aValue) {
        onmessage = aValue;
    }
}
