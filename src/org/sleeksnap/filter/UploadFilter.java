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
package org.sleeksnap.filter;

import org.sleeksnap.upload.Upload;

/**
 * An interface representing an upload filter, used to modify images, text or files before uploading
 * @author Nikki
 *
 * @param <T>
 */
public interface UploadFilter<T extends Upload> {

	/**
	 * Apply this filter to the object
	 * @param object
	 * 			The object to apply to
	 * @return
	 * 			The modified object, or original object.
	 */
	public T filter(T object);
	
}
