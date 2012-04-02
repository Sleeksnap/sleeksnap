package org.sleeksnap.api;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import org.sleeksnap.api.SleeksnapProto.ReturnType;

@SuppressWarnings("unused")
public class SleeksnapApiClient {
	
	private DatagramSocket socket;
	
	private InetSocketAddress address;
	
	public SleeksnapApiClient(InetSocketAddress address) {
		this.address = address;
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void requestUpload(Object object, ReturnType type) {
		
	}
	
	public void send(byte[] data) throws IOException {
		socket.send(new DatagramPacket(data, data.length, address));
	}
}