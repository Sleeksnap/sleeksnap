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
package org.sleeksnap.uploaders.generic;

import org.sleeksnap.upload.Upload;
import org.sleeksnap.uploaders.Uploader;

/**
 * A basic uploader
 * 
 * @author Nikki
 */
public abstract class GenericUploader extends Uploader<Upload> {

	/**
	 * Get the uploaders that this uploader can use
	 * 
	 * @return The uploaders
	 */
	public abstract Uploader<?>[] getSubUploaders();

	@Override
	public String upload(Upload t) throws Exception {
		return null;
	}
}