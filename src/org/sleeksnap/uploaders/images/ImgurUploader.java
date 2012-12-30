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
package org.sleeksnap.uploaders.images;

import java.awt.image.BufferedImage;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.HttpUtil;
import org.sleeksnap.util.Utils.ImageUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An uploader to upload images to imgur.com The included API Key is for use by
 * Sleeksnap ONLY, If you would like a key you may register one at imgur's
 * website
 * 
 * @author Nikki
 * 
 */
public class ImgurUploader extends Uploader<BufferedImage> {
	
	private static final String API_KEY = "a071fe99cee17999a8ff93b282cd602f";

	@Override
	public String getName() {
		return "Imgur";
	}

	@Override
	public String upload(BufferedImage image) throws Exception {
		URL url = new URL("http://api.imgur.com/2/upload.xml");
		/**
		 * Encode the image into a base64 string using apache commons codec
		 */
		Map<String, Object> req = new HashMap<String, Object>();
		req.put("image", ImageUtil.toBase64(image));
		req.put("key", API_KEY);
		
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		
		/**
		 * Write the image data and api key
		 */
		OutputStreamWriter writer = new OutputStreamWriter(
				connection.getOutputStream());
		writer.write(HttpUtil.implode(req));
		writer.flush();
		writer.close();
		
		
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
