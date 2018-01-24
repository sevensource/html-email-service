package org.sevensource.commons.email.model;

import org.springframework.core.io.Resource;

public class DefaultAttachmentModel implements AttachmentModel {
	private final String filename;
	private final Resource resource;
	private final boolean inline;

	public DefaultAttachmentModel(String filename, Resource resource, boolean inline) {
		this.filename = filename;
		this.resource = resource;
		this.inline = inline;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public Resource getResource() {
		return resource;
	}

	@Override
	public boolean isInline() {
		return inline;
	}
}