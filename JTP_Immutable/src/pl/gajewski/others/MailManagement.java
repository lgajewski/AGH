package pl.gajewski.others;

/**
 * Created by Lukasz on 16.04.14.
 */

import org.apache.commons.mail.*;
import org.apache.log4j.Logger;

import pl.gajewski.Language;

public class MailManagement {

    static Logger log = Logger.getLogger(MailManagement.class.getName());

    public static void sendMail() {

        Email email = new SimpleEmail();
        email.setHostName("smtp.googlemail.com");
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator("projektex5", "projektex"));
        email.setSSLOnConnect(true);
        try {
            String from = "projektex5@gmail.com";
            String subject = "TestMail";
            String msg = "This is a test mail ... :-)";
            String to = "dragen12@gmail.com";

            email.setFrom(from);
            email.setSubject(subject);
            email.setMsg(msg);
            email.addTo(to);
            email.send();
            log.info(Language.Lang.MSG_EMAIL.getMsg() + " " + to);
        } catch (EmailException e) {
            System.out.println("ERROR, MORE INFO IN LOGS");
            log.debug(e.getMessage());
        }

    }

}
