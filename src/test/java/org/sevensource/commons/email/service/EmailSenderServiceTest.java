package org.sevensource.commons.email.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

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
	public void text_only_works() throws IOException, MessagingException {
		emailModel.setHtml("");
		emailModel.setText("Hello World");

		emailSenderService.sendMail(emailModel);

		Mailbox mbx = Mailbox.get(emailModel.getTo().get(0));
		Message msg = mbx.get(0);

		assertThat(msg.getContentType()).contains("text/plain");
		assertThat(msg.getContentType()).contains("UTF-8");

		Object content = msg.getContent();
		assertThat(content.getClass()).isEqualTo(String.class);
		assertThat(content).isEqualTo("Hello World");
	}

	@Test
	public void html_only_works() throws IOException, MessagingException {
		final String body = "<html><body>Hello World</body></html>";
		emailModel.setHtml(body);
		emailModel.setText("");

		emailSenderService.sendMail(emailModel);

		Mailbox mbx = Mailbox.get(emailModel.getTo().get(0));
		Message msg = mbx.get(0);

		assertThat(msg.getContentType()).isEqualTo("text/html;charset=UTF-8");
		Object content = msg.getContent();
		assertThat(content.getClass()).isEqualTo(String.class);
		assertThat(content).isEqualTo(body);
	}

	@Test
	public void multipart_alternative_works() throws IOException, MessagingException {
		final String bodyTxt = "Hello World";
		final String bodyHtml = "<html><body>Hello World</body></html>";
		emailModel.setText(bodyTxt);
		emailModel.setHtml(bodyHtml);


		emailSenderService.sendMail(emailModel);

		Mailbox mbx = Mailbox.get(emailModel.getTo().get(0));
		Message msg = mbx.get(0);

		assertThat(msg.getContentType()).contains("multipart");
		Object content = msg.getContent();
		assertThat(content.getClass()).isEqualTo(MimeMultipart.class);

		MimeMultipart contentMixed = (MimeMultipart) content;
		assertThat(contentMixed.getContentType()).contains("multipart/mixed");
		assertThat(contentMixed.getCount()).isEqualTo(1);

		MimeMultipart contentRelated = (MimeMultipart) contentMixed.getBodyPart(0).getContent();
		assertThat(contentRelated.getContentType()).contains("multipart/related");
		assertThat(contentRelated.getCount()).isEqualTo(1);

		MimeMultipart contentAlternative = (MimeMultipart) contentRelated.getBodyPart(0).getContent();
		assertThat(contentAlternative.getContentType()).contains("multipart/alternative");
		assertThat(contentAlternative.getCount()).isEqualTo(2);

		String contentTxt = (String) contentAlternative.getBodyPart(0).getContent();
		assertThat(contentTxt).isEqualTo(bodyTxt);

		String contentHtml = (String) contentAlternative.getBodyPart(1).getContent();
		assertThat(contentHtml).isEqualTo(bodyHtml);
	}

	@Test
	public void html_inline_attachment_works() throws IOException, MessagingException {
		final String body = "<html><body><img src='cid:my-image.jpg'></body></html>";
		emailModel.setHtml(body);
		emailModel.addAttachmentInline("my-image.jpg", new ByteArrayResource(new byte[] {0} ));

		emailSenderService.sendMail(emailModel);

		Mailbox mbx = Mailbox.get(emailModel.getTo().get(0));
		Message msg = mbx.get(0);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		msg.writeTo(baos);
		System.out.println(baos.toString());


		assertThat(msg.getContentType()).contains("multipart/mixed");
		Object content = msg.getContent();
		assertThat(content.getClass()).isEqualTo(MimeMultipart.class);

		MimeMultipart contentMixed = (MimeMultipart) content;
		assertThat(contentMixed.getCount()).isEqualTo(1);

		MimeMultipart contentRelated = (MimeMultipart) contentMixed.getBodyPart(0).getContent();
		assertThat(contentRelated.getContentType()).contains("multipart/related");
		assertThat(contentRelated.getCount()).isEqualTo(2);

		String contentHtml = (String) contentRelated.getBodyPart(0).getContent();
		assertThat(contentHtml).isEqualTo(body);



		BodyPart attachment = contentRelated.getBodyPart(1);
		assertThat(attachment.getContentType()).contains("image/jpeg");
		assertThat(attachment.getHeader("Content-ID")[0]).isEqualTo("<my-image.jpg>");
	}

	@Test
	public void setting_envelope_from_works() throws IOException, MessagingException {
		emailModel.setEnvelopeFrom("test@test.com");
		emailSenderService.sendMail(emailModel);

		Mailbox mbx = Mailbox.get(emailModel.getTo().get(0));
		Message msg = mbx.get(0);
		assertThat(msg.getHeader("Sender")).containsExactly("test@test.com");
	}

	@Test
	public void setting_from_works() throws IOException, MessagingException {
		emailModel.setFrom("test@test.com", "");
		emailSenderService.sendMail(emailModel);

		Mailbox mbx = Mailbox.get(emailModel.getTo().get(0));
		Message msg = mbx.get(0);
		assertThat(msg.getFrom()).hasSize(1);
		assertThat(msg.getFrom()[0]).isEqualTo(new InternetAddress("test@test.com", ""));
	}

	@Test
	public void setting_to_works() throws IOException, MessagingException {
		emailModel.getTo().clear();
		emailModel.addTo("test@test.com", "Test");
		emailSenderService.sendMail(emailModel);

		Mailbox mbx = Mailbox.get(emailModel.getTo().get(0));
		Message msg = mbx.get(0);
		assertThat(msg.getRecipients(RecipientType.TO)).hasSize(1);
		assertThat(msg.getRecipients(RecipientType.CC)).isNull();
		assertThat(msg.getRecipients(RecipientType.BCC)).isNull();
		assertThat(msg.getRecipients(RecipientType.TO)[0]).isEqualTo(new InternetAddress("test@test.com", "Test"));
	}

	@Test
	public void setting_cc_works() throws IOException, MessagingException {
		emailModel.getTo().clear();
		emailModel.addTo("test@test.com", "Test");
		emailModel.addCc("test2@test.com", "Test2");
		emailSenderService.sendMail(emailModel);

		Mailbox mbx = Mailbox.get(emailModel.getTo().get(0));
		Message msg = mbx.get(0);
		assertThat(msg.getRecipients(RecipientType.TO)).hasSize(1);
		assertThat(msg.getRecipients(RecipientType.CC)).hasSize(1);
		assertThat(msg.getRecipients(RecipientType.BCC)).isNull();
		assertThat(msg.getRecipients(RecipientType.TO)[0]).isEqualTo(new InternetAddress("test@test.com", "Test"));
		assertThat(msg.getRecipients(RecipientType.CC)[0]).isEqualTo(new InternetAddress("test2@test.com", "Test2"));

		mbx = Mailbox.get(emailModel.getCc().get(0));
		msg = mbx.get(0);
		assertThat(msg.getRecipients(RecipientType.TO)).hasSize(1);
		assertThat(msg.getRecipients(RecipientType.CC)).hasSize(1);
		assertThat(msg.getRecipients(RecipientType.BCC)).isNull();
		assertThat(msg.getRecipients(RecipientType.TO)[0]).isEqualTo(new InternetAddress("test@test.com", "Test"));
		assertThat(msg.getRecipients(RecipientType.CC)[0]).isEqualTo(new InternetAddress("test2@test.com", "Test2"));
	}

	@Test
	public void setting_bcc_works() throws IOException, MessagingException {
		emailModel.getTo().clear();
		emailModel.addTo("test@test.com", "Test");
		emailModel.addBcc("test2@test.com", "Test2");
		emailSenderService.sendMail(emailModel);

		Mailbox mbx = Mailbox.get(emailModel.getTo().get(0));
		Message msg = mbx.get(0);
		assertThat(msg.getRecipients(RecipientType.TO)).hasSize(1);
		assertThat(msg.getRecipients(RecipientType.CC)).isNull();
		assertThat(msg.getRecipients(RecipientType.BCC)).hasSize(1);
		assertThat(msg.getRecipients(RecipientType.TO)[0]).isEqualTo(new InternetAddress("test@test.com", "Test"));
		assertThat(msg.getRecipients(RecipientType.BCC)[0]).isEqualTo(new InternetAddress("test2@test.com", "Test2"));

		mbx = Mailbox.get(emailModel.getBcc().get(0));
		msg = mbx.get(0);
		assertThat(msg.getRecipients(RecipientType.TO)).hasSize(1);
		assertThat(msg.getRecipients(RecipientType.CC)).isNull();
		assertThat(msg.getRecipients(RecipientType.BCC)).hasSize(1);
		assertThat(msg.getRecipients(RecipientType.TO)[0]).isEqualTo(new InternetAddress("test@test.com", "Test"));
		assertThat(msg.getRecipients(RecipientType.BCC)[0]).isEqualTo(new InternetAddress("test2@test.com", "Test2"));
	}

	@Test
	public void setting_reply_to_works() throws IOException, MessagingException {
		emailModel.setReplyTo("no-reply@test.com", "NoReply");
		emailSenderService.sendMail(emailModel);

		Mailbox mbx = Mailbox.get(emailModel.getTo().get(0));
		Message msg = mbx.get(0);
		assertThat(msg.getRecipients(RecipientType.TO)).hasSize(1);
		assertThat(msg.getRecipients(RecipientType.TO)[0]).isEqualTo(new InternetAddress("test@test.com", "Test"));

		assertThat(msg.getReplyTo()[0]).isEqualTo(new InternetAddress("no-reply@test.com", "NoReply"));
	}

	@Test
	public void setting_subject_works() throws IOException, MessagingException {
		emailModel.setSubject("Hello World");
		emailSenderService.sendMail(emailModel);

		Mailbox mbx = Mailbox.get(emailModel.getTo().get(0));
		Message msg = mbx.get(0);
		assertThat(msg.getSubject()).isEqualTo("Hello World");
	}

	@Test
	public void setting_headers_works() throws IOException, MessagingException, ParseException {
		emailModel.addCustomHeader("X-Test", "Hello World");

		ZonedDateTime dateSent = ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 0, ZoneId.of("America/New_York"));

		emailModel.setDateSent(dateSent);
		emailSenderService.sendMail(emailModel);

		Mailbox mbx = Mailbox.get(emailModel.getTo().get(0));
		Message msg = mbx.get(0);

		assertThat(msg.getHeader("X-Test")).containsExactly("Hello World");


		assertThat(msg.getSentDate()).isNotNull();
		Instant dateSentHeader = Instant.ofEpochMilli(msg.getSentDate().getTime());
		assertThat(dateSentHeader).isEqualTo(dateSent.toInstant());
	}
}
