package org.sevensource.commons.email.html.spring;

import java.io.InputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.sun.mail.smtp.SMTPMessage;

/**
 * Use a {@link SMTPMessage} instead of Springs SmartMimeMessage
 * 
 * @author pgaschuetz
 *
 */
public class ConfigurableJavaMailSenderImpl extends JavaMailSenderImpl {
	
	private final static Logger logger = LoggerFactory.getLogger(ConfigurableJavaMailSenderImpl.class);
	
	
	@Override
	public MimeMessage createMimeMessage() {
		return new SMTPMessage(getSession());
	}
	
	@Override
	public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
		try {
			return new SMTPMessage(getSession(), contentStream);
		}
		catch (MessagingException ex) {
			throw new MailParseException("Could not parse raw MIME content", ex);
		}
	}
}
