package org.sevensource.commons.email.template;

import java.nio.charset.StandardCharsets;

import org.springframework.context.ApplicationContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

public class DefaultTemplateEngineFactory implements TemplateEngineFactory {

	private ApplicationContext applicationContext;

	private final static String PREFIX = "/mail/";
	private final static String SUFFIX = ".html";
	private final static String MODE = "HTML";
	private final static String ENCODING = StandardCharsets.UTF_8.name();
	private final static boolean CACHEABLE = true;



	public DefaultTemplateEngineFactory(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public TemplateEngine getTemplateEngine() {
        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(defaultTemplateResolver());
        return templateEngine;
	}


	protected SpringResourceTemplateResolver defaultTemplateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(this.applicationContext);
		resolver.setPrefix(PREFIX);
		resolver.setSuffix(SUFFIX);
		resolver.setTemplateMode(MODE);
		resolver.setCharacterEncoding(ENCODING);
		resolver.setCacheable(CACHEABLE);
		resolver.setOrder(1);
		return resolver;
	}
}
