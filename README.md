# html-email-service

Library for creating and sending HTML email with Spring.
Uses Thymeleaf for templating.
Contains functionality to transform text to html, as well as html to text.

## Getting started
1. Configure Spring
```java
@Configuration
@Import({EmailThymeleafConfiguration.class, HtmlEmailServiceConfiguration.class})
public class MyConfig {
	@Bean
	public Session session() {
		/* create/acquire a JavaMail session */
	}
}
```

2. use it :)
```java
public class SomeService {
	private final EmailService emailService;
	private final EmailTemplateRendererService emailTemplateRendererService;
	
	@Inject
	public SomeService(EmailService emailService, EmailTemplateRendererService emailTemplateRendererService) {
		this.emailService = emailService;
		this.emailTemplateRendererService = emailTemplateRendererService;
	}
	
	public void someServiceAction() {

		EmailModel model = new EmailModel();
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
		Map<String, Object> renderModel = new HashMap<String,Object>();
		renderModel.put("message", "Check out http://www.github.com");
		html = emailTemplateRendererService.render("someTemplate", emailModel, renderModel, Locale.ENGLISH);
		text = emailTemplateRendererService.htmlToText(html);
		model.setText(text);
		model.setHtml(html);
		
		emailService.sendMail(model);
	}
}
```