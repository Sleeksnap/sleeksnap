package org.sleeksnap.uploaders.images;

import org.sleeksnap.http.HttpUtil;
import org.sleeksnap.http.MultipartPostMethod;
import org.sleeksnap.upload.ImageUpload;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.uploaders.settings.Setting;
import org.sleeksnap.uploaders.settings.SettingsClass;
import org.sleeksnap.uploaders.settings.types.ComboBoxSettingType;
import org.sleeksnap.util.Utils;

import java.net.URLEncoder;

/**
 * An Uploader for basic image hosting scripts.
 *
 * @author Nikki
 */
@SettingsClass(ImageScriptUploader.ImageScriptSettings.class)
public class ImageScriptUploader extends Uploader<ImageUpload> {

	/**
	 * The image hosting script settings.
	 */
	private final ImageScriptSettings settings;

	/**
	 * Construct a new ImageScriptUploader.
	 *
	 * @param settings The settings instance.
	 */
	public ImageScriptUploader(ImageScriptSettings settings) {
		this.settings = settings;
	}

	@Override
	public String getName() {
		return "Image Hosting Script";
	}

	@Override
	public String upload(ImageUpload imageUpload) throws Exception {
		switch (settings.formType.toLowerCase()) {
			case "base64":
				String resp = HttpUtil.executePost(settings.url, settings.variableName + "=" + URLEncoder.encode(imageUpload.toBase64(), "UTF-8"));
				if(!resp.substring(0, 4).equals("http")) {
					throw new UploadException(resp);
				}
				return resp;
			case "multipart":
				MultipartPostMethod method = new MultipartPostMethod(settings.url);
				method.setParameter(settings.variableName, new MultipartPostMethod.MultipartFile(Utils.FileUtils.generateFileName(imageUpload), imageUpload.asInputStream()));
				method.execute();
				return method.getResponse();
			default:
				throw new UploadException("Unknown form type.");
		}
	}

	public static class ImageScriptSettings {

		@Setting(name = "Script URL", description = "Path to image upload script (including url)")
		public String url;

		@Setting(name = "Form Variable", description = "Form variable name")
		public String variableName;

		@Setting(name = "Form Type", description = "Form Type, Normal (Base64 Encoded) or Multipart.", type = ComboBoxSettingType.class, defaults = { "Base64", "Multipart" })
		public String formType = "Base64";

	}
}
