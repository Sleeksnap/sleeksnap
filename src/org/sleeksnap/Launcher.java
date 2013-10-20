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
package org.sleeksnap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.sleeksnap.util.Util;
import org.sleeksnap.util.Util.OperatingSystem;
import org.sleeksnap.util.Utils.FileUtils;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.Option;
import com.sanityinc.jargs.CmdLineParser.OptionException;
import com.sun.jna.Platform;

/**
 * A simple launcher which will re-launch Sleeksnap with the specified memory (Default is 128MB)
 * @author Nikki
 *
 */
public class Launcher {
	
	public static void main(String[] args) {
		CmdLineParser parser = new CmdLineParser();
		
		Option<Integer> memory = parser.addIntegerOption('m', "memory");
		
		try {
			parser.parse(args);
		} catch (OptionException e1) {
			e1.printStackTrace();
		}
		
		try {
			File jar = FileUtils.getJarFile(Launcher.class);

			launch(jar, "org.sleeksnap.ScreenSnapper", new String[] { "-Xmx" + memory.getOptionValue(parser, 128) + "m" }, parser.getRemainingArgs());
			
			System.exit(0);
		} catch (Exception e) {
			ScreenSnapper.main(args);
		}
	}
	
	/**
	 * Launch a class from the jar file
	 * 
	 * @param jarFile
	 * 			The jar file to launch from
	 * @param className
	 * 			The class in the jar file to call
	 * @throws Exception
	 * 			If an error occurs
	 */
	public static void launch(File jarFile, String className) throws Exception {
		launch(jarFile, className, null, null);
	}
	
	/**
	 * Launch a class from a jar file with java and normal arguments
	 * 
	 * @param jarFile
	 * 			The jar file to launch from
	 * @param className
	 * 			The class to  launch
	 * @param javaArgs
	 * 			The java arguments (Added BEFORE -classpath)
	 * @param args
	 * 			The program arguments
	 * @throws Exception
	 * 			If an error occurs and is unable to launch
	 */
	public static void launch(File jarFile, String className, String[] javaArgs, String[] args) throws Exception {
		ArrayList<String> params = new ArrayList<String>();
		
		File exe = FileUtils.getJavaExecutable();
		
		if(exe == null) {
			throw new Exception("Unable to find java executable!");
		}

		params.add(exe.getPath());
		
		if(javaArgs != null)
			params.addAll(Arrays.asList(javaArgs));
		
		if(Util.getPlatform() == OperatingSystem.MAC) {
			params.add("-Dapple.awt.UIElement=true");
		}
		
		params.add("-classpath");
		params.add(jarFile.getAbsolutePath());
		params.add(className);
		
		if(args != null)
			params.addAll(Arrays.asList(args));
		
		String[] cmd = params.toArray(new String[params.size()]);
		
		Runtime.getRuntime().exec(cmd);
	}
}
