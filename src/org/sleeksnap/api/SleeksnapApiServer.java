package org.sleeksnap.api;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.sleeksnap.ScreenSnapper;
import org.sleeksnap.api.SleeksnapProto.ActiveRequest;
import org.sleeksnap.api.SleeksnapProto.AreaRequest;
import org.sleeksnap.api.SleeksnapProto.FullRequest;
import org.sleeksnap.api.SleeksnapProto.RequestResponse;
import org.sleeksnap.api.SleeksnapProto.RequestStatus;
import org.sleeksnap.api.SleeksnapProto.UploadRequest;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.ScreenshotUtil;
import org.sleeksnap.util.Utils.DateUtil;
import org.sleeksnap.util.active.WindowUtilProvider;

import com.google.protobuf.ByteString;
import com.google.protobuf.ByteString.Output;

public class SleeksnapApiServer implements Runnable {
	
	private static final Logger logger = Logger.getAnonymousLogger();

	private ScreenSnapper snapper;
	private DatagramSocket socket;

	public SleeksnapApiServer(ScreenSnapper snapper) {
		this.snapper = snapper;
		try {
			socket = new DatagramSocket(9876);
		} catch (SocketException e) {
			logger.log(Level.SEVERE, "Unable to start API", e);
		}
	}

	public void buildError(Throwable throwable, RequestResponse.Builder resp) {
		resp.setStatus(RequestStatus.STATUS_ERROR);
		resp.setData(ByteString.copyFromUtf8(throwable.getMessage()));
	}

	public RequestResponse handleActiveRequest(ActiveRequest request) {
		RequestResponse.Builder response = RequestResponse.newBuilder();
		response.setRequestId(request.getRequestId());
		try {
			BufferedImage image = ScreenshotUtil.capture(WindowUtilProvider
					.getWindowUtil().getActiveWindow().getBounds());
			switch (request.getReturnType()) {
			case RETURN_URL:
				uploadRequest(image, response);
				break;
			case RETURN_FILEPATH:
				// TODO temp file
				break;
			case RETURN_IMAGE:
				imageResponse(image, response);
				break;
			}
		} catch(Exception e) {
			buildError(e, response);
		}
		return response.build();
	}

	private RequestResponse handleAreaRequest(AreaRequest request) throws IOException {
		RequestResponse.Builder response = RequestResponse.newBuilder();
		response.setRequestId(request.getRequestId());
		
		Rectangle rectangle = new Rectangle(request.getX(), request.getY(), request.getWidth(), request.getHeight());
		BufferedImage image = ScreenshotUtil.capture(rectangle);
		switch (request.getReturnType()) {
		case RETURN_URL:
			uploadRequest(image, response);
			break;
		case RETURN_FILEPATH:
			// TODO temp file
			break;
		case RETURN_IMAGE:
			imageResponse(image, response);
			break;
		}
		return response.build();
	}

	private RequestResponse handleFullRequest(FullRequest request) throws IOException {
		RequestResponse.Builder response = RequestResponse.newBuilder();
		response.setRequestId(request.getRequestId());
		
		BufferedImage image = ScreenshotUtil.capture();
		switch (request.getReturnType()) {
		case RETURN_URL:
			uploadRequest(image, response);
			break;
		case RETURN_FILEPATH:
			// TODO temp file
			break;
		case RETURN_IMAGE:
			imageResponse(image, response);
			break;
		}
		return response.build();
	}

	public void handleRequest() throws IOException {
		byte[] headerBytes = new byte[5];

		DatagramPacket receivePacket = new DatagramPacket(headerBytes,
				headerBytes.length);

		socket.receive(receivePacket);

		RequestType type = RequestType.values()[headerBytes[0]];
		int length = (headerBytes[1] << 24) + ((headerBytes[2] & 0xFF) << 16)
				+ ((headerBytes[3] & 0xFF) << 8) + (headerBytes[4] & 0xFF);

		byte[] contentBytes = new byte[length];

		receivePacket = new DatagramPacket(contentBytes, contentBytes.length);
		socket.receive(receivePacket);

		InetAddress address = receivePacket.getAddress();

		RequestResponse response = null;

		switch (type) {
		case UPLOAD:
			response = handleUploadRequest(UploadRequest
					.parseFrom(contentBytes));
			break;
		case ACTIVE:
			response = handleActiveRequest(ActiveRequest.parseFrom(contentBytes));
			break;
		case AREA:
			response = handleAreaRequest(AreaRequest.parseFrom(contentBytes));
			break;
		case FULL:
			response = handleFullRequest(FullRequest.parseFrom(contentBytes));
			break;
		}

		if (response != null) {
			byte[] responseBytes = response.toByteArray();

			DatagramPacket packet = new DatagramPacket(responseBytes,
					responseBytes.length, address, receivePacket.getPort());
			socket.send(packet);
		}
	}
	
	public RequestResponse handleUploadRequest(UploadRequest request)
			throws IOException {
		RequestResponse.Builder response = RequestResponse.newBuilder();
		response.setRequestId(request.getRequestId());
		ByteString bytes = request.getData();
		Object object = null;
		switch (request.getContentType()) {
		case CONTENT_IMAGE:
			object = ImageIO.read(bytes.newInput());
			break;
		case CONTENT_FILE:
			File file = File.createTempFile("sleeksnapapifile", ".file");
			FileOutputStream out = new FileOutputStream(file);
			out.write(bytes.toByteArray());
			out.close();
			file.deleteOnExit();
			object = file;
			break;
		case CONTENT_URL:
			object = new URL(bytes.toString());
			break;
		}
		// Execute the upload
		uploadRequest(object, response);
		// Build the response
		return response.build();
	}

	public void imageResponse(BufferedImage image,
			RequestResponse.Builder response) throws IOException {
		Output string = ByteString.newOutput();
		try {
			ImageIO.write(image, "png", string);
			response.setData(string.toByteString());
		} catch (IOException e) {
			buildError(e, response);
		}
		string.close();
	}

	public void run() {
		while (true) {
			try {
				handleRequest();
			} catch (IOException e) {
				logger.log(Level.WARNING, "API Error!", e);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void uploadRequest(Object object, RequestResponse.Builder response) {
		if (object != null) {
			try {
				Uploader uploader = snapper.getUploaderFor(object.getClass());
				if (uploader != null) {
					String url = uploader.upload(uploader.getUploadType().cast(
							object));
					if (url != null) {
						if (snapper.getConfiguration()
								.getBoolean("shortenurls")
								&& uploader.getUploadType() != URL.class) {
							Uploader shortener = snapper
									.getUploaderFor(URL.class);
							if (shortener != null) {
								url = shortener.upload(new URL(url));
							}
						}
						if (object instanceof BufferedImage) {
							if (snapper.getConfiguration().getBoolean(
									"savelocal")) {
								FileOutputStream output = new FileOutputStream(
										snapper.getLocalFile(DateUtil
												.getCurrentDate() + ".png"));
								try {
									ImageIO.write(((BufferedImage) object),
											"png", output);
								} finally {
									output.close();
								}
							}
						}
						response.setStatus(RequestStatus.STATUS_SUCCESS);
						response.setData(ByteString.copyFromUtf8(url));
					} else {
						throw new Exception("Failed to get URL");
					}
				}
			} catch (Exception e) {
				buildError(e, response);
			}
		} else {
			response.setStatus(RequestStatus.STATUS_ERROR);
		}
	}
}