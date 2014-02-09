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
package org.sleeksnap.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple wrapper for a Map which contains POST or GET data
 * 
 * @author Nikki
 * 
 */
public class RequestData {
	
	/**
	 * The data map used to store values
	 */
	private Map<String, Object> data = new HashMap<String, Object>();
	
	/**
	 * Set a key to a value
	 * @param key
	 * 			The key to set
	 * @param value
	 * 			The value to set
	 * @return
	 * 			The RequestData instance for chaining
	 */
	public RequestData put(String key, Object value) {
		data.put(key, value);
		return this;
	}
	
	/**
	 * Get a value
	 * @param key
	 * 			The key to get the value for
	 * @return
	 * 			The value, or null if not found
	 */
	public Object get(String key) {
		return data.get(key);
	}
	
	/**
	 * Transform this data into a URLEncoded string
	 * @return
	 * 			The URL Encoded String
	 * @throws IOException
	 * 			If an encoding error occurs
	 */
	public String toURLEncodedString() throws IOException {
		return HttpUtil.implode(data);
	}
}