/**
 * Sleeksnap, the open source cross-platform screenshot uploader
 * Copyright (C) 2012 Nikki <nikki@nikkii.us>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sleeksnap.upload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * An upload commonly used for Pastebins which contains text only
 * 
 * @author Nikki
 *
 */
public class TextUpload implements Upload {

	/**
	 * The upload text data
	 */
	private String text;
	
	public TextUpload(String text) {
		this.text = text;
	}
	
	@Override
	public InputStream asInputStream() {
		return new ByteArrayInputStream(text.getBytes());
	}
	
	/**
	 * Get this upload's contents
	 * 
	 * @return
	 * 			The upload contents
	 */
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return text;
	}
}
