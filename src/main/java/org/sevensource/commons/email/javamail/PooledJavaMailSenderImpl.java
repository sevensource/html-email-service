package org.sevensource.commons.email.javamail;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.nlab.smtp.pool.SmtpConnectionPool;
import org.nlab.smtp.transport.connection.ClosableSmtpConnection;
import org.nlab.smtp.transport.factory.SmtpConnectionFactories;
import org.nlab.smtp.transport.factory.SmtpConnectionFactory;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;

public class PooledJavaMailSenderImpl extends ConfigurableJavaMailSenderImpl {
	
	private static final String HEADER_MESSAGE_ID = "Message-ID";
	private static final UnsupportedOperationException UNSUPPORTED_EXCEPTION = new UnsupportedOperationException("This method is unsupported");
	
	private final SmtpConnectionFactory factory;
	private final SmtpConnectionPool smtpConnectionPool;
	
	
	public PooledJavaMailSenderImpl(Session session) {
		this(session, new GenericObjectPoolConfig());
	}
	
	public PooledJavaMailSenderImpl(Session session, GenericObjectPoolConfig config) {
		super();
		this.factory = SmtpConnectionFactories.newSmtpFactory(session);
		this.smtpConnectionPool = new SmtpConnectionPool(this.factory, config);
	}
	
	@PreDestroy
	public void destroy() {
		smtpConnectionPool.close();
	}
	
	@Override
	public void setJavaMailProperties(Properties javaMailProperties) {
		throw UNSUPPORTED_EXCEPTION;
	}
	
	@Override
	public synchronized void setSession(Session session) {
		throw UNSUPPORTED_EXCEPTION;
	}
	
	@Override
	public synchronized Session getSession() {
		return factory.getSession();
	}
	
	@Override
	public void setProtocol(String protocol) {
		throw UNSUPPORTED_EXCEPTION;
	}
	
	@Override
	public void setHost(String host) {
		throw UNSUPPORTED_EXCEPTION;
	}
	
	@Override
	public void setPort(int port) {
		throw UNSUPPORTED_EXCEPTION;
	}
	
	@Override
	public void setUsername(String username) {
		throw UNSUPPORTED_EXCEPTION;
	}
	
	@Override
	public void setPassword(String password) {
		throw UNSUPPORTED_EXCEPTION;
	}
	
	@Override
	protected Transport getTransport(Session session) throws NoSuchProviderException {
		throw UNSUPPORTED_EXCEPTION;
	}
	
	@Override
	public void testConnection() throws MessagingException {
		ClosableSmtpConnection transport = connectClosableTransport();
		
		try {
			transport.close();
		} catch(Exception e) {
			// swallow...
		}
	}
	
	protected ClosableSmtpConnection connectClosableTransport() throws MessagingException {
		try {
			return smtpConnectionPool.borrowObject();
		} catch(MessagingException me) {
			throw me;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected Transport connectTransport() throws MessagingException {
		throw UNSUPPORTED_EXCEPTION;
	}
	
	@Override
	protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
		Map<Object, Exception> failedMessages = new LinkedHashMap<>();
		ClosableSmtpConnection transport = null;

		try {
			for (int i = 0; i < mimeMessages.length; i++) {

				// Check transport connection first...
				if (transport == null || !transport.isConnected()) {
					if (transport != null) {
						try {
							transport.close();
						}
						catch (Exception ex) {
							// Ignore - we're reconnecting anyway
						}
						transport = null;
					}
					try {
						transport = connectClosableTransport();
					}
					catch (AuthenticationFailedException ex) {
						throw new MailAuthenticationException(ex);
					}
					catch (Exception ex) {
						// Effectively, all remaining messages failed...
						for (int j = i; j < mimeMessages.length; j++) {
							Object original = (originalMessages != null ? originalMessages[j] : mimeMessages[j]);
							failedMessages.put(original, ex);
						}
						throw new MailSendException("Mail server connection failed", ex, failedMessages);
					}
				}

				// Send message via current transport...
				MimeMessage mimeMessage = mimeMessages[i];
				try {
					if (mimeMessage.getSentDate() == null) {
						mimeMessage.setSentDate(new Date());
					}
					String messageId = mimeMessage.getMessageID();
					mimeMessage.saveChanges();
					if (messageId != null) {
						// Preserve explicitly specified message id...
						mimeMessage.setHeader(HEADER_MESSAGE_ID, messageId);
					}
					Address[] addresses = mimeMessage.getAllRecipients();
					transport.sendMessage(mimeMessage, (addresses != null ? addresses : new Address[0]));
				}
				catch (Exception ex) {
					Object original = (originalMessages != null ? originalMessages[i] : mimeMessage);
					failedMessages.put(original, ex);
				}
			}
		}
		finally {
			try {
				if (transport != null) {
					transport.close();
				}
			}
			catch (Exception ex) {
				if (!failedMessages.isEmpty()) {
					throw new MailSendException("Failed to close server connection after message failures", ex,
							failedMessages);
				}
				else {
					throw new MailSendException("Failed to close server connection after message sending", ex);
				}
			}
		}

		if (!failedMessages.isEmpty()) {
			throw new MailSendException(failedMessages);
		}
	}
	

}
