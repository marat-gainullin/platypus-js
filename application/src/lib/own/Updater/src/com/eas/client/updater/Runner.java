/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.updater;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AB
 */
public class Runner {

    /**
     *
     * @param args
     * @return bool result true if run ok 
     */
    public boolean runThis(String args) {
        try {
            Process process = Runtime.getRuntime().exec(args);
            if (process == null) {
                return false;
            }
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),"Cp866"))) {
                String s = "";
                s = bufferedReader.readLine();
                while (s != null) {
                    s="";  
                    s = bufferedReader.readLine();
                    //Нужно вычитывать поток для того что бы приложение точно запустилось под Linux
                }
            }
            return true;
        } catch (Exception ex) {
            Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, ex.getLocalizedMessage()+"||"+args, ex);
            return false;
        }
    }
}
