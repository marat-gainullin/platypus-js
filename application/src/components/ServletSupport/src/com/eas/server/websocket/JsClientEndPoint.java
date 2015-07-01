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
    protected Scripts.LocalContext context;

    public JsClientEndPoint(WebSocketClientSession aSession, Scripts.LocalContext aContext) {
        super();
        session = aSession;
        context = aContext;
    }

    private void inContext(Runnable aTask) {
        Scripts.LocalContext oldContext = Scripts.getContext();
        Scripts.setContext(context);
        try {
            aTask.run();
        } finally {
            Scripts.setContext(oldContext);
        }
    }

    @OnMessage
    public void onMessage(Session websocketSession, String aData) {
        if (onmessage != null) {
            inContext(() -> {
                Scripts.getSpace().process(() -> {
                    JSObject messageEvent = Scripts.getSpace().makeObj();
                    messageEvent.setMember("data", aData);
                    onmessage.call(session.getPublished(), new Object[]{messageEvent});
                });
            });
        }
    }

    @OnOpen
    public void onOpen(Session aSession) {
        if (onopen != null) {
            inContext(() -> {
                Scripts.getSpace().process(() -> {
                    onopen.call(session.getPublished(), new Object[]{});
                });
            });
        }
    }

    @OnClose
    public void onClose(Session websocketSession, CloseReason aReason) {
        if (onclose != null) {
            inContext(() -> {
                Scripts.getSpace().process(() -> {
                    JSObject closeEvent = Scripts.getSpace().makeObj();
                    closeEvent.setMember("wasClean", aReason.getCloseCode() == CloseReason.CloseCodes.NORMAL_CLOSURE);
                    closeEvent.setMember("code", aReason.getCloseCode().getCode());
                    closeEvent.setMember("reason", aReason.getReasonPhrase());
                    onclose.call(session.getPublished(), new Object[]{closeEvent});
                });
            });
        }
    }

    @OnError
    public void onError(Session websocketSession, Throwable aError) {
        if (onerror != null) {
            inContext(() -> {
                Scripts.getSpace().process(() -> {
                    JSObject errorEvent = Scripts.getSpace().makeObj();
                    errorEvent.setMember("message", aError.getMessage());
                    onerror.call(session.getPublished(), new Object[]{errorEvent});
                });
            });
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
