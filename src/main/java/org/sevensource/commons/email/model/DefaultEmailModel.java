package org.sevensource.commons.email.model;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

public class DefaultEmailModel implements EmailModel {

	private final static Logger logger = LoggerFactory.getLogger(DefaultEmailModel.class);

	private final static String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();

	private String encoding = DEFAULT_ENCODING;

	private String envelopeFrom = null;

	private InternetAddress from;
	private InternetAddress replyTo;

	private List<InternetAddress> to;
	private List<InternetAddress> cc;
	private List<InternetAddress> bcc;

	private String subject;

	private ZonedDateTime dateSent = ZonedDateTime.now();

	private String text;
	private String html;

	private List<AttachmentModel> attachments;

	private Map<String, String> customHeaders;



	/**
	 * set the SMTP Envelope address of the email
	 *
	 * @param address a valid email address
	 * @throws AddressException in case of an invalid email address
	 */
	public void setEnvelopeFrom(String address) throws AddressException {
		if(StringUtils.isEmpty(address)) {
			this.envelopeFrom = address;
		} else {
			InternetAddress tmp = toInternetAddress(address, null);
			this.envelopeFrom = tmp.getAddress();
		}
	}

	/**
	 * set the From address of the email
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
	 * @param address a valid email address
	 * @param personal the real world name of the sender (can be null)
	 * @throws AddressException in case of an invalid email address
	 */
	public void setReplyTo(String address, String personal) throws AddressException {
		replyTo = toInternetAddress(address, personal);
	}

	/**
	 * adds a recipient (To)
	 *
	 * @param address a valid email address
	 * @param personal the real world name of the sender (can be null)
	 * @throws AddressException in case of an invalid email address
	 */
	public void addTo(String address, String personal) throws AddressException {
		if(to == null) {
			to = new ArrayList<>();
		}
		to.add( toInternetAddress(address, personal) );
	}

	/**
	 * adds a CC recipient
	 *
	 * @param address a valid email address
	 * @param personal the real world name of the sender (can be null)
	 * @throws AddressException in case of an invalid email address
	 */
	public void addCc(String address, String personal) throws AddressException {
		if(cc == null) {
			cc = new ArrayList<>();
		}
		cc.add( toInternetAddress(address, personal) );
	}

	/**
	 * adds a BCC recipient
	 *
	 * @param address a valid email address
	 * @param personal the real world name of the sender (can be null)
	 * @throws AddressException in case of an invalid email address
	 */
	public void addBcc(String address, String personal) throws AddressException {
		if(bcc == null) {
			bcc = new ArrayList<>();
		}
		bcc.add( toInternetAddress(address, personal) );
	}

	/**
	 * adds an attachment
	 *
	 * @param	filename the filename to be displayed in the email.
	 * @param 	source the content of the attachment
	 */
	public void addAttachment(String filename, Resource source) {
		doAddAttachment(filename, source, false);
	}

	/**
	 * adds an inline attachment
	 *
	 * @param	cid the CID reference (must be ending with a known file extension)
	 * @param 	source the content of the attachment
	 */
	public void addAttachmentInline(String cid, Resource source) {
		doAddAttachment(cid, source, true);
	}

	private void doAddAttachment(String id, Resource resource, boolean inline) {
		if(attachments == null) {
			attachments = new ArrayList<>();
		}
		attachments.add( new DefaultAttachmentModel(id, resource, inline) );
	}

	/**
	 * converts an email address and a name to an {@link InternetAddress}
	 *
	 * @param address a valid email address
	 * @param personal the real world name of the sender (can be null)
	 * @return the converted InternetAddress
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
			internetAddress.setPersonal(personal, getEncoding());
		} catch (UnsupportedEncodingException e) {
			logger.warn(String.format("Cannot set sender name [%s] with Charset %s. Just setting the email address", personal, DEFAULT_ENCODING), e);
		}

		return internetAddress;
	}

	public void addCustomHeader(String headerName, String value) {
		if(customHeaders == null) {
			customHeaders = new HashMap<>();
		}
		customHeaders.put(headerName, value);
	}

	@Override
	public Map<String, String> getCustomHeaders() {
		return customHeaders;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public String getEnvelopeFrom() {
		return envelopeFrom;
	}

	@Override
	public InternetAddress getFrom() {
		return from;
	}

	@Override
	public List<InternetAddress> getTo() {
		return to;
	}

	@Override
	public List<InternetAddress> getCc() {
		return cc;
	}

	@Override
	public List<InternetAddress> getBcc() {
		return bcc;
	}

	@Override
	public List<AttachmentModel> getAttachments() {
		return attachments;
	}

	@Override
	public InternetAddress getReplyTo() {
		return replyTo;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	@Override
	public ZonedDateTime getDateSent() {
		return dateSent;
	}

	public void setDateSent(ZonedDateTime dateSent) {
		this.dateSent = dateSent;
	}

	@Override
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
