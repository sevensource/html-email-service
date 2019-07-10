package org.sevensource.commons.email.configuration;

import javax.mail.Session;

import org.sevensource.commons.email.javamail.ConfigurableJavaMailSenderImpl;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ConfigurableJavaMailSenderConfiguration extends AbstractJavaMailSenderConfiguration<ConfigurableJavaMailSenderImpl>{


	@Override
	protected ConfigurableJavaMailSenderImpl getJavaMailSenderInstance(Session session) {
		return new ConfigurableJavaMailSenderImpl(session);
	}
}
