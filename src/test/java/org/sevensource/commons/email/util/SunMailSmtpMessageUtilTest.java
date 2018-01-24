package org.sevensource.commons.email.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;

import javax.mail.internet.MimeMessage;

import org.junit.Test;

import com.sun.mail.smtp.SMTPMessage;

public class SunMailSmtpMessageUtilTest {

	@Test
	public void isAvailable_should_return_true() {
		assertThat(SunMailSmtpMessageUtil.isAvailable()).isTrue();
	}

	@Test
	public void new_smtpmessage_works() {
		MimeMessage msg = SunMailSmtpMessageUtil.newSMTPMessage(null);
		assertThat(msg).isInstanceOf(SMTPMessage.class);
	}

	@Test
	public void new_smtpmessage2_works() {
		byte[] buf = new byte[] {10};

		MimeMessage msg = SunMailSmtpMessageUtil.newSMTPMessage(null, new ByteArrayInputStream(buf));
		assertThat(msg).isInstanceOf(SMTPMessage.class);
	}
}
