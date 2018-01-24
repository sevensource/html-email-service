package org.sevensource.commons.email.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class HtmlUtilTest {

	@Test
	public void works() {
		String test = "<ü";
		String result = HtmlUtil.escapeToHtml(test);

		assertThat(result).doesNotContain("<");
		assertThat(result).doesNotContain("ü");
	}
}
