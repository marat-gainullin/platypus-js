/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.DatabasesClient;
import com.eas.client.login.DbPlatypusPrincipal;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.OutHashRequest;
import com.eas.sensors.sms.SmsSender;
import com.eas.sensors.sms.SmsSenderSmsFeedback;
import com.eas.server.PlatypusServerCore;
import com.eas.server.RequestHandler;
import com.eas.server.SessionManager;

/**
 *
 * @author bl
 */
public class OutHashHandler extends RequestHandler<OutHashRequest> {

    public static final String SMS_PREFIX = "Password=";
    public static final String SMS_SIGN = "Alt-Soft";

    public OutHashHandler(PlatypusServerCore server, OutHashRequest rq) {
        super(server, rq);
    }

    /**
     * Возвращает номер телефона по имени пользователя
     * @return 
     */
    public String getPhoneByUserName(String aUserName) throws Exception {
        DbPlatypusPrincipal principal = DatabasesClient.userNameToPrincipal(getServerCore().getDatabasesClient(), aUserName);
        return principal.getPhone();
    }

    @Override
    protected Response handle() throws Exception {
        int resCode = OutHashRequest.Response.RES_CODE_SENDING_ERROR;
        String resStr = null;
        String phone = null;
        try {
            phone = getPhoneByUserName(getRequest().getUserName());
            if (phone != null) {
                SmsSender smsSender = new SmsSenderSmsFeedback();
                smsSender.setAuth("antonbulankin", "ALt258456S0Ft");
                String tmpPassword = SessionManager.TemporaryPasswords.generatePassword();
                getServerCore().getSessionManager().getTemporaryPasswords().registerTempPassword(getRequest().getUserName(), tmpPassword);
                if (smsSender.sendSms(SMS_PREFIX + tmpPassword, phone, SMS_SIGN)) {
                    resCode = OutHashRequest.Response.RES_CODE_SENDING_SUCCESS;
                } else {
                    if (smsSender.getResult().get(SmsSender.KEY_RESULT_CODE) instanceof Integer) {
                        resCode = OutHashRequest.Response.RES_CODE_SENDING_ERROR;
                        resStr = (String) smsSender.getResult().get(SmsSender.KEY_RESULT_STR);
                    }
                }
            }else{
                resCode = OutHashRequest.Response.RES_CODE_SENDING_NO_PHONE;
            }
        } catch (Exception e) {
            resCode = OutHashRequest.Response.RES_CODE_SENDING_NO_PHONE;
            resStr = e.getMessage();
        }
        return new OutHashRequest.Response(getRequest().getID(), resCode, resStr);
    }
}
