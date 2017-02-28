/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.sensors.api;

/**
 * Iterface, intended to abstract data packets recieving process.
 * This interface should be implemented by data reciever.
 * WARNING! The implementation should not perforn lengthy operations, such as database queries etc.
 * The operations must be quick and easy, such as enqueuing request to an application server and immidiatly returning.
 * @author mg
 */
public interface PacketReciever {

    String ATTRIBUTE_SESSION_ID = "sessionId";
    long WAIT_SEND_TIMEOUT = 20 * 1000;

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
     */
    Object received(Packet aPacket) throws Exception;

}
