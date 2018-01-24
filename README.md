# html-email-service

Wrapper library for creating and sending HTML email with Spring.
 * uses Thymeleaf for templating
 * can transform text to html
 * can transform html to text
 * attachments (inline and attachment) 
 * 

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