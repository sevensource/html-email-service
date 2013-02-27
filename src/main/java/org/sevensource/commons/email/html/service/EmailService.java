package org.sevensource.commons.email.html.service;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.sevensource.commons.email.html.EmailModel;
import org.sevensource.commons.email.html.EmailModel.AttachmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.sun.mail.smtp.SMTPMessage;


@Service
public class EmailService {
	
	private final static Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	private final static String DEFAULT_ENCODING = "UTF-8";
	
	private final JavaMailSender javaMailSender;
	
	@Inject
	public EmailService(JavaMailSender javaMailSender) {
		this.javaMailSender =  javaMailSender;
	}
	
	
	public void sendMail(final EmailModel emailModel) throws MailException {
		
		if (logger.isInfoEnabled()) {
			logger.info("Sending email from {} to {}", emailModel.getFrom(), addressListToString(emailModel.getTo()));
		}
		
		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				
				MimeMessageHelper messageHelper = 
						new MimeMessageHelper(mimeMessage, isMultipart(emailModel), DEFAULT_ENCODING);
				
				if(emailModel.getEnvelopeFrom() != null && mimeMessage instanceof SMTPMessage) {
					((SMTPMessage) mimeMessage).setEnvelopeFrom(emailModel.getEnvelopeFrom());
				}

				//set sender
				messageHelper.setFrom(emailModel.getFrom());
				if(emailModel.getReplyTo() != null) {
					messageHelper.setReplyTo(emailModel.getReplyTo());
				}
				
				// set recipients
				for(InternetAddress to : emailModel.getTo())
					messageHelper.addTo(to);
				for(InternetAddress cc : emailModel.getCc())
					messageHelper.addCc(cc);
				for(InternetAddress bcc : emailModel.getBcc())
					messageHelper.addBcc(bcc);
				
				// set default headers
				messageHelper.setSentDate( emailModel.getDateSent() == null ? new Date() : emailModel.getDateSent().toDate() );
				messageHelper.setSubject(emailModel.getSubject());
		
				// TODO: set custom headers
				// for getCustomHeaders() mimeMessage.addHeader(name, value);
				// for emailModel.getCustomHeaders() mimeMessage.addHeader(name, value);
				
				if(isMultipart(emailModel)) {
					//Multipart mode
					
					messageHelper.setText(emailModel.getText(), emailModel.getHtml());
					
					//TODO: add inline
					

//		            // create wrapper multipart/alternative part
//		            MimeMultipart multiPart = new MimeMultipart("alternative");
//		            mimeMessage.setContent(multiPart);
//		            
//		            // create the plain text
//		            MimeBodyPart plainText = new MimeBodyPart();
//		            plainText.setText(emailModel.getText(), DEFAULT_ENCODING, "plain");
//		            plainText.addHeader("Content-Disposition", "inline");
//		            multiPart.addBodyPart(plainText);
//		            
//		            // create the html part
//		            MimeBodyPart html = new MimeBodyPart();
//		            html.setText(emailModel.getHtml(), DEFAULT_ENCODING, "html");
//		            html.addHeader("Content-Disposition", "inline");
//		            multiPart.addBodyPart(html);
		            
		            
				} else if(! StringUtils.isEmpty(emailModel.getText())) {
					messageHelper.setText(emailModel.getText(), false);
				} else if(! StringUtils.isEmpty(emailModel.getHtml())) {
					messageHelper.setText(emailModel.getHtml(), true);
				} else {
					messageHelper.setText("", false);
				}
				
				for(AttachmentModel attachment : emailModel.getAttachments()) {
					messageHelper.addAttachment(attachment.getFilename(), attachment.getInputStreamSource());
				}
			}
		};
		
		// send the mail using the preparator
		javaMailSender.send(preparator);
		
	}
	
	private boolean isMultipart(EmailModel emailModel) {
		return emailModel.getText() != null && emailModel.getHtml() != null;
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
