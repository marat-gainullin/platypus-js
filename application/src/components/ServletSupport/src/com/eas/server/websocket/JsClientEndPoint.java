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

    protected WebSocket session;
    protected JSObject onopen;
    protected JSObject onclose;
    protected JSObject onerror;
    protected JSObject onmessage;

    public JsClientEndPoint(WebSocket aSession) {
        super();
        session = aSession;
    }

    @OnMessage
    public void onMessage(Session websocketSession, String aData) {
        if (onmessage != null) {
            session.inContext(() -> {
                JSObject messageEvent = ScriptUtils.makeObj();
                messageEvent.setMember("data", aData);
                onmessage.call(session.getPublished(), new Object[]{messageEvent});
            });
        }
    }

    @OnOpen
    public void onOpen(Session aSession) {
        if (onopen != null) {
            session.inContext(() -> {
                onopen.call(session.getPublished(), new Object[]{});
            });
        }
    }

    @OnClose
    public void onClose(Session websocketSession, CloseReason aReason) {
        if (onclose != null) {
            session.inContext(() -> {
                JSObject closeEvent = ScriptUtils.makeObj();
                closeEvent.setMember("wasClean", aReason.getCloseCode() == CloseReason.CloseCodes.NORMAL_CLOSURE);
                closeEvent.setMember("code", aReason.getCloseCode().getCode());
                closeEvent.setMember("reason", aReason.getReasonPhrase());
                onclose.call(session.getPublished(), new Object[]{closeEvent});
            });
        }
    }

    @OnError
    public void onError(Session websocketSession, Throwable aError) {
        if (onerror != null) {
            session.inContext(() -> {
                JSObject errorEvent = ScriptUtils.makeObj();
                errorEvent.setMember("message", aError.getMessage());
                onerror.call(session.getPublished(), new Object[]{errorEvent});
            });
        }
    }

    public JSObject getOnopen() {
        return onopen;
    }

    public void setOnopen(JSObject onopen) {
        this.onopen = onopen;
    }

    public JSObject getOnclose() {
        return onclose;
    }

    public void setOnclose(JSObject onclose) {
        this.onclose = onclose;
    }

    public JSObject getOnerror() {
        return onerror;
    }

    public void setOnerror(JSObject onerror) {
        this.onerror = onerror;
    }

    public JSObject getOnmessage() {
        return onmessage;
    }

    public void setOnmessage(JSObject onmessage) {
        this.onmessage = onmessage;
    }
}
