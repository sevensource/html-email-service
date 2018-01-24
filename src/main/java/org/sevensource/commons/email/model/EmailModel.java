package org.sevensource.commons.email.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

public interface EmailModel {
	public ZonedDateTime getDateSent();
	public String getEncoding();

	public Map<String, String> getCustomHeaders();

	public String getEnvelopeFrom();
	public InternetAddress getFrom();
	public List<InternetAddress> getTo();
	public List<InternetAddress> getCc();
	public List<InternetAddress> getBcc();
	public InternetAddress getReplyTo();


	public String getSubject();
	public String getText();
	public String getHtml();
	public List<AttachmentModel> getAttachments();
}
