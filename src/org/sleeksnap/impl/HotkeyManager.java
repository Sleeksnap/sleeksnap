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

	/**
	 * The screensnapper instance
	 */
	private ScreenSnapper snapper;

	/**
	 * Whether the input has been initialized or disabled
	 */
	private boolean initialized = false;

	/**
	 * Construct a new hotkey manager
	 * 
	 * @param snapper
	 *            The screensnapper instance
	 */
	public HotkeyManager(ScreenSnapper snapper) {
		this.snapper = snapper;
		this.provider = HotkeyProvider.getCurrentProvider(false);
		// Register a shutdown hook just in case
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
								snapper.getTrayIcon()
										.displayMessage(
												"Error",
												"Could not open settings, is there another window open?",
												TrayIcon.MessageType.ERROR);
							}
						}
					});
		}
		// We support active windows only on windows/linux, but OSX SOON!
		if ((Platform.isWindows() || Platform.isLinux())
				&& keys.containsKey("active")) {
			provider.register(KeyStroke.getKeyStroke(keys.get("active")),
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

	/**
	 * Reset the bound keys and let other classes know that they are not set
	 */
	public void resetKeys() {
		try {
			provider.reset();
		} finally {
			initialized = false;
		}
	}

	/**
	 * Get whether this class has been initialized/keys have been bound
	 * 
	 * @return The value of <code>initialized</code>
	 */
	public boolean hasKeysBound() {
		return initialized;
	}
}
