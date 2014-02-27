package org.sleeksnap.uploaders.images.imgur;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.sleeksnap.ScreenSnapper;
import org.sleeksnap.http.HttpUtil;
import org.sleeksnap.http.RequestData;
import org.sleeksnap.uploaders.UploaderConfigurationException;
import org.sleeksnap.uploaders.images.ImgurUploader;
import org.sleeksnap.util.Util;

/**
 * Provides helpful methods for Authentication on Imgur
 * 
 * @author Nikki
 *
 */
public class ImgurAuthentication {
	
	private static final Logger logger = Logger.getLogger(ImgurUploader.class.getName());
	
	/**
	 * The Imgur Refresh Auth URL
	 */
	private static final String TOKEN_URL = "https://api.imgur.com/oauth2/token";
	
	/**
	 * The ImgurUploader this authentication belongs to
	 */
	private ImgurUploader parent;

	public ImgurAuthentication(ImgurUploader parent) {
		this.parent = parent;
	}
	
	/**
	 * Check and add the access token to the request
	 * @param conn
	 * 			The connection to add the token to
	 * @throws Exception
	 */
	public void addToConnection(URLConnection conn) throws Exception {
		ImgurAuthenticationObject account = parent.getSettings().account;
		if (account != null && account.getAccessToken() != null) {
			// Check token validity
			if(Util.currentTimeSeconds() > account.getExpirationTime()) {
				logger.info("Token expired, requesting new token...");
				requestNewAccessToken();
				
				// Refresh the object we have
				account = parent.getSettings().account;
			}
			// If we have a good token, use it!
			if(account != null && account.getAccessToken() != null) {
				conn.setRequestProperty("Authorization", "Bearer " + account.getAccessToken());
				return;
			}
			throw new UploaderConfigurationException("Unable to renew access token!");
		} else {
			conn.setRequestProperty("Authorization", "Client-ID " + ImgurUploader.CLIENT_ID);
		}
	}
	
	/**
	 * Requests a new access token to use for uploads
	 * @throws Exception
	 * 			If an error occurs during the request
	 */
	public void requestNewAccessToken() throws Exception {
		ImgurAuthenticationObject account = parent.getSettings().account;
		
		String refreshToken = account.getRefreshToken();

		if (refreshToken == null) {
			throw new UploaderConfigurationException("No refresh token found.");
		}
		
		account.setAccessToken(null);

		HttpURLConnection conn = (HttpURLConnection) new URL(TOKEN_URL).openConnection();
		conn.setReadTimeout(10000);
		try {
			RequestData data = new RequestData();
			data.put("refresh_token", refreshToken);
			data.put("client_id", ImgurUploader.CLIENT_ID);
			data.put("client_secret", ImgurUploader.CLIENT_SECRET);
			data.put("grant_type", "refresh_token");
			
			String response = HttpUtil.executePost(TOKEN_URL, data);
			
			try {
				JSONObject obj = new JSONObject(response);
				
				// expires_in = seconds, update the counter
				long expireTime = obj.getLong("expires_in");
				
				account.setAccessToken(obj.getString("access_token"));
				account.setExpirationTime(Util.currentTimeSeconds() + expireTime);
				
				// The object is the same as what we would have, so just keep it :D
				parent.saveSettings(ScreenSnapper.getSettingsFile(ImgurUploader.class));
			} catch(JSONException e) {
				throw new UploaderConfigurationException("Unable to refresh access token from Imgur!");
			}
		} finally {
			try {
				conn.disconnect();
			} catch (Exception ignore) {}
		}
	}
}
