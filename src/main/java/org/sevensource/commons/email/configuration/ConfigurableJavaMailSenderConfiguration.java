package org.sevensource.commons.email.configuration;

import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.mail.Session;

import org.sevensource.commons.email.util.ConfigurableJavaMailSenderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


@Configuration
public class ConfigurableJavaMailSenderConfiguration {

	private static final String MAIL_SMTP_ALLOW8BITMIME = "mail.smtp.allow8bitmime";


	@Bean
	@Inject
	public JavaMailSender mailSender(Session session) {
		configureSession(session);

		final JavaMailSenderImpl mailSender = new ConfigurableJavaMailSenderImpl(session);
		configureJavaMailSender(mailSender);
		return mailSender;
	}

	/**
	 * override to configure JavaMail {@link Session}
	 *
	 * in order to specify a default, override this method and add
	 *
	 * <code>
	 * if(! session.getProperties().contains("mail.smtp.from")) {
	 *   session.getProperties().put("mail.smtp.from", "bounces@foobar.com");
	 * }
	 * </code>
	 *
	 * @param session the session
	 */
	protected void configureSession(Session session) {
		if(! session.getProperties().contains(MAIL_SMTP_ALLOW8BITMIME)) {
			session.getProperties().put(MAIL_SMTP_ALLOW8BITMIME, "true");
		}
	}

	/**
	 * override to set custom properties on {@link JavaMailSender}
	 * @param mailSender the sender
	 */
	protected void configureJavaMailSender(JavaMailSenderImpl mailSender) {
		mailSender.setDefaultEncoding(StandardCharsets.UTF_8.name());
	}
}
