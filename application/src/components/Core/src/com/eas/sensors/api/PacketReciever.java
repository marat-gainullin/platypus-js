/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.sensors.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.session.IoSession;

/**
 * Iterface, intended to abstract data packets recieving process.
 * This interface should be implemented by data reciever.
 * WARNING! The implementation should not perforn lengthy operations, such as database queries etc.
 * The operations must be quick and easy, such as enqueuing request to an application server and immidiatly returning.
 * @author mg
 */
public interface PacketReciever {
    public final static Map<String, IoSession> retranslateSessions = new ConcurrentHashMap<>();

    public static final String ATTRIBUTE_SESSION_ID = "sessionId";
    public static final long WAIT_SEND_TIMEOUT = 20 * 1000;

    /**
     * This method is called by device drivers subsystem.
     * @param aPacket Data packet recieved from abstract device.
     * @return Message object, expressed in terms of subject area to be sent to
     * the connected device as a response on recieved request.
     * It's ok to return null here. If null returned no ant responce will be sent.
     * If some message is to be sent to the device, but it is not an answer on
     * device's request, than client code should return null.
     * Such situation is processed with <code>pending()</code> method.
     * @throws java.lang.Exception
     * @see PositioningPacket
     * @see #pending()
     */
    public Object received(Packet aPacket) throws Exception;

    
    //TODO Need fix. Use sessions for send request
    /**
     * Returns and removes first pending message object, expressed in terms of subject area.
     * messages by itself.
     * <b>This method is useful when connection is present, but device is a client
     * and message is not response on it's request.</b>
     * When message is response on device's request, no need to process pending responses.
     * In this case <code>recieved()</code> method will return the response object.
     * @param aDeviceId Identifier on device. It might be an imei or other object, identifying th device.
     * @return First pending message object, to be sent to device.
     * @see #received(com.eas.sensors.positioning.PositioningPacket)
     */
    //public DevicesCommunication.DeviceRequest getRequest(String aDeviceId);
    
    /**
     * Returns and removes first waitingRequest message object, expressed in terms of subject area.
     * <b>This method gets first request has waiting responce from device from queue.</b>
     * @param aDeviceId Identifier on device. It might be an imei or other object, identifying th device.
     * @return First sendRequest message object, to be sent to device.
     * @see #received(com.eas.sensors.positioning.PositioningPacket)
     */
    //public DevicesCommunication.DeviceRequest getWaitingRequest(String aDeviceId);
    
    //public void putWaitingRequest(String aDeviceId, DevicesCommunication.DeviceRequest aRequest);
    
}
