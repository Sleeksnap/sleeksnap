package org.sleeksnap.impl;

import java.awt.TrayIcon;
import java.util.Map;

import javax.swing.KeyStroke;

import org.sleeksnap.ScreenSnapper;

import com.sun.jna.Platform;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.HotkeyProvider;

public class HotkeyManager {

	/**
	 * The hotkey provider
	 */
	private HotkeyProvider provider;
	private ScreenSnapper snapper;
	
	private boolean initialized = false;
	
	public HotkeyManager(ScreenSnapper snapper) {
		this.snapper = snapper;
		this.provider = HotkeyProvider.getCurrentProvider(false);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				cleanupInput();
			}
		});
	}
	
	/**
	 * Initialize the inputs, using JIntellitype for Windows and JXGrabKey for
	 * Linux
	 */
	public void initializeInput() {
		Map<String, String> keys = snapper.getConfiguration().getMap("hotkeys");
		if (keys.containsKey("crop")) {
			provider.register(KeyStroke.getKeyStroke(keys.get("crop")),
					new HotKeyListener() {
						@Override
						public void onHotKey(HotKey hotKey) {
							snapper.crop();
						}
					});
		}
		if (keys.containsKey("full")) {
			provider.register(KeyStroke.getKeyStroke(keys.get("full")),
					new HotKeyListener() {
						@Override
						public void onHotKey(HotKey hotKey) {
							snapper.full();
						}
					});
		}
		if (keys.containsKey("clipboard")) {
			provider.register(KeyStroke.getKeyStroke(keys.get("clipboard")),
					new HotKeyListener() {
						@Override
						public void onHotKey(HotKey hotKey) {
							snapper.clipboard();
						}
					});
		}
		if (keys.containsKey("options")) {
			provider.register(KeyStroke.getKeyStroke(keys.get("options")),
					new HotKeyListener() {
						@Override
						public void onHotKey(HotKey hotKey) {
							if (!snapper.openSettings()) {
								snapper.getTrayIcon().displayMessage(
										"Error",
										"Could not open settings, is there another window open?",
										TrayIcon.MessageType.ERROR);
							}
						}
					});
		}
		// We support active windows only on windows operating systems
		if (Platform.isWindows() && keys.containsKey("active")) {
			provider.register(
					KeyStroke.getKeyStroke(keys.get("active")),
					new HotKeyListener() {
						@Override
						public void onHotKey(HotKey hotKey) {
							snapper.active();
						}
					});
		}
		initialized = true;
	}


	/**
	 * Clean up the input bindings
	 */
	public void cleanupInput() {
		resetKeys();
		provider.stop();
	}
	
	public void resetKeys() {
		try {
			provider.reset();
		} finally {
			initialized = false;
		}
	}

	public boolean hasKeysBound() {
		return initialized;
	}
}
