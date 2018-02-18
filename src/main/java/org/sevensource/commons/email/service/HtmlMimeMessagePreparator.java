package org.sevensource.commons.email.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map.Entry;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.sevensource.commons.email.model.AttachmentModel;
import org.sevensource.commons.email.model.EmailModel;
import org.sevensource.commons.email.util.SunMailSmtpMessageUtil;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.util.StringUtils;

public class HtmlMimeMessagePreparator implements MimeMessagePreparator {

	private final EmailModel emailModel;

	public HtmlMimeMessagePreparator(EmailModel emailModel) {
		this.emailModel = emailModel;
	}

	@Override
	public void prepare(MimeMessage mimeMessage) throws Exception {

		final boolean isMultipart = emailModel.getText() != null && emailModel.getHtml() != null;

		final MimeMessageHelper messageHelper =
				new MimeMessageHelper(mimeMessage, isMultipart, emailModel.getEncoding());

		setSenders(messageHelper, mimeMessage, emailModel);
		setRecipients(messageHelper, emailModel);
		setAdditionalHeaders(messageHelper, mimeMessage, emailModel);

		if(emailModel.getSubject() != null) {
			messageHelper.setSubject(emailModel.getSubject());
		}

		if(isMultipart) {
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


	private static void setSenders(MimeMessageHelper messageHelper, MimeMessage mimeMessage, EmailModel emailModel) throws MessagingException {
		if(emailModel.getEnvelopeFrom() != null) {
			final InternetAddress envelopeFrom = new InternetAddress(emailModel.getEnvelopeFrom());
			mimeMessage.setSender(envelopeFrom);

			if(SunMailSmtpMessageUtil.isAvailable()) {
				SunMailSmtpMessageUtil.setEnvelopeFromIfPossible(mimeMessage, envelopeFrom.getAddress());
			}
		}

		//set sender
		if(emailModel.getFrom() != null) {
			messageHelper.setFrom(emailModel.getFrom());
		}

		if(emailModel.getReplyTo() != null) {
			messageHelper.setReplyTo(emailModel.getReplyTo());
		}
	}

	private static void setRecipients(MimeMessageHelper messageHelper, EmailModel emailModel) throws MessagingException {
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
	}

	private static void setAdditionalHeaders(MimeMessageHelper messageHelper, MimeMessage mimeMessage, EmailModel emailModel) throws MessagingException, UnsupportedEncodingException {
		final Date sentDate = (emailModel.getDateSent() == null) ? new Date() : Date.from(emailModel.getDateSent().toInstant());
		messageHelper.setSentDate(sentDate);

		if(emailModel.getCustomHeaders() != null) {
			for(Entry<String, String> e : emailModel.getCustomHeaders().entrySet()) {
				mimeMessage.addHeader(e.getKey(), MimeUtility.encodeText(e.getValue()));
			}
		}
	}
}
