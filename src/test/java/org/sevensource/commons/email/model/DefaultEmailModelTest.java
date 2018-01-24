package org.sevensource.commons.email.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.ZonedDateTime;

import javax.mail.internet.AddressException;

import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;

public class DefaultEmailModelTest {

	@Test
	public void works() throws AddressException, IOException {
		DefaultEmailModel model = new DefaultEmailModel();

		assertThat(model.getDateSent()).isNotNull();
		ZonedDateTime now = ZonedDateTime.now();
		model.setDateSent(now);
		assertThat(model.getDateSent()).isEqualTo(now);

		assertThat(model.getFrom()).isNull();
		model.setFrom("test@test.com", "Testing");
		assertThat(model.getFrom()).isNotNull();
		assertThat(model.getFrom().getAddress()).isEqualTo("test@test.com");
		assertThat(model.getFrom().getPersonal()).isEqualTo("Testing");

		assertThat(model.getReplyTo()).isNull();
		model.setReplyTo("test@test.com", "Testing");
		assertThat(model.getReplyTo()).isNotNull();
		assertThat(model.getReplyTo().getAddress()).isEqualTo("test@test.com");
		assertThat(model.getReplyTo().getPersonal()).isEqualTo("Testing");

		assertThat(model.getTo()).isNull();
		model.addTo("test@test.com", "Testing");
		assertThat(model.getTo()).hasSize(1);
		assertThat(model.getTo().get(0).getAddress()).isEqualTo("test@test.com");
		assertThat(model.getTo().get(0).getPersonal()).isEqualTo("Testing");

		assertThat(model.getCc()).isNull();
		model.addCc("test@test.com", "Testing");
		assertThat(model.getCc()).hasSize(1);
		assertThat(model.getCc().get(0).getAddress()).isEqualTo("test@test.com");
		assertThat(model.getCc().get(0).getPersonal()).isEqualTo("Testing");

		assertThat(model.getBcc()).isNull();
		model.addBcc("test@test.com", "Testing");
		assertThat(model.getBcc()).hasSize(1);
		assertThat(model.getBcc().get(0).getAddress()).isEqualTo("test@test.com");
		assertThat(model.getBcc().get(0).getPersonal()).isEqualTo("Testing");

		assertThat(model.getSubject()).isNull();
		model.setSubject("Testing");
		assertThat(model.getSubject()).isEqualTo("Testing");

		assertThat(model.getText()).isNull();
		model.setText("Testing");
		assertThat(model.getText()).isEqualTo("Testing");

		assertThat(model.getHtml()).isNull();
		model.setHtml("Testing");
		assertThat(model.getHtml()).isEqualTo("Testing");

		assertThat(model.getAttachments()).isNull();
		final byte[] c = new byte[] {9};
		model.addAttachment("test.pdf", new ByteArrayResource(c));
		assertThat(model.getAttachments()).hasSize(1);
		assertThat(model.getAttachments().get(0).getFilename()).isEqualTo("test.pdf");
		assertThat(model.getAttachments().get(0).getResource().contentLength()).isEqualTo(1);
		assertThat(model.getAttachments().get(0).isInline()).isFalse();

		model.addAttachmentInline("logo.jpg", new ByteArrayResource(c));
		assertThat(model.getAttachments()).hasSize(2);
		assertThat(model.getAttachments().get(1).getFilename()).isEqualTo("logo.jpg");
		assertThat(model.getAttachments().get(1).getResource().contentLength()).isEqualTo(1);
		assertThat(model.getAttachments().get(1).isInline()).isTrue();
	}

	@Test(expected=AddressException.class)
	public void invalid_from_throws_exception() throws AddressException {
		DefaultEmailModel model = new DefaultEmailModel();
		model.setFrom("invalid", null);
	}

	@Test(expected=AddressException.class)
	public void invalid_to_throws_exception() throws AddressException {
		DefaultEmailModel model = new DefaultEmailModel();
		model.addTo("invalid", null);
	}

	@Test(expected=AddressException.class)
	public void invalid_cc_throws_exception() throws AddressException {
		DefaultEmailModel model = new DefaultEmailModel();
		model.addCc("invalid", null);
	}

	@Test(expected=AddressException.class)
	public void invalid_bcc_throws_exception() throws AddressException {
		DefaultEmailModel model = new DefaultEmailModel();
		model.addBcc("invalid", null);
	}

	@Test(expected=AddressException.class)
	public void invalid_replyto_throws_exception() throws AddressException {
		DefaultEmailModel model = new DefaultEmailModel();
		model.setReplyTo("invalid", null);
	}

	@Test(expected=AddressException.class)
	public void invalid_envelopeFrom_throws_exception() throws AddressException {
		DefaultEmailModel model = new DefaultEmailModel();
		model.setEnvelopeFrom("invalid");
	}

	@Test()
	public void empty_envelopeFrom_has_no_exception() throws AddressException {
		DefaultEmailModel model = new DefaultEmailModel();
		model.setEnvelopeFrom("");
	}
}
