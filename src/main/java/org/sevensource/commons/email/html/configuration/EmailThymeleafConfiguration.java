package org.sevensource.commons.email.html.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

@Configuration
public class EmailThymeleafConfiguration {
	
	private boolean templateCachable = false;
	private Long templateCacheTTL = TemplateResolver.DEFAULT_CACHE_TTL_MS;
	private String templateMode = "HTML5";
	
	public final static String TEMPLATE_ENGINE_BEAN_NAME = "emailTemplateEngine";
	
	
	@Bean(name=TEMPLATE_ENGINE_BEAN_NAME)
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
		templateResolver.setCacheable( isTemplateCachable() );
		templateResolver.setTemplateMode( getTemplateMode() );
		templateResolver.setCacheTTLMs( getTemplateCacheTTL() );
		templateResolver.setOrder(order);
		return templateResolver;
	}
	
	/**
	 * @see TemplateResolver#setCacheTTLMs(Long)
	 */
	protected Long getTemplateCacheTTL() {
		return templateCacheTTL;
	}
	
	/**
	 * 
	 * @see TemplateResolver#setTemplateMode(String)
	 */
	protected String getTemplateMode() {
		return templateMode;
	}
	
	/**
	 * 
	 * @see TemplateResolver#setCacheable(boolean)
	 */
	public boolean isTemplateCachable() {
		return templateCachable;
	}
}
