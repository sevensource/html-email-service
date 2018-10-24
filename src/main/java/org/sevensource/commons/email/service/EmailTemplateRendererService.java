package org.sevensource.commons.email.service;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.sevensource.commons.email.model.EmailModel;
import org.sevensource.commons.email.template.TemplateEngineFactory;
import org.sevensource.commons.email.util.HtmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.expression.ThymeleafEvaluationContext;

import net.htmlparser.jericho.Source;


@Service
public class EmailTemplateRendererService {

	private final static Logger logger = LoggerFactory.getLogger(EmailTemplateRendererService.class);

	private final static String THYMELEAF_EMAIL_MODEL_NAME = "emailModel";

	private final static Pattern URL_TO_HTML_HREF_PATTERN = Pattern.compile("(?i)\\bhttps?://([a-z0-9_-]+\\.)+([a-z0-9_-]+)[^\\s]*\\b");
	private final static Pattern NEWLINE_TO_BR_PATTERN = Pattern.compile("\\r?\\n");

	private final TemplateEngineFactory templateEngineFactory;
	private final ApplicationContext applicationContext;

	@Inject
	public EmailTemplateRendererService(ApplicationContext ctx, TemplateEngineFactory templateEngineFactory) {
		this.templateEngineFactory = templateEngineFactory;
		this.applicationContext = ctx;
	}

	/**
	 * renders a template and returns its output
	 *
	 * @param templateName the templateName to be resolved by Thymeleafs templateResolver
	 * @param emailModel the emailModel
	 * @param model additional model
	 * @param locale the locale
	 *
	 * @return rendered output
	 */
	public String render(String templateName, EmailModel emailModel, Map<String, Object> model, Locale locale) {

		if (logger.isDebugEnabled()) {
			logger.debug("Rendering template [{}] for recipient [{}]", templateName, emailModel.getTo());
		}

		final Context context = new Context(locale);

        context.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
                new ThymeleafEvaluationContext(applicationContext, null));

		context.setVariables(model);
		context.setVariable(THYMELEAF_EMAIL_MODEL_NAME, emailModel);
		return templateEngineFactory.getTemplateEngine().process(templateName, context);
	}

	/**
	 * Converts HTML to plain text using Jericho HTML parser
	 *
	 * @see <a href="http://jericho.htmlparser.net">http://jericho.htmlparser.net</a>
	 * @param html valid html Content
	 * @return a plain text representation of the HTML content (line breaks, list items, tables, etc. converted)
	 */
	public String htmlToText(String html) {
		final Source source = new Source(html);
		return source.getRenderer().toString();
	}

	/**
	 * transforms plain text into HTML by
	 * <ul>
	 * <li>escaping HTML entities
	 * <li>converting URLs to &lt;a href&gt; tags
	 * <li>converting linebreaks to &lt;br&gt;
	 * <li>surrounding the text with a valid HTML4 markup
	 * </ul>
	 *
	 *
	 * @param text the plain text to convert
	 * @return a HTMLified version of the text
	 */
	public String textToHtml(final String text) {

		String escaped = HtmlUtil.escapeToHtml(text);

		Matcher matcher = URL_TO_HTML_HREF_PATTERN.matcher(escaped);
		escaped = matcher.replaceAll("<a href=\"$0\">$0</a>");

		matcher = NEWLINE_TO_BR_PATTERN.matcher(escaped);
		escaped = matcher.replaceAll("<br />");

	    return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" + "\n" +
	    		"<html><head>" + "\n" +
	    		"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" + "\n" +
	    		"</head><body>" + "\n" +
	    		escaped + "\n" +
	    		"</body></html>" + "\n";
	}
}
