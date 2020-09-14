package life.majiang.community.util;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;

public class SendqqMail {
	public static void send(String people,String password,String title) throws AddressException, MessagingException, GeneralSecurityException {
        Properties props = new Properties();
        props.setProperty("mail.host", "smtp.qq.com");
        props.setProperty("mail.smtp.auth", "true");
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);
        Authenticator authenticator = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("","");
            }
        };        
        Session session = Session.getDefaultInstance(props, authenticator);
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(""));
        message.setRecipient(RecipientType.TO, new InternetAddress(people));
        message.setSubject(title);
        String str =password;
        message.setContent(str, "text/html;charset=UTF-8");
        Transport.send(message);
    }
}
