package org.sleeksnap.uploaders.images.imgur;

public class ImgurAuthenticationObject {
	private String accessToken;
	private String refreshToken;
	private String accountUsername;
	
	private long expirationTime = 0;
	
	public long getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public void setAccountUsername(String accountUsername) {
		this.accountUsername = accountUsername;
	}
	public String getAccountUsername() {
		return accountUsername;
	}
}
