package org.sleeksnap.uploaders.images;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.binary.Base64;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImgurUploader extends Uploader<BufferedImage> {

	@Override
	public String getName() {
		return "Imgur";
	}

	@Override
	public Class<?> getUploadType() {
		return BufferedImage.class;
	}

	@Override
	public String upload(BufferedImage image) throws Exception {
		URL url = new URL("http://api.imgur.com/2/upload.xml");
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(image, "PNG", output);
		/**
		 * Encode the image into a base64 string using apache commons codec
		 */
		String data = URLEncoder.encode("image", "UTF-8")
				+ "="
				+ URLEncoder.encode(
						Base64.encodeBase64String(output.toByteArray()),
						"UTF-8");
		data += "&key=a071fe99cee17999a8ff93b282cd602f";
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		/**
		 * Write the image data and api key
		 */
		OutputStreamWriter writer = new OutputStreamWriter(
				connection.getOutputStream());
		writer.write(data);
		writer.flush();
		writer.close();

		int remainingUploads = Integer.parseInt(connection.getHeaderField("X-RateLimit-Remaining"));
		long resetTime = Long.parseLong(connection.getHeaderField("X-RateLimit-Reset"));
		if(remainingUploads <= 50 && (resetTime - Util.currentTimeSeconds()) >= 600) {
			//TODO show a warning about remaining uploads
		}
		/**
		 * Parse the URL from the response
		 */
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document d = db.parse(connection.getInputStream());
		NodeList nodeList = d.getElementsByTagName("original").item(0)
				.getChildNodes();
		Node n = (Node) nodeList.item(0);

		return n.getNodeValue();
	}
}
