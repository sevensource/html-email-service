package org.sevensource.commons.email.html.configuration;

import javax.inject.Inject;
import javax.mail.Session;

import org.sevensource.commons.email.html.service.NoopEmailServiceMarker;
import org.sevensource.commons.email.html.spring.ConfigurableJavaMailSenderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


@Configuration
@Import({ThymeleafConfiguration.class})
@ComponentScan(basePackageClasses={NoopEmailServiceMarker.class})
public class EmailConfiguration {
	
	@Bean
	@Inject
	public JavaMailSenderImpl mailSender(Session session) {
		
		//add a callback to configure container provided mail sessions
		configureSession(session);
		
		JavaMailSenderImpl mailSender = new ConfigurableJavaMailSenderImpl();
		mailSender.setSession(session);
		
		configureJavaMailSender(mailSender);
		
		return mailSender;
	}
	
	
	/**
	 * override to configure JavaMail {@link Session}
	 * @param session
	 */
	protected void configureSession(Session session) {
		
		//session.setDebug(false);
		
		if(! session.getProperties().contains("mail.smtp.allow8bitmime")) {
			session.getProperties().put("mail.smtp.allow8bitmime", "true");
		}
		
//		if(! session.getProperties().contains("mail.smtp.from")) {
//			session.getProperties().put("mail.smtp.from", smtp envelope sender);
//		}
	}
	
	
	/**
	 * override to set custom properties on {@link JavaMailSender}
	 * @param mailSender
	 */
	protected void configureJavaMailSender(JavaMailSenderImpl mailSender) {
		mailSender.setDefaultEncoding("UTF-8");
	}
	
}
