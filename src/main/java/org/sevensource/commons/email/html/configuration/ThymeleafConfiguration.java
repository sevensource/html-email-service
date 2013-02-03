package org.sevensource.commons.email.html.configuration;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

@Configuration
public class ThymeleafConfiguration {
	
	private boolean templateCachable = false;
	private long templateCacheTTL = 1;
	private String templateMode = "HTML5";
	
	
	@Bean
	public TemplateEngine templateEngine() {
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolvers( getTemplateResolver() );
		return templateEngine;
	}
	
	protected Set<TemplateResolver> getTemplateResolver() {
		Set<TemplateResolver> resolvers = new HashSet<TemplateResolver>();
		
		int counter = Integer.MAX_VALUE - 10;
		
		resolvers.add( configureTemplateResolver( new ClassLoaderTemplateResolver(), counter++ ));
		resolvers.add( configureTemplateResolver( new FileTemplateResolver(), counter++ ));
		resolvers.add( configureTemplateResolver( new UrlTemplateResolver(), counter++ ));
		
		return resolvers;
	}
	
	private TemplateResolver configureTemplateResolver(TemplateResolver templateResolver, int order) {
		templateResolver.setCacheable(templateCachable);
		templateResolver.setTemplateMode(templateMode);
		templateResolver.setCacheTTLMs(templateCacheTTL);
		templateResolver.setOrder(order);
		return templateResolver;
	}
}
