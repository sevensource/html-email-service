package org.sevensource.commons.email.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class HtmlUtilTest {

	@Test
	public void works() {
		String test = "<>ü\"& a  b\nnewline";
		String result = HtmlUtil.escapeToHtml(test);

		assertThat(result).isEqualTo("&lt;&gt;&#252;&quot;&amp; a &nbsp;b<br/>newline");
	}
}
