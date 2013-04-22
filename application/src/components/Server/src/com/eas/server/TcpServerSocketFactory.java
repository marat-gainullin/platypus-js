/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;

/**
 *
 * @author pk
 */
public class TcpServerSocketFactory extends ServerSocketFactory {

    @Override
    public ServerSocket createServerSocket(int port) throws IOException
    {
        return new ServerSocket(port);
    }

    @Override
    public ServerSocket createServerSocket(int port, int backlog) throws IOException
    {
        return new ServerSocket(port, backlog);
    }

    @Override
    public ServerSocket createServerSocket(int port, int backlog, InetAddress ifAddress) throws IOException
    {
        return new ServerSocket(port, backlog, ifAddress);
    }
}
