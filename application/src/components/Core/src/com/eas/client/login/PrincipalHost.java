/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

/**
 * Service interface for getting principal.
 * In server it can be current principal for specific client request.
 * @author vv
 */
public interface PrincipalHost {
    
    public PlatypusPrincipal getPrincipal();
    
}
