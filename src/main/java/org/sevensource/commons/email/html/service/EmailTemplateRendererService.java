package org.sevensource.commons.email.html.service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import net.htmlparser.jericho.Source;

import org.apache.commons.lang3.StringEscapeUtils;
import org.sevensource.commons.email.html.EmailModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.VariablesMap;


@Service
public class EmailTemplateRendererService {

	private final static Logger logger = LoggerFactory.getLogger(EmailTemplateRendererService.class);
	
	private final static String THYMELEAF_EMAIL_MODEL_NAME = "emailModel";
	private final static int TEMPLATE_BUFFER_INITIAL_SIZE = 4 * 1024; //4kb
	
	private final TemplateEngine templateEngine;
	
	private final Pattern URL_TO_HTML_HREF_PATTERN = Pattern.compile("(?i)\\bhttps?://([a-z0-9_-]+\\.)+([a-z0-9_-]+)[^\\s]*\\b");
	private final Pattern NEWLINE_TO_BR_PATTERN = Pattern.compile("\\r?\\n");
	
	@Inject
	public EmailTemplateRendererService(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}
	
	
	/**
	 * renders a template and returns its output
	 * 
	 * @param templateName
	 * @param emailModel
	 * @param model
	 * @param locale
	 * @return
	 */
	public String render(String templateName, EmailModel emailModel, Map<String, ?> model, Locale locale) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Rendering template [{}] for recipient [{}]", templateName, emailModel.getTo());
		}
		
		final Map<String, Object> modelMap = new VariablesMap<String, Object>(model.size() + 1);
		modelMap.putAll(model);
		modelMap.put(THYMELEAF_EMAIL_MODEL_NAME, emailModel);
		
		
		Context context = new Context(locale, modelMap);
		StringWriter out = new StringWriter(TEMPLATE_BUFFER_INITIAL_SIZE);
		
		templateEngine.process(templateName, context, out);
		
		return out.getBuffer().toString();
	}
	
	
	/**
	 * Converts HTML to plain text using Jericho HTML parser
	 *  
	 * @see http://jericho.htmlparser.net
	 * @param html valid html Content
	 * @return a plain text representation of the HTML content (line breaks, list items, tables, etc. converted)
	 */
	public String htmlToText(String html) {
		Source source = new Source(html);
		return source.getRenderer().toString();
	}
	
	
	
	/**
	 * transforms plain text into HTML, mainly converting etities, links and line breaks
	 * @param text
	 * @return
	 */
	public String textToHtml(String text) {
		text = StringEscapeUtils.escapeHtml4(text);
		
		Matcher matcher = URL_TO_HTML_HREF_PATTERN.matcher(text);
		text = matcher.replaceAll("<a href=\"$0\">$0</a>");
		
		matcher = NEWLINE_TO_BR_PATTERN.matcher(text);
		text = matcher.replaceAll("<br />");
		
	    return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" + "\n" +
	    		"<html><head></head><body>" + "\n" +
	    		text + "</body></html>\n";
	}
}
