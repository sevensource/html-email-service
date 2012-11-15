package org.sevensource.commons.email.html.service;

import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.sevensource.commons.email.html.EmailException;
import org.sevensource.commons.email.html.EmailModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
	
	private final static Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	
	private final JavaMailSender javaMailSender;
	
	public EmailService(JavaMailSender javaMailSender) {
		this.javaMailSender =  javaMailSender;
	}
	
	
	public void sendMail(EmailModel emailModel) throws EmailException {
		
		if (logger.isInfoEnabled()) {
			logger.info("Sending email from {} to {}", emailModel.getFrom(), addressListToString(emailModel.getTo()));
		}
		
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom( emailModel.getFrom() );
			for(InternetAddress to : emailModel.getTo())
				helper.addTo(to);
			for(InternetAddress cc : emailModel.getCc())
				helper.addCc(cc);
			for(InternetAddress bcc : emailModel.getBcc())
				helper.addBcc(bcc);
			
			if(emailModel.getReplyTo() != null)
				helper.setReplyTo(emailModel.getReplyTo());
			
			helper.setSentDate( emailModel.getDateSent() == null ? new Date() : emailModel.getDateSent().toDate());
			
			helper.setSubject( emailModel.getSubject() );
			
			
			if(StringUtils.isEmpty(emailModel.getText())) {
				helper.setText(emailModel.getHtml(), true);
			} else if(StringUtils.isEmpty(emailModel.getHtml())) {
				helper.setText(emailModel.getText());
			} else {
				helper.setText(emailModel.getText(), emailModel.getHtml());
			}
		} catch(MessagingException me) {
			logger.error("Error preparing email", me);
			throw new EmailException(me);
		}
		
		try {
			javaMailSender.send(message);
		} catch(MailException me) {
			final String msg = String.format("Error sending email to %s", addressListToString(emailModel.getTo()));
			logger.error(msg, me);
			throw new EmailException(msg, me);
		}
	}
	
	private String addressListToString(List<InternetAddress> addresses) {
		StringBuilder sb = new StringBuilder(addresses.size() * 40);
		for(InternetAddress address : addresses) {
			if(sb.length() > 0) {
				sb.append("; ");
			}
			
			if(StringUtils.isEmpty(address.getPersonal())) {
				sb.append(String.format("[%s]", address.getAddress()));
			} else {
				sb.append(String.format("[\"%s\" %s]", address.getPersonal(), address.getAddress()));
			}
		}
		
		return sb.toString();
	}
	
}
