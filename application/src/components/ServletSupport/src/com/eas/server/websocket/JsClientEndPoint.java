/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.websocket;

import com.eas.script.Scripts;
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
                JSObject messageEvent = Scripts.makeObj();
                messageEvent.setMember("data", aData);
                onmessage.call(session.getPublished(), new Object[]{messageEvent});
            }
        });
    }

    @OnOpen
    public void onOpen(Session aSession) {
        Scripts.submitTask(() -> {
            Scripts.acceptTaskResult(() -> {
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
                JSObject closeEvent = Scripts.makeObj();
                closeEvent.setMember("wasClean", aReason.getCloseCode() == CloseReason.CloseCodes.NORMAL_CLOSURE);
                closeEvent.setMember("code", aReason.getCloseCode().getCode());
                closeEvent.setMember("reason", aReason.getReasonPhrase());
                onclose.call(session.getPublished(), new Object[]{closeEvent});
            }
        };
        if (Scripts.getLock() == null) {
            session.inContext(actor);
        } else {// already in context
            final Object lock = Scripts.getLock();
            synchronized (lock) {
                actor.run();
            }
        }
    }

    @OnError
    public void onError(Session websocketSession, Throwable aError) {
        Runnable actor = () -> {
            if (onerror != null) {
                JSObject errorEvent = Scripts.makeObj();
                errorEvent.setMember("message", aError.getMessage());
                onerror.call(session.getPublished(), new Object[]{errorEvent});
            }
        };
        if (Scripts.getLock() == null) {
            session.inContext(actor);
        } else {// already in context
            final Object lock = Scripts.getLock();
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
