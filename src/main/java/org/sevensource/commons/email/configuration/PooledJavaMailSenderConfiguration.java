package org.sevensource.commons.email.configuration;

import javax.mail.Session;

import org.sevensource.commons.email.javamail.PooledJavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PooledJavaMailSenderConfiguration extends AbstractJavaMailSenderConfiguration {

	@Autowired(required = true)
	PooledJavaMailSenderPoolConfiguration pooledJavaMailSenderPoolConfiguration;

	@Override
	protected PooledJavaMailSenderImpl getJavaMailSenderInstance(Session session) {
		return new PooledJavaMailSenderImpl(session, pooledJavaMailSenderPoolConfiguration);
	}
}
