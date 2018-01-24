[![GitHub Tag](https://img.shields.io/github/tag/sevensource/html-email-service.svg?maxAge=3600)](https://github.com/sevensource/html-email-service/tags)
[![Maven Central](https://img.shields.io/maven-central/v/org.sevensource.commons.mail/html-email-service.svg?maxAge=3600)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.sevensource.commons.mail%22%20AND%20a%3A%22html-email-service%22)
[![License](https://img.shields.io/github/license/sevensource/html-email-service.svg)](https://github.com/sevensource/html-email-service/blob/master/LICENSE)

# html-email-service

Wrapper library for creating and sending HTML email with Spring.
=======

 * uses Thymeleaf for templating
 * can transform text to html
 * can transform html to text
 * attachments (inline and attachment)

## Example
```java

@Configuration
public class EmailServiceConfiguration {
	@Autowired
	ApplicationContext applicationContext;
	
	// also see ConfigurableJavaMailSenderConfiguration
	@Autowired 
	JavaMailSender javaMailSender;
	
	@Bean
	public EmailTemplateRendererService emailTemplateRenderService() {
		return new EmailTemplateRendererService(factory);
	}
	
	@Bean
	public EmailSenderService emailSenderService() {
		TemplateEngineFactory factory = new DefaultTemplateEngineFactory(applicationContext);
		return new EmailTemplateRendererService(factory);
	}
}

public void sendmail() {
	DefaultEmailModel model = new DefaultEmailModel();
	model.setFrom("foo@bar.com", "Foobar");
	model.addTo("far@boo.com", "Farboo");
	model.setSubject("Let's get things started");
	
	//create a simple HTML representation from plain text
	String text = "Make sure to check out http://www.github.com"; 
	String html = emailTemplateRendererService.textToHtml(text);
	model.setText(text);
	model.setHtml(html);
	
	emailService.sendMail(model);
	
	// ...or render a Thymeleaf template into HTML and automatically
	// provide a text only fallback version
	Map<String, Object> renderModel = new HashMap<>();
	renderModel.put("message", "Check out http://www.github.com");
	html = emailTemplateRendererService.render("someTemplate", emailModel, renderModel, Locale.ENGLISH);
	text = emailTemplateRendererService.htmlToText(html);
	model.setText(text);
	model.setHtml(html);
	
	emailService.sendMail(model);
}
```