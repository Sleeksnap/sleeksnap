/**
 * Sleeksnap, the open source cross-platform screenshot uploader
 * Copyright (C) 2012 Nicole Schuiteman
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
package org.sleeksnap.uploaders;


/**
 * A basic uploader
 * 
 * @author Nikki
 *
 * @param <T>
 * 			The upload type
 */
public abstract class GenericUploader extends Uploader<Object> {
	
	/**
	 * Get the uploaders that this uploader can use
	 * @return
	 * 		The uploaders
	 */
	public abstract Uploader<?>[] getSubUploaders();

	
	@Override
	public Class<?> getUploadType() {
		return Object.class;
	}

	@Override
	public String upload(Object t) throws Exception {
		return null;
	}
}