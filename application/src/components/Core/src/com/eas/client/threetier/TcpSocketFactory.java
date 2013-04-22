/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.threetier;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.SocketFactory;

/**
 *
 * @author pk
 */
public class TcpSocketFactory extends SocketFactory {

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException
    {
        return new Socket(host, port);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
