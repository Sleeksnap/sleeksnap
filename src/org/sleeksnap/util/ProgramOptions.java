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
package org.sleeksnap.util;

import java.util.HashMap;
import java.util.Map;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.Option;
import com.sanityinc.jargs.CmdLineParser.OptionException;

/**
 * A Program Options parser using Jargs
 * 
 * @author Nikki
 *
 */
public class ProgramOptions {

	/**
	 * Parse program arguments into a map
	 * @param args
	 * 			The arguments to prase
	 * @return
	 * 			The map of parsed arguments
	 */
	public static Map<String, Object> parseSettings(String[] args) {
		Map<String, Object> out = new HashMap<String, Object>();
		
		CmdLineParser parser = new CmdLineParser();
		
		Option<String> dir = parser.addStringOption('d', "dir");
		Option<String> language = parser.addStringOption('l', "language");
		Option<Boolean> resetConfig = parser.addBooleanOption("resetconfig");
		
		try {
			parser.parse(args);
		} catch (OptionException e) {
			return out;
		}
		
		if(dir.getOptionValue(parser) != null) {
			out.put("dir", dir.getOptionValue(parser));
		}
		
		if(language.getOptionValue(parser) != null) {
			out.put("language", dir.getOptionValue(parser));
		}
		
		if(resetConfig.getOptionValue(parser, false)) {
			out.put("resetconfig", true);
		}
		
		return out;
	}
}
