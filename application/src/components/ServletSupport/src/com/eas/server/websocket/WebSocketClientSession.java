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
import com.eas.script.ScriptObj;
import com.eas.script.Scripts;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
@ScriptObj(name = "WebSocket")
public class WebSocketClientSession implements HasPublished {

    protected JSObject published;
    protected Session webSocketSession;
    //
    protected JsClientEndPoint endPoint = new JsClientEndPoint(this, Scripts.getContext(), Scripts.getSpace());

    @ScriptFunction(params = {"uri"})
    public WebSocketClientSession(String aUri) throws Exception {
        super();
        Scripts.getSpace().process(() -> {
            try {
                webSocketSession = ContainerProvider.getWebSocketContainer().connectToServer(endPoint, URI.create(aUri));
            } catch (DeploymentException | IOException ex) {
                Logger.getLogger(WebSocketClientSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @ScriptFunction
    public void close(Double opCode, String aReason) throws Exception {
        if (opCode == null) {
            opCode = Double.valueOf(CloseReason.CloseCodes.NO_STATUS_CODE.getCode());
        }
        if (aReason == null) {
            aReason = "";
        }
        if (webSocketSession.isOpen()) {
            webSocketSession.close(new CloseReason(CloseReason.CloseCodes.getCloseCode(opCode.intValue()), aReason));
        }
    }

    @ScriptFunction(params = "data")
    public void send(String aData) {
        try {
            if (webSocketSession.isOpen()) {
                webSocketSession.getBasicRemote().sendText(aData);
            }
        } catch (IOException ex) {
            Logger.getLogger(WebSocketClientSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @ScriptFunction
    public String getId() {
        return webSocketSession.getId();
    }

    @ScriptFunction
    public String getProtocolVersion() {
        return webSocketSession.getProtocolVersion();
    }

    @ScriptFunction
    public String getQuery() {
        return webSocketSession.getQueryString();
    }

    @ScriptFunction
    public String getUri() {
        return webSocketSession.getRequestURI().toString();
    }

    @ScriptFunction
    public JSObject getOnopen() {
        return endPoint.getOnopen();
    }

    @ScriptFunction
    public void setOnopen(JSObject onopen) {
        endPoint.setOnopen(onopen);
    }

    @ScriptFunction
    public JSObject getOnclose() {
        return endPoint.getOnclose();
    }

    @ScriptFunction
    public void setOnclose(JSObject onclose) {
        endPoint.setOnclose(onclose);
    }

    @ScriptFunction
    public JSObject getOnerror() {
        return endPoint.getOnerror();
    }

    @ScriptFunction
    public void setOnerror(JSObject onerror) {
        endPoint.setOnerror(onerror);
    }

    @ScriptFunction
    public JSObject getOnmessage() {
        return endPoint.getOnmessage();
    }

    @ScriptFunction
    public void setOnmessage(JSObject onmessage) {
        endPoint.setOnmessage(onmessage);
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
