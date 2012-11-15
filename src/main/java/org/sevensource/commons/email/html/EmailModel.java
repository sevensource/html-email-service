package org.sevensource.commons.email.html;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailModel {
	
	private final static Logger logger = LoggerFactory.getLogger(EmailModel.class);
	
	private final static String SENDER_ENCODING = "UTF-8";
	
	
	private InternetAddress from;
	private List<InternetAddress> to;
	private List<InternetAddress> cc;
	private List<InternetAddress> bcc;
	private InternetAddress replyTo;
	
	private String subject;
	
	private DateTime dateSent = new DateTime();
	
	private String text;
	private String html;
	
	
	/**
	 * set the sender of the email
	 * 
	 * @param address a valid email address
	 * @param personal the real world name of the sender (can be null)
	 * @throws AddressException in case of an invalid email address
	 */
	public void setFrom(String address, String personal) throws AddressException {
		from = toInternetAddress(address, personal);
	}
	
	/**
	 * sets the reply to address
	 * 
	 * @param address
	 * @param personal
	 * @throws AddressException
	 */
	public void setReplyTo(String address, String personal) throws AddressException {
		replyTo = toInternetAddress(address, personal);
	}
	
	/**
	 * adds a recipient (To)
	 * 
	 * @param address
	 * @param personal
	 * @throws AddressException
	 */
	public void addTo(String address, String personal) throws AddressException {
		if(to == null) {
			to = new ArrayList<InternetAddress>();
		}
		to.add( toInternetAddress(address, personal) );
	}
	
	/**
	 * adds a CC recipient
	 * 
	 * @param address
	 * @param personal
	 * @throws AddressException
	 */
	public void addCc(String address, String personal) throws AddressException {
		if(cc == null) {
			cc = new ArrayList<InternetAddress>();
		}
		cc.add( toInternetAddress(address, personal) );
	}
	
	/**
	 * adds a BCC recipient
	 * @param address
	 * @param personal
	 * @throws AddressException
	 */
	public void addBcc(String address, String personal) throws AddressException {
		if(bcc == null) {
			bcc = new ArrayList<InternetAddress>();
		}
		bcc.add( toInternetAddress(address, personal) );
	}
	
	/**
	 * converts an email address and a name to an {@link InternetAddress}
	 * @param address a valid email address
	 * @param personal the real world name of the sender (can be null)
	 * @return
	 * @throws AddressException in case of an invalid email address
	 */
	private InternetAddress toInternetAddress(String address, String personal) throws AddressException {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Creating InternetAddress from address [{}] and personal [{}]", address, personal);
		}
		
		InternetAddress internetAddress;
		
		try {
			internetAddress = new InternetAddress(address, true);
		} catch (AddressException e) {
			logger.error(String.format("Cannot parse email address [%s]", address), e);
			throw e;
		}
		
		try {
			internetAddress.setPersonal(personal, SENDER_ENCODING);
		} catch (UnsupportedEncodingException e) {
			logger.warn(String.format("Cannot set sender name [%s] with Charset %s. Just setting the email address", personal, SENDER_ENCODING), e);
		}
		
		return internetAddress;
	}
	
	/**
	 * sets the subject of the email
	 * 
	 * @param subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public InternetAddress getFrom() {
		return from;
	}

	public List<InternetAddress> getTo() {
		return to == null ? Collections.<InternetAddress>emptyList() : to;
	}

	public List<InternetAddress> getCc() {
		return cc == null ? Collections.<InternetAddress>emptyList() : cc;
	}

	public List<InternetAddress> getBcc() {
		return bcc == null ? Collections.<InternetAddress>emptyList() : bcc;
	}

	public InternetAddress getReplyTo() {
		return replyTo;
	}

	public String getSubject() {
		return subject;
	}

	public String getText() {
		return text;
	}

	public String getHtml() {
		return html;
	}
	
	public DateTime getDateSent() {
		return dateSent;
	}
	
	public void setDateSent(DateTime dateSent) {
		this.dateSent = dateSent;
	}
}
