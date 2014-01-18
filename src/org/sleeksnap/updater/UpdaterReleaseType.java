package org.sleeksnap.updater;

public enum UpdaterReleaseType {
	RECOMMENDED("latest.json"), DEVELOPMENT("latest_development.json");
	
	private String feedPath;

	private UpdaterReleaseType(String feedPath) {
		this.feedPath = feedPath;
	}
	
	public String getFeedPath() {
		return feedPath;
	}
}
