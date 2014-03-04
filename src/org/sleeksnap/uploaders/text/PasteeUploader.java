/**
 * Sleeksnap, the open source cross-platform screenshot uploader
 * Copyright (C) 2014 Nikki <nikki@nikkii.us>
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
package org.sleeksnap.uploaders.text;

import java.util.concurrent.TimeUnit;

import org.sleeksnap.http.HttpUtil;
import org.sleeksnap.http.RequestData;
import org.sleeksnap.upload.TextUpload;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.uploaders.settings.Setting;
import org.sleeksnap.uploaders.settings.SettingsClass;
import org.sleeksnap.uploaders.text.PasteeUploader.PasteeSettings.PasteeExpiration;
import org.sleeksnap.util.Utils.FormatUtil;

/**
 * An uploader for Paste.ee
 * 
 * @author Nikki
 *
 */
@SettingsClass(PasteeUploader.PasteeSettings.class)
public class PasteeUploader extends Uploader<TextUpload> {
	
	/**
	 * The settings object used for this uploader
	 */
	private PasteeSettings settings;
	
	/**
	 * Construct this uploader with the loaded settings
	 * @param settings
	 * 			The settings object
	 */
	public PasteeUploader(PasteeSettings settings) {
		this.settings = settings;
	}

	@Override
	public String getName() {
		return "Paste.ee";
	}

	@Override
	public String upload(TextUpload upload) throws Exception {
		RequestData data = new RequestData();
		
		data.put("key", settings.apiKey != null && !settings.apiKey.isEmpty() ? settings.apiKey : "public")
			.put("language", "plain")
			.put("format", "simple")
			.put("paste", upload.getText());
		
		if(settings.expireViews > 0) {
			data.put("expire", "views;" + settings.expireViews);
		} else {
			data.put("expire", FormatUtil.formattedTimeToMinutes(settings.expiration != null ? settings.expiration.toString() : PasteeExpiration.NO_EXPIRATION.toString()));
		}
		
		return HttpUtil.executePost("http://paste.ee/api", data);
	}
	
	public static class PasteeSettings {
		@Setting(name = "API Key", description = "Paste.ee API Key", defaults = "public", optional = true)
		public String apiKey = "public";

		@Setting(name = "Description", description = "Paste Description", defaults = "", optional = true)
		public String description = "";

		@Setting(name = "Expiration", description = "Paste Expiration Time", optional = true)
		public PasteeExpiration expiration = PasteeExpiration.NO_EXPIRATION;

		@Setting(name = "Expiration Views", description = "Paste Expiration Views", defaults = "0", optional = true)
		public int expireViews;
		
		public enum PasteeExpiration {
			NO_EXPIRATION(0, TimeUnit.SECONDS),
			FIVE_MINUTES(5, TimeUnit.MINUTES),
			FIFTEEN_MINUTES(15, TimeUnit.MINUTES),
			THIRTY_MINUTES(30, TimeUnit.MINUTES),
			ONE_HOUR(1, TimeUnit.HOURS),
			SIX_HOURS(6, TimeUnit.HOURS),
			TWELVE_HOURS(12, TimeUnit.HOURS),
			ONE_DAY(1, TimeUnit.DAYS),
			THREE_DAYS(3, TimeUnit.DAYS),
			FIVE_DAYS(5, TimeUnit.DAYS),
			TEN_DAYS(10, TimeUnit.DAYS),
			FIFTEEN_DAYS(15, TimeUnit.DAYS),
			ONE_MONTH(30, TimeUnit.DAYS);
			
			/**
			 * The time in the specified unit for this expiration
			 */
			private int time;
			
			/**
			 * The time unit for this expiration
			 */
			private TimeUnit unit;

			private PasteeExpiration(int time, TimeUnit unit) {
				this.time = time;
				this.unit = unit;
			}
			
			/**
			 * Convert this expiration time to minutes
			 * @return
			 * 		The converted time in minutes
			 */
			public int toMinutes() {
				return (int) unit.toMinutes(time);
			}
			
			/**
			 * Convert this value to a string. This is necessary because we cannot use numbers.
			 */
			@Override
			public String toString() {
				String name = unit.name().toLowerCase();
				if (time == 1) {
					name = name.substring(0, name.length()-1);
				}
				return time == 0 ? "No Expiration" : time + " " + name;
			}
		}
	}
}
