package org.sevensource.commons.email.html.spring;

import javax.inject.Inject;
import javax.mail.Session;

import org.sevensource.commons.email.html.service.NoopEmailServiceMarker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSenderImpl;


@Configuration
@Import({ThymeleafConfiguration.class})
@ComponentScan(basePackageClasses={NoopEmailServiceMarker.class})
public class EmailConfiguration {
	
	@Bean
	@Inject
	public JavaMailSenderImpl mailSender(Session session) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setSession(session);
		return mailSender;
	}
	
}
