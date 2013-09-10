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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * An upload which targets a File
 * 
 * @author Nikki
 *
 */
public class FileUpload implements Upload {

	/**
	 * The file in this upload
	 */
	private File file;
	
	public FileUpload(File file) {
		this.file = file;
	}
	
	/**
	 * Set the upload's file
	 * @param file
	 * 			The file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
	
	/**
	 * Get the file for the upload
	 * @return
	 * 		The file
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Get this file as a FileInputStream
	 */
	@Override
	public InputStream asInputStream() {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return null;
		}
	}
}
