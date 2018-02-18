package org.sevensource.commons.email.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.mock_javamail.Mailbox;
import org.sevensource.commons.email.configuration.ConfigurableJavaMailSenderConfiguration;
import org.sevensource.commons.email.model.DefaultEmailModel;
import org.sevensource.commons.email.template.DefaultTemplateEngineFactory;
import org.sevensource.commons.email.template.TemplateEngineFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class EmailSenderServiceTest {

	@Configuration
	@Import(ConfigurableJavaMailSenderConfiguration.class)
	public static class TestConfig {
		@Bean
		public Session session() {
			Properties props = new Properties();
			Session sess = Session.getDefaultInstance(props);
			sess.setDebug(true);
			return sess;
		}
	}


	@Autowired
	ApplicationContext appctx;

	@Autowired
	JavaMailSender javaMailSender;

	EmailTemplateRendererService renderService;
	EmailSenderService emailSenderService;

	DefaultEmailModel emailModel;
	Map<String, Object> model;

	@Before
	public void before() throws AddressException {
		TemplateEngineFactory factory = new DefaultTemplateEngineFactory(appctx);
		renderService = new EmailTemplateRendererService(factory);

		emailModel = new DefaultEmailModel();
		emailModel.addTo("test@test.com", "Test Recipient");
		model = new HashMap<>();
		model.put("ordernumber", "1234567890");

		emailSenderService = new EmailSenderService(javaMailSender);
		Mailbox.clearAll();
	}

	@Test
	public void appctx_is_not_null() {
		assertThat(appctx).isNotNull();
	}


	@Test
	public void works() throws IOException, MessagingException {

		final String html = renderService.render("test", emailModel, model, Locale.ENGLISH);
		final String text = renderService.htmlToText(html);

		emailModel.setHtml(html);
		emailModel.setText(text);

		emailModel.addAttachment("test.pdf", new ByteArrayResource(new byte[] {0} ));

		emailSenderService.sendMail(emailModel);

		Mailbox mbx = Mailbox.get(emailModel.getTo().get(0));
		assertThat(mbx).isNotNull();
		assertThat(mbx.getNewMessageCount()).isEqualTo(1);
		Message msg = mbx.get(0);
		assertThat(msg).isNotNull();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		msg.writeTo(baos);
		System.out.println(baos.toString());
	}

	@Test
	public void setting_envelope_from_works() throws IOException, MessagingException {
		emailModel.setEnvelopeFrom("test@test.com");
		emailSenderService.sendMail(emailModel);

		Mailbox mbx = Mailbox.get(emailModel.getTo().get(0));
		Message msg = mbx.get(0);
		assertThat(msg.getHeader("Sender")).isEqualTo("test@test.com");
	}
}
