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
package org.sleeksnap.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.sleeksnap.util.Util;

/**
 * A basic multi language implementation using JSON files
 * 
 * @author Nikki
 *
 */
public class Language {
	
	/**
	 * Pattern to match variables with
	 */
	private static final Pattern VAR_PATTERN = Pattern.compile("\\{(\\d+)\\}");
	
	/**
	 * Tokens in the current loaded language set
	 */
	private static Map<String, String> tokens = new HashMap<String, String>();
	
	/**
	 * Load a language after loading the default 'english' language to prevent missing phrases.
	 * @param lang
	 * 			The language name to load
	 * @throws IOException
	 * 			If an error occurs while loading
	 */
	public static void load(String lang) throws IOException {
		tokens.clear();
		loadAndMerge("english");
		if(!lang.equals("english")) {
			loadAndMerge(lang);
		}
	}
	
	/**
	 * Load and merge the tokens from a language
	 * @param lang
	 * 			The language to load
	 * @throws IOException
	 * 			If an error occurs, or if it's an invalid language file
	 */
	@SuppressWarnings("unchecked")
	public static void loadAndMerge(String lang) throws IOException {
		URL url = Util.getResourceByName("/languages/" + lang + ".json");
		if(url != null) {
			InputStream input = url.openStream();
			
			JSONObject obj = new JSONObject(new JSONTokener(input));
			
			JSONObject tokenObj = obj.getJSONObject("tokens");
			
			for(String s : (Set<String>) tokenObj.keySet()) {
				tokens.put(s, tokenObj.getString(s));
			}
			
			input.close();
		} else {
			throw new IOException("Invalid language " + lang);
		}
	}

	/**
	 * Get a language string
	 * @param token
	 * 			The language token name
	 * @param args
	 * 			The language arguments
	 * @return
	 * 			The parsed language string, or "" if null
	 */
	public static String getString(String token, Object... args) {
		String str = tokens.get(token);
		
		if(str == null) {
			return "";
		}
		
		if(str.indexOf('{') != -1) {
			Matcher m = VAR_PATTERN.matcher(str);
			
			StringBuffer out = new StringBuffer();
			while(m.find()) {
				int paramIndex = Integer.parseInt(m.group(1));
				if(args.length > paramIndex-1) {
					m.appendReplacement(out, args[paramIndex-1].toString());
				}
			}
			m.appendTail(out);
			
			str = out.toString();
		}
		return str;
	}
}
