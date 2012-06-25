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
package org.sleeksnap.gui.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import org.sleeksnap.Configuration;
import org.sleeksnap.gui.OptionPanel;
import org.sleeksnap.util.Util;

import com.sun.jna.Platform;

/**
 * An OptionSubPanel for Hotkeys
 * 
 * @author Nikki
 *
 */
@SuppressWarnings({"serial"})
public class HotkeyPanel extends OptionSubPanel {
	private OptionPanel parent;
	
	private Configuration configuration;
	

    private JLabel fullscreenLabel;
    private JLabel hotkeySettingsLabel;
    private JLabel cropLabel;
    private JLabel clipboardLabel;
    private JLabel activeLabel;
    private JLabel noteLabel;
	private JLabel settingsLabel;
    
    private JButton fullHotkeyButton;
    private JButton cropHotkeyButton;
    private JButton clipboardHotkeyButton;
    private JButton activeHotkeyButton;
	private JButton optionsHotkeyButton;
	
    private JButton hotkeyResetButton;
    private JButton hotkeySaveButton;

	public HotkeyPanel(OptionPanel parent) {
		this.parent = parent;
		this.configuration = parent.getSnapper().getConfiguration();
	}

	@Override
	public void initComponents() {
        fullHotkeyButton = new JButton();
        cropHotkeyButton = new JButton();
        clipboardHotkeyButton = new JButton();
        activeHotkeyButton = new JButton();
        optionsHotkeyButton = new JButton();

        hotkeySettingsLabel = new JLabel();
        fullscreenLabel = new JLabel();
        cropLabel = new JLabel();
        clipboardLabel = new JLabel();
        activeLabel = new JLabel();
        settingsLabel = new JLabel();
        noteLabel = new JLabel();
        
        hotkeyResetButton = new JButton();
        hotkeySaveButton = new JButton();

        hotkeySettingsLabel.setText("Hotkey settings (click the button to change)");
        
		fullscreenLabel.setText("Fullscreen shot:");

        fullHotkeyButton.setText("Not set");
        
        fullHotkeyButton.addKeyListener(new HotkeyChangeListener(fullHotkeyButton));

        cropLabel.setText("Crop shot:");

        cropHotkeyButton.setText("Not set");
        
        cropHotkeyButton.addKeyListener(new HotkeyChangeListener(cropHotkeyButton));

        clipboardLabel.setText("Clipboard upload:");

        clipboardHotkeyButton.setText("Not set");
        
        clipboardHotkeyButton.addKeyListener(new HotkeyChangeListener(clipboardHotkeyButton));

        activeLabel.setText("Active window:");

        activeHotkeyButton.setText("Not set");
        
        activeHotkeyButton.addKeyListener(new HotkeyChangeListener(activeHotkeyButton));

        settingsLabel.setText("Open settings:");

        optionsHotkeyButton.setText("Not set");
        
        optionsHotkeyButton.addKeyListener(new HotkeyChangeListener(optionsHotkeyButton));

        noteLabel.setText("Note: All hotkeys are temporarily disabled when you click a button");

        hotkeyResetButton.setText("Reset");
        
        hotkeyResetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadCurrentHotkeys();
				hotkeySaveButton.setEnabled(false);
				hotkeyResetButton.setEnabled(false);
			}
        });

        hotkeySaveButton.setText("Save");
        
        hotkeySaveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				savePreferences();
			}
        });
        
        hotkeyResetButton.setEnabled(false);
        hotkeySaveButton.setEnabled(false);

        javax.swing.GroupLayout hotkeyPanelLayout = new javax.swing.GroupLayout(this);
        this.setLayout(hotkeyPanelLayout);
        hotkeyPanelLayout.setHorizontalGroup(
            hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hotkeyPanelLayout.createSequentialGroup()
                .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(hotkeyPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(hotkeySettingsLabel))
                    .addGroup(hotkeyPanelLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cropLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fullscreenLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(clipboardLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(activeLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(settingsLabel, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(optionsHotkeyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(activeHotkeyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(clipboardHotkeyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cropHotkeyButton, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                            .addComponent(fullHotkeyButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(50, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, hotkeyPanelLayout.createSequentialGroup()
                .addContainerGap(313, Short.MAX_VALUE)
                .addComponent(hotkeySaveButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hotkeyResetButton)
                .addContainerGap())
            .addGroup(hotkeyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(noteLabel)
                .addContainerGap(137, Short.MAX_VALUE))
        );
        hotkeyPanelLayout.setVerticalGroup(
            hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hotkeyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hotkeySettingsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fullscreenLabel)
                    .addComponent(fullHotkeyButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cropLabel)
                    .addComponent(cropHotkeyButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clipboardLabel)
                    .addComponent(clipboardHotkeyButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(activeLabel)
                    .addComponent(activeHotkeyButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(settingsLabel)
                    .addComponent(optionsHotkeyButton))
                .addGap(40, 40, 40)
                .addComponent(noteLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 202, Short.MAX_VALUE)
                .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hotkeyResetButton)
                    .addComponent(hotkeySaveButton))
                .addContainerGap())
        );
	}
	
	public void loadCurrentHotkeys() {
		Map<String, String> keys = configuration.getMap("hotkeys");
		if(keys.containsKey("full")) {
			fullHotkeyButton.setText(getButtonText(keys.get("full")));
		} else {
			fullHotkeyButton.setText("Not set");
		}
		if(keys.containsKey("crop")) {
			cropHotkeyButton.setText(getButtonText(keys.get("crop")));
		} else {
			cropHotkeyButton.setText("Not set");
		}
		if(keys.containsKey("clipboard")) {
			clipboardHotkeyButton.setText(getButtonText(keys.get("clipboard")));
		} else {
			clipboardHotkeyButton.setText("Not set");
		}
		if(keys.containsKey("options")) {
			optionsHotkeyButton.setText(getButtonText(keys.get("options")));
		} else {
			optionsHotkeyButton.setText("Not set");
		}
		if(Platform.isWindows() || Platform.isLinux()) {
			if(keys.containsKey("active")) {
				activeHotkeyButton.setText(getButtonText(keys.get("active")));
			} else {
				activeHotkeyButton.setText("Not set");
			}
		} else {
			activeHotkeyButton.setText("Unavailable");
			activeHotkeyButton.setEnabled(false);
		}
	}
	
	public void savePreferences() {
		Map<String, String> keys = new HashMap<String, String>();
		String full = fullHotkeyButton.getText();
		if(!full.equals("Not set")) {
			keys.put("full", getFormattedKeyStroke(full));
		}
		String crop = cropHotkeyButton.getText();
		if(!crop.equals("Not set")) {
			keys.put("crop", getFormattedKeyStroke(crop));
		}
		String clipboard = clipboardHotkeyButton.getText();
		if(!clipboard.equals("Not set")) {
			keys.put("clipboard", getFormattedKeyStroke(clipboard));
		}
		String active = activeHotkeyButton.getText();
		if(!active.equals("Not set")) {
			keys.put("active", getFormattedKeyStroke(active));
		}
		String options = optionsHotkeyButton.getText();
		if(!options.equals("Not set")) {
			keys.put("options", getFormattedKeyStroke(options));
		}
		configuration.put("hotkeys", keys);
		try {
			configuration.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		hotkeySaveButton.setEnabled(false);
		hotkeyResetButton.setEnabled(false);
	}
	
	public class HotkeyChangeListener extends KeyAdapter {
		private JButton button;
		public HotkeyChangeListener(JButton button) {
			this.button = button;
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			if(parent.getSnapper().getKeyManager().hasKeysBound()) {
				parent.getSnapper().getKeyManager().resetKeys();
			}
			//If the key is unknown/a function key
			if(e.getKeyCode() == 0 || e.getKeyCode() == KeyEvent.VK_ALT || e.getKeyCode() == KeyEvent.VK_CONTROL || e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_META) {
				return;
			}
			button.setText(formatKeyStroke(KeyStroke.getKeyStrokeForEvent(e)));
			if(!hotkeyResetButton.isEnabled()) {
				hotkeyResetButton.setEnabled(true);
			}
			if(!hotkeySaveButton.isEnabled()) {
				hotkeySaveButton.setEnabled(true);
			}
		}
	}
	
	public static String formatKeyStroke(KeyStroke stroke) {
		if(stroke == null) {
			return "Not set";
		}
		String s = stroke.toString();
		s = s.replace("released ", "");
		s = s.replace("typed ", "");
		s = s.replace("pressed ", "");
		StringBuilder out = new StringBuilder();
		String[] split = s.split(" ");
		for(int i = 0; i < split.length; i++) {
			String str = split[i];
			if(str.contains("_")) {
				str = str.replace('_', ' ');
			}
			out.append(Util.ucwords(str));
			if(i != (split.length - 1)) {
				out.append(" + ");
			}
		}
		return out.toString();
	}
	
	public static String getFormattedKeyStroke(String s) {
		String[] split = s.split(" \\+ ");
		StringBuilder out = new StringBuilder();
		for(int i = 0; i < split.length; i++) {
			if(i == (split.length - 1)) {
				out.append(split[i].toUpperCase().replace(' ', '_'));
			} else {
				out.append(split[i].toLowerCase());
				out.append(" ");
			}
		}
		return out.toString();
	}
	
	private String getButtonText(String stroke) {
		return formatKeyStroke(KeyStroke.getKeyStroke(stroke));
	}
}
