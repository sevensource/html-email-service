package org.sevensource.commons.email.javamail;

import java.io.InputStream;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.sevensource.commons.email.util.SunMailSmtpMessageUtil;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Use a com.sun.mail.smtp.SMTPMessage instead of Springs SmartMimeMessage
 *
 * @author pgaschuetz
 *
 */
public class ConfigurableJavaMailSenderImpl extends JavaMailSenderImpl {

	public ConfigurableJavaMailSenderImpl() {
		super();
	}
	
	public ConfigurableJavaMailSenderImpl(Session session) {
		super();
		setSession(session);
	}

	@Override
	public MimeMessage createMimeMessage() {
		if(SunMailSmtpMessageUtil.isAvailable()) {
			return SunMailSmtpMessageUtil.newSMTPMessage(getSession());
		} else {
			return super.createMimeMessage();
		}
	}

	@Override
	public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
		if(SunMailSmtpMessageUtil.isAvailable()) {
			return SunMailSmtpMessageUtil.newSMTPMessage(getSession(), contentStream);
		} else {
			return super.createMimeMessage(contentStream);
		}
	}
}
