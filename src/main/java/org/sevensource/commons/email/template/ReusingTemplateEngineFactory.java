package org.sevensource.commons.email.template;

import org.thymeleaf.TemplateEngine;

public class ReusingTemplateEngineFactory implements TemplateEngineFactory {

	private final TemplateEngine templateEngine;

	public ReusingTemplateEngineFactory(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	@Override
	public TemplateEngine getTemplateEngine() {
		return templateEngine;
	}
}
