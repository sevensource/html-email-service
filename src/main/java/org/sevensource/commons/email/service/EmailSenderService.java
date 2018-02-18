package org.sevensource.commons.email.service;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;

import org.sevensource.commons.email.model.EmailModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;


@Service
public class EmailSenderService {

	private final static Logger logger = LoggerFactory.getLogger(EmailSenderService.class);

	private final JavaMailSender javaMailSender;


	@Inject
	public EmailSenderService(JavaMailSender javaMailSender) {
		this.javaMailSender =  javaMailSender;
	}


	/**
	 *
	 * @param emailModel the model describing the email to send
	 *
	 * @throws MailException throws {@link MailParseException}, {@link MailAuthenticationException}, {@link MailSendException} on error
	 *
	 */
	public void sendMail(final EmailModel emailModel) throws MailException {

		if (logger.isDebugEnabled()) {
			final InternetAddress[] addresses = emailModel.getTo().toArray(new InternetAddress[0]);
			logger.debug("Sending email from {} to {}", emailModel.getFrom(), InternetAddress.toUnicodeString(addresses));
		}

		final MimeMessagePreparator preparator = new HtmlMimeMessagePreparator(emailModel);

		// send
		try {
			javaMailSender.send(preparator);
		} catch(MailParseException e) {
			logger.error("Failed to parse message", e);
			throw e;
		} catch(MailAuthenticationException e) {
			logger.error("Failed to authenticate against mail server: {}", e.getMessage(), e);
		} catch(MailSendException e) {
			logger.error("Failed to send message: {}", e.getMessage());
		}
	}
}
