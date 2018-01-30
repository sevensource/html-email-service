package org.sevensource.commons.email.service;

import java.util.Date;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.sevensource.commons.email.model.AttachmentModel;
import org.sevensource.commons.email.model.EmailModel;
import org.sevensource.commons.email.util.SunMailSmtpMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


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

		final MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {

				final MimeMessageHelper messageHelper =
						new MimeMessageHelper(mimeMessage, isMultipart(emailModel), emailModel.getEncoding());


				if(emailModel.getEnvelopeFrom() != null) {
					try {
						InternetAddress a = new InternetAddress(emailModel.getEnvelopeFrom());
						mimeMessage.setSender(a);
					} catch(AddressException e) {}

					setEnvelopeFrom(mimeMessage, emailModel.getEnvelopeFrom());
				}


				//set sender
				if(emailModel.getFrom() != null) {
					messageHelper.setFrom(emailModel.getFrom());
				}

				if(emailModel.getReplyTo() != null) {
					messageHelper.setReplyTo(emailModel.getReplyTo());
				}

				// set recipients
				if(emailModel.getTo() != null) {
					for(InternetAddress to : emailModel.getTo()) {
						messageHelper.addTo(to);
					}
				}

				if(emailModel.getCc() != null) {
					for(InternetAddress cc : emailModel.getCc()) {
						messageHelper.addCc(cc);
					}
				}

				if(emailModel.getBcc() != null) {
					for(InternetAddress bcc : emailModel.getBcc()) {
						messageHelper.addBcc(bcc);
					}
				}

				// set default headers
				if(emailModel.getDateSent() == null) {
					messageHelper.setSentDate(new Date());
				} else {
					messageHelper.setSentDate(Date.from(emailModel.getDateSent().toInstant()));
				}

				if(emailModel.getCustomHeaders() != null) {
					for(Entry<String, String> e : emailModel.getCustomHeaders().entrySet()) {
						mimeMessage.addHeader(e.getKey(), MimeUtility.encodeText(e.getValue()));
					}
				}

				if(emailModel.getSubject() != null) {
					messageHelper.setSubject(emailModel.getSubject());
				}

				if(isMultipart(emailModel)) {
					messageHelper.setText(emailModel.getText(), emailModel.getHtml());
				} else if(! StringUtils.isEmpty(emailModel.getText())) {
					messageHelper.setText(emailModel.getText(), false);
				} else if(! StringUtils.isEmpty(emailModel.getHtml())) {
					messageHelper.setText(emailModel.getHtml(), true);
				} else {
					messageHelper.setText("", false);
				}

				if(emailModel.getAttachments() != null) {
					for(AttachmentModel attachment : emailModel.getAttachments()) {
						if(attachment.isInline()) {
							messageHelper.addInline(attachment.getFilename(), attachment.getResource());
						} else {
							messageHelper.addAttachment(attachment.getFilename(), attachment.getResource());
						}
					}
				}

			}
		};

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


	private void setEnvelopeFrom(MimeMessage mimeMessage, String envelopeFrom) {
		if(! SunMailSmtpMessageUtil.isAvailable()) {
			return;
		} else if(envelopeFrom == null || StringUtils.isEmpty(envelopeFrom)) {
			return;
		} else {
			SunMailSmtpMessageUtil.setEnvelopeFromIfPossible(mimeMessage, envelopeFrom);
		}
	}

	private static boolean isMultipart(EmailModel emailModel) {
		return emailModel.getText() != null && emailModel.getHtml() != null;
	}
}
