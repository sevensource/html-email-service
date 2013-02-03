package org.sevensource.commons.email.html.spring;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Extension to Springs {@link JavaMailSenderImpl}, which forces the Content-Transfer-Encoding to
 * a specified encoding if possible and therefore overriding JavaMails behaviour of 7bit or base64
 * 
 * @author Philipp
 *
 */
public class ConfigurableJavaMailSenderImpl extends JavaMailSenderImpl {

	private final static String CONTENT_TRANSFER_ENCODING_HEADER = "Content-Transfer-Encoding";
	private final static String CONTENT_TRANSFER_ENCODING_VALUE = "quoted-printable";
	
	private final static Logger logger = LoggerFactory.getLogger(ConfigurableJavaMailSenderImpl.class);
	
	
	/**
	 * we really want text/* parts to be quoted-printable or 8bit.
	 * JavaMail regularly reverts back to 7bit or base64, which increases
	 * spamminess massively.
	 */
	@Override
	protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages)
			throws MailException {
		
		if(logger.isDebugEnabled()) {
			logger.debug("Enabling JavaMail Debug output");
			getSession().setDebug(true);
		}
		
		for (MimeMessage mimeMessage : mimeMessages) {
			try {
				if(mimeMessage.getHeader(CONTENT_TRANSFER_ENCODING_HEADER) == null) {
					mimeMessage.addHeader(CONTENT_TRANSFER_ENCODING_HEADER, CONTENT_TRANSFER_ENCODING_VALUE);
				}
				
				try {
					// if this is a MimeMultipart, iterate through the parts and
					// search for text/plain and text/html parts that do not have a
					// Content-Transfer-Encoding header set.
					if(mimeMessage.getContent() instanceof MimeMultipart) {
						MimeMultipart multipart = (MimeMultipart) mimeMessage.getContent();
						for(int i=0; i < multipart.getCount(); i++) {
							BodyPart part = multipart.getBodyPart(i);
							if("text/plain".equalsIgnoreCase(part.getContentType()) || "text/html".equalsIgnoreCase(part.getContentType())) {
								if(part.getHeader(CONTENT_TRANSFER_ENCODING_HEADER) == null) {
									part.addHeader(CONTENT_TRANSFER_ENCODING_HEADER, CONTENT_TRANSFER_ENCODING_VALUE);
								}
							}
						}
					}
				} catch (IOException e) {
					//ignore
				}
				
			} catch (MessagingException e) {
				throw new MailPreparationException(
						"Cannot add ContentTransferEncoding Header", e);
			}
		}
		super.doSend(mimeMessages, originalMessages);
	}

}
