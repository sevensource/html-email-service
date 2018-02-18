package org.sevensource.commons.email.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

public class ConfigurableJavaMailSenderImplTest {

	@Test
	public void simple_works() {
		Session sess = Session.getDefaultInstance(new Properties());
		ConfigurableJavaMailSenderImpl sender = new ConfigurableJavaMailSenderImpl(sess);
		MimeMessage msg = sender.createMimeMessage();
		assertThat(msg).isNotNull();
	}

	@Test
	public void with_inputstream_works() {
		Session sess = Session.getDefaultInstance(new Properties());
		ConfigurableJavaMailSenderImpl sender = new ConfigurableJavaMailSenderImpl(sess);
		ByteArrayInputStream is = new ByteArrayInputStream(new String().getBytes());
		MimeMessage msg = sender.createMimeMessage(is);
		assertThat(msg).isNotNull();
	}
}
