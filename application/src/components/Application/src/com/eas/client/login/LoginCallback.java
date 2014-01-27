/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.login;

import javax.security.auth.callback.Callback;

/**
 *
 * @author mg
 */
public interface LoginCallback extends Callback{

    public boolean tryToLogin(String aUrl, String aUserName, char[] aPassword) throws Exception;
}
