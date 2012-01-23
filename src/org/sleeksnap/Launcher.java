package org.sleeksnap;

import java.util.ArrayList;

import org.sleeksnap.util.Utils.FileUtils;

public class Launcher {
	public static void main(String[] args) {
		String memSize = "64m";
		if(args.length > 0) {
			if(args[0].startsWith("-memory") && args.length >= 2) {
				memSize = args[1];
			}
		}
		try {
			String pathToJar = FileUtils.getJarPath(Launcher.class);

			ArrayList<String> params = new ArrayList<String>();

			params.add("javaw");
			params.add("-Xmx"+memSize);
			params.add("-classpath");
			params.add(pathToJar);
			params.add("org.sleeksnap.ScreenSnapper");
			ProcessBuilder pb = new ProcessBuilder(params);
			Process process = pb.start();
			if (process == null)
				throw new Exception("!");
			System.exit(0);
		} catch (Exception e) {
			ScreenSnapper.main(args);
		}
	}
}
