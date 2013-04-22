/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.login;

import com.eas.client.settings.EasSettings;
import javax.security.auth.callback.Callback;

/**
 *
 * @author mg
 */
public interface LoginCallback extends Callback{

    public boolean tryToLogin(EasSettings aSettings, String aDbUserName, char[] aDbPassword, String aUserName, char[] aPassword) throws Exception;
}
