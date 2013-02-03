package org.sevensource.commons.email.html.service;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.sevensource.commons.email.html.EmailException;
import org.sevensource.commons.email.html.EmailModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
	
	private final static Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	private final static String DEFAULT_ENCODING = "UTF-8";
	
	private final JavaMailSender javaMailSender;
	
	@Inject
	public EmailService(JavaMailSender javaMailSender) {
		this.javaMailSender =  javaMailSender;
	}
	
	
	public void sendMail(final EmailModel emailModel) throws EmailException {
		
		if (logger.isInfoEnabled()) {
			logger.info("Sending email from {} to {}", emailModel.getFrom(), addressListToString(emailModel.getTo()));
		}
		
		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				//set sender
				mimeMessage.setFrom(emailModel.getFrom());
				if(emailModel.getReplyTo() != null)
					mimeMessage.setReplyTo(new InternetAddress[] { emailModel.getReplyTo() } );
				
				// set recipients
				for(InternetAddress to : emailModel.getTo())
					mimeMessage.addRecipient(RecipientType.TO, to);
				for(InternetAddress cc : emailModel.getCc())
					mimeMessage.addRecipient(RecipientType.CC, cc);
				for(InternetAddress bcc : emailModel.getBcc())
					mimeMessage.addRecipient(RecipientType.BCC, bcc);
				
				// set default headers
				mimeMessage.setSentDate( emailModel.getDateSent() == null ? new Date() : emailModel.getDateSent().toDate());
				mimeMessage.setSubject( emailModel.getSubject(), DEFAULT_ENCODING );
		
				// TODO: set custom headers
				//for getCustomHeaders() mimeMessage.addHeader(name, value);
				//for emailModel.getCustomHeaders() mimeMessage.addHeader(name, value);
				
				if(emailModel.getText() != null && emailModel.getHtml() != null) {
					//Multipart mode
					
		            // create wrapper multipart/alternative part
		            MimeMultipart multiPart = new MimeMultipart("alternative");
		            mimeMessage.setContent(multiPart);
		            
		            // create the plain text
		            MimeBodyPart plainText = new MimeBodyPart();
		            plainText.setText(emailModel.getText(), DEFAULT_ENCODING, "plain");
		            plainText.addHeader("Content-Disposition", "inline");
		            multiPart.addBodyPart(plainText);
		            
		            // create the html part
		            MimeBodyPart html = new MimeBodyPart();
		            html.setText(emailModel.getHtml(), DEFAULT_ENCODING, "html");
		            html.addHeader("Content-Disposition", "inline");
		            multiPart.addBodyPart(html);
		            
				} else if(! StringUtils.isEmpty(emailModel.getText())) {
					mimeMessage.setText(emailModel.getText(), DEFAULT_ENCODING, "plain");
				} else if(! StringUtils.isEmpty(emailModel.getHtml())) {
					mimeMessage.setText(emailModel.getHtml(), DEFAULT_ENCODING, "html");
				} else {
					mimeMessage.setText("");
				}
			}
		};
		
		// send the mail using the preparator
		javaMailSender.send(preparator);
		
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
