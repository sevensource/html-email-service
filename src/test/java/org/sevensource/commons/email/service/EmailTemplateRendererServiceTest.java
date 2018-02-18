package org.sevensource.commons.email.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.AddressException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.commons.email.model.DefaultEmailModel;
import org.sevensource.commons.email.template.DefaultTemplateEngineFactory;
import org.sevensource.commons.email.template.TemplateEngineFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class EmailTemplateRendererServiceTest {

	@Configuration
	public static class TestConfig {
	}

	@Autowired
	ApplicationContext appctx;

	EmailTemplateRendererService service;


	@Before
	public void before() {
		TemplateEngineFactory factory = new DefaultTemplateEngineFactory(appctx);
		service = new EmailTemplateRendererService(factory);
	}

	@Test
	public void appctx_is_not_null() {
		assertThat(appctx).isNotNull();
	}

	@Test
	public void html_to_text_works() {
		String html = "<a href='http://test.com'>click</a>";
		String text = service.htmlToText(html);
		assertThat(text).isEqualTo("click <http://test.com>");

		html = "<a href='http://test.com'>test.com</a>";
		text = service.htmlToText(html);
		assertThat(text).isEqualTo("<http://test.com>");
	}

	@Test
	public void text_to_html_works() {
		String text = ">more\nhttp://www.test.com";
		String html = service.textToHtml(text);
		assertThat(html).contains("&gt;more<br/><a href=\"http://www.test.com\">http://www.test.com</a>");
	}

	@Test
	public void render_works() throws AddressException {
		DefaultEmailModel emailModel = new DefaultEmailModel();
		emailModel.addTo("test@test.com", "Test Recipient");
		Map<String, Object> model = new HashMap<>();
		model.put("ordernumber", "1234567890");

		String result = service.render("test", emailModel, model, Locale.ENGLISH);

		assertThat(result).contains("<h1>Welcome</h1>");
		assertThat(result).contains("<span>Test Recipient</span>");
		assertThat(result).contains("<span>test@test.com</span>");
		assertThat(result).contains("<span>1234567890</span>");
	}

	@Test
	public void render_with_locale_works() throws AddressException {
		DefaultEmailModel emailModel = new DefaultEmailModel();
		emailModel.addTo("test@test.com", "Test Recipient");
		Map<String, Object> model = new HashMap<>();
		model.put("ordernumber", "1234567890");

		String result = service.render("test", emailModel, model, Locale.GERMAN);

		assertThat(result).contains("<h1>Willkommen</h1>");
		assertThat(result).contains("<span>Test Recipient</span>");
		assertThat(result).contains("<span>test@test.com</span>");
		assertThat(result).contains("<span>1234567890</span>");
	}
}
