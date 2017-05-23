package com.eas.server.mail;

import com.eas.script.Scripts;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.naming.InitialContext;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.naming.Context;
import javax.naming.NamingException;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mgainullin
 */
public class Messages {

    private static final String DEFAULT_LOOKUP_PLACE = "java:comp/env";
    private static final String DEFAULT_MAIL_SESSION_NAME = "mail/Session";
    private static final String DEFAULT_CONTENT_TYPE = "text/plain; charset=utf-8";
    
    private final String lookup;
    private final String sessionName;
    private final String contentType;
    
    public Messages(){
        super();
        lookup = DEFAULT_LOOKUP_PLACE;
        sessionName = DEFAULT_MAIL_SESSION_NAME;
        contentType = DEFAULT_CONTENT_TYPE;
    }
    public Messages(final String aContentType){
        super();
        lookup = DEFAULT_LOOKUP_PLACE;
        sessionName = DEFAULT_MAIL_SESSION_NAME;
        contentType = aContentType;
    }
    
    public Messages(final String aLookup, final String aSessionName){
        super();
        lookup = aLookup;
        sessionName = aSessionName;
        contentType = DEFAULT_CONTENT_TYPE;
    }
    
    public Messages(final String aLookup, final String aSessionName, final String aContentType){
        super();
        lookup = aLookup;
        sessionName = aSessionName;
        contentType = aContentType;
    }
    
    public void send(String from, String to, String subject, String body, JSObject aOnSuccess, JSObject aOnFailure) throws NamingException, AddressException, MessagingException {
        InitialContext initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup(lookup);
        Session session = (Session) envCtx.lookup(sessionName);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject(subject);
        message.setContent(body, contentType);
        Scripts.Space callingSpace = Scripts.getSpace();
        Scripts.LocalContext callingContext = Scripts.getContext();
        callingContext.incAsyncsCount();
        Scripts.startBIO(() -> {
            try {
                Transport.send(message);
                if (aOnSuccess != null) {
                    callingSpace.process(callingContext, () -> {
                        aOnSuccess.call(null, new Object[]{});
                    });
                }
            } catch (MessagingException ex) {
                Logger.getLogger(Messages.class.getName()).log(Level.SEVERE, null, ex);
                if (aOnFailure != null) {
                    callingSpace.process(callingContext, () -> {
                        aOnFailure.call(null, new Object[]{ex});
                    });
                }
            }
        });
    }

}
