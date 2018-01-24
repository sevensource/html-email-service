package org.sevensource.commons.email.model;

import org.springframework.core.io.Resource;

public interface AttachmentModel {

	public String getFilename();
	public Resource getResource();
	public boolean isInline();
}
