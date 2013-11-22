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

import java.io.IOException;
import java.io.InputStream;

/**
 * A base upload, used to define different types.
 * 
 * @author Nikki
 *
 */
public interface Upload {

	/**
	 * Get this upload as an InputStream
	 * @return
	 * 			The InputStream containing the upload data
	 */
	public InputStream asInputStream() throws IOException;
}
