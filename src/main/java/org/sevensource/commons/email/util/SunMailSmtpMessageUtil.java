package org.sevensource.commons.email.util;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class SunMailSmtpMessageUtil {

	private static final Logger logger = LoggerFactory.getLogger(SunMailSmtpMessageUtil.class);


	private final static String SUN_MAIL_SMTPMESSAGE = "com.sun.mail.smtp.SMTPMessage";

	private static final boolean smtpMessageAvailable;
	private static final Class<?> smtpMessageClass;
	private static final Constructor<?> simpleConstructor;
	private static final Constructor<?> inputStreamConstructor;

	static {
		Class<?> clazz = null;
		try {
			clazz = ClassUtils.forName(SUN_MAIL_SMTPMESSAGE, SunMailSmtpMessageUtil.class.getClassLoader());
		} catch (ClassNotFoundException e) {}

		smtpMessageClass = clazz;
		smtpMessageAvailable = smtpMessageClass != null;

		if(smtpMessageAvailable) {
			simpleConstructor = ClassUtils.getConstructorIfAvailable(smtpMessageClass, Session.class);
			inputStreamConstructor = ClassUtils.getConstructorIfAvailable(smtpMessageClass, Session.class, InputStream.class);

			if(simpleConstructor == null || inputStreamConstructor == null) {
				final String msg = String.format("%s is missing expected constructors", SUN_MAIL_SMTPMESSAGE);
				logger.error(msg);
				throw new RuntimeException(msg);
			}

		} else {
			simpleConstructor = inputStreamConstructor = null;
		}
	}


	public static boolean isAvailable() {
		return smtpMessageAvailable;
	}

	private static void assertAvailable() {
		if(! smtpMessageAvailable) {
			final String msg = String.format("%s cannot be found on classpath", SUN_MAIL_SMTPMESSAGE);
			throw new IllegalStateException(msg);
		}
	}

	public static MimeMessage newSMTPMessage(Session session) {
		assertAvailable();
		return (MimeMessage) BeanUtils.instantiateClass(simpleConstructor, session);
	}

	public static MimeMessage newSMTPMessage(Session session, InputStream contentStream) {
		assertAvailable();
		return (MimeMessage) BeanUtils.instantiateClass(inputStreamConstructor, session, contentStream);
	}

	public static void setEnvelopeFromIfPossible(MimeMessage mimeMessage, String envelopeFrom) {
		if(! isAvailable()) {
			return;
		}

		final Class<?> mimeMessageClazz = mimeMessage.getClass();
		if(ClassUtils.isAssignable(smtpMessageClass, mimeMessageClazz)) {
			final Method setEnvelopeFromMethod = ReflectionUtils.findMethod(mimeMessageClazz, "setEnvelopeFrom");
			if(setEnvelopeFromMethod != null) {
				ReflectionUtils.invokeMethod(setEnvelopeFromMethod, mimeMessage, envelopeFrom);
			}
		}
	}
}
