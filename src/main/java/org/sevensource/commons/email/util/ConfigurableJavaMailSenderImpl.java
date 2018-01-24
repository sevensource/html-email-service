package org.sevensource.commons.email.util;

import java.io.InputStream;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Use a com.sun.mail.smtp.SMTPMessage instead of Springs SmartMimeMessage
 *
 * @author pgaschuetz
 *
 */
public class ConfigurableJavaMailSenderImpl extends JavaMailSenderImpl {

	private final static Logger logger = LoggerFactory.getLogger(ConfigurableJavaMailSenderImpl.class);


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
