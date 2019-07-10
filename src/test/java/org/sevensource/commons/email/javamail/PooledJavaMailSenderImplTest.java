//package org.sevensource.commons.email.javamail;
//
//import java.util.Properties;
//
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.Session;
//import javax.mail.internet.AddressException;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//
//import org.junit.Test;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//import org.springframework.util.StopWatch;
//
//public class PooledJavaMailSenderImplTest {
//
//	@Test
//	public void integrationTest() throws AddressException {
//
//		Properties props = new Properties();
//		props.put("mail.smtp.host", "localhost");
//		props.put("mail.smtp.port", "1025");
//		props.put("mail.smtp.class", "com.sun.mail.smtp.SMTPTransport");
//
//		Session session = Session.getInstance(props);
//
////		JavaMailSender mailSender = new PooledJavaMailSenderImpl(session);
//		JavaMailSender mailSender = new JavaMailSenderImpl();
//		((JavaMailSenderImpl)mailSender).setSession(session);
//
//		InternetAddress to = new InternetAddress("recipient@foobar.com");
//		InternetAddress from = new InternetAddress("sender@foorbar.com");
//
//		try {
//			StopWatch stopWatch = new StopWatch();
//			stopWatch.start();
//			
//			for(int i=0; i<10; i++) {
//				StopWatch taskStopWatch = new StopWatch();
//				taskStopWatch.start();
//				
//				MimeMessage message = mailSender.createMimeMessage();
//				message.setFrom(from);
//				message.addRecipient(Message.RecipientType.TO, to);
//				message.setSubject("Hello World");
//				message.setText("Hello, this is a test");
//
//				mailSender.send(message);
//				
//				taskStopWatch.stop();
//				System.out.println(
//						String.format("Message %d was sent successfully - %dms", i, taskStopWatch.getTotalTimeMillis())
//						);				
//			}
//			
//			stopWatch.stop();
//			
//			System.out.println(
//					String.format("TOTAL: %dms", stopWatch.getTotalTimeMillis())
//					);	
//		} catch (MessagingException mex) {
//			mex.printStackTrace();
//		}
//	}
//}
