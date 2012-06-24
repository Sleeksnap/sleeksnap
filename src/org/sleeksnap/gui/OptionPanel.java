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
package org.sleeksnap.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.sleeksnap.Configuration;
import org.sleeksnap.Constants;
import org.sleeksnap.ScreenSnapper;
import org.sleeksnap.impl.History;
import org.sleeksnap.impl.HistoryEntry;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.StreamUtils;
import org.sleeksnap.util.Util;
import org.sleeksnap.util.Utils.ClipboardUtil;
import org.sleeksnap.util.Utils.FileUtils;
import org.sleeksnap.util.Utils.SortingUtil;
import org.sleeksnap.util.WinRegistry;

import com.sun.jna.Platform;

/**
 * 
 * @author Nikki
 */
@SuppressWarnings({"serial", "unchecked", "rawtypes"})
public class OptionPanel extends JPanel {

	public class HotkeyChangeListener extends KeyAdapter {
		private JButton button;
		public HotkeyChangeListener(JButton button) {
			this.button = button;
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			if(snapper.getKeyManager().hasKeysBound()) {
				snapper.getKeyManager().resetKeys();
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
	private class UploaderWrapper {
		private Uploader<?> uploader;
		
		public UploaderWrapper(Uploader<?> uploader) {
			this.uploader = uploader;
		}
		
		public Uploader<?> getUploader() {
			return uploader;
		}
		
		public String toString() {
			return uploader.getName();
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
	private DefaultComboBoxModel imageModel;

	private DefaultComboBoxModel textModel;

	private DefaultComboBoxModel fileModel;
	
	private DefaultComboBoxModel urlModel;
	
	private ScreenSnapper snapper;
	
	private JCheckBox automaticUpload;
	
	private JButton browseButton;

	private JComboBox fileUploader;

	private JButton historyCopy;

	private JList historyList;

	private JButton historyOpen;

	private JPanel historyPanel;

	private JButton historySelect;
	private JComboBox imageUploader;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JLabel jLabel5;
	private JScrollPane jScrollPane1;
	private JTabbedPane jTabbedPane1;
	private JTextField linkField;
	private JLabel logoLabel;
	private JPanel mainPanel;
	private JButton saveButton;
	private JCheckBox shortenURLs;
	private JComboBox textUploader;
	
    private JPanel uploaderPanel;
    private JComboBox urlShortener;
    private JLabel versionLabel;
    private JPanel hotkeyPanel;
    private JButton fullHotkeyButton;
    private JButton cropHotkeyButton;
    private JButton clipboardHotkeyButton;
    private JButton activeHotkeyButton;
    private JButton hotkeyResetButton;
    private JButton hotkeySaveButton;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
	private JScrollPane jScrollPane2;
	private JTextArea logArea;
	private JPanel logPanel;
	private JButton optionsHotkeyButton;
	
	private JLabel jLabel12;
	
	private JCheckBox startOnStartup;
	private JButton saveAllButton;
	private JCheckBox localCopyCheckbox;
	private int previousTab = 0;
	
	private Configuration configuration;

	public OptionPanel(ScreenSnapper snapper) {
		this.snapper = snapper;
		this.configuration = snapper.getConfiguration();
		initComponents();
	}
	private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			Desktop.getDesktop().open(
					new File(Util.getWorkingDirectory(), "uploaders"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void doneBuilding() {
		if(configuration.contains("startOnStartup")) {
			startOnStartup.setSelected(configuration.getBoolean("startOnStartup"));
		}
		saveButton.setEnabled(false);
	}
	
	private String getButtonText(String stroke) {
		return formatKeyStroke(KeyStroke.getKeyStroke(stroke));
	}
	
	private void historyCopyActionPerformed(java.awt.event.ActionEvent evt) {
		String text = linkField.getText();
		if(!text.equals("")) {
			ClipboardUtil.setClipboard(text);
		}
	}
	
	private void historyOpenActionPerformed(java.awt.event.ActionEvent evt) {
		String text = linkField.getText();
		if(!text.equals("")) {
			try {
				Desktop.getDesktop().browse(new URL(text).toURI());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void historySelectActionPerformed(java.awt.event.ActionEvent evt) {
		String text = linkField.getText();
		if(!text.equals("")) {
			linkField.select(0, text.length());
		}
	}

	private void hotkeySaveButtonActionPerformed(java.awt.event.ActionEvent evt) {
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
	private void initComponents() {

		jTabbedPane1 = new JTabbedPane();
        mainPanel = new JPanel();
        logoLabel = new JLabel();
        versionLabel = new JLabel();
        uploaderPanel = new JPanel();
        jLabel1 = new JLabel();
        imageUploader = new JComboBox();
        jLabel2 = new JLabel();
        textUploader = new JComboBox();
        jLabel3 = new JLabel();
        fileUploader = new JComboBox();
        saveButton = new JButton();
        browseButton = new JButton();
        automaticUpload = new JCheckBox();
        shortenURLs = new JCheckBox();
        jLabel5 = new JLabel();
        urlShortener = new JComboBox();
        localCopyCheckbox = new JCheckBox();
        hotkeyPanel = new JPanel();
        jLabel6 = new JLabel();
        fullHotkeyButton = new JButton();
        jLabel7 = new JLabel();
        jLabel8 = new JLabel();
        cropHotkeyButton = new JButton();
        jLabel9 = new JLabel();
        clipboardHotkeyButton = new JButton();
        jLabel10 = new JLabel();
        activeHotkeyButton = new JButton();
        jLabel11 = new JLabel();
        jLabel12 = new JLabel();
        hotkeyResetButton = new JButton();
        hotkeySaveButton = new JButton();
        optionsHotkeyButton = new JButton();
        historyPanel = new JPanel();
        jScrollPane1 = new JScrollPane();
        historyList = new JList();
        jLabel4 = new JLabel();
        linkField = new JTextField();
        historyOpen = new JButton();
        historyCopy = new JButton();
        historySelect = new JButton();
        logPanel = new JPanel();
        jScrollPane2 = new JScrollPane();
        logArea = new JTextArea();
        startOnStartup = new javax.swing.JCheckBox();
        saveAllButton = new javax.swing.JButton();

		imageModel = new DefaultComboBoxModel();
		textModel = new DefaultComboBoxModel();
		fileModel = new DefaultComboBoxModel();
		urlModel = new DefaultComboBoxModel();
		
		ActionListener changeListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!saveButton.isEnabled()) {
					saveButton.setEnabled(true);
				}
			}
		};
		
		imageUploader.addActionListener(changeListener);
		textUploader.addActionListener(changeListener);
		fileUploader.addActionListener(changeListener);
		shortenURLs.addActionListener(changeListener);
		automaticUpload.addActionListener(changeListener);
		localCopyCheckbox.addActionListener(changeListener);
		urlShortener.addActionListener(changeListener);

		setMinimumSize(new java.awt.Dimension(500, 300));

		jTabbedPane1.setTabPlacement(JTabbedPane.LEFT);
		jTabbedPane1.setCursor(new java.awt.Cursor(
				java.awt.Cursor.DEFAULT_CURSOR));

		mainPanel.setPreferredSize(new java.awt.Dimension(300, 442));

		logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		logoLabel.setIcon(new ImageIcon(Util.getResourceByName(Constants.Resources.LOGO_PATH))); // NOI18N

		versionLabel.setText("Version "+Constants.Application.VERSION);

        startOnStartup.setText("Start Sleeksnap on startup (Windows only)");

        saveAllButton.setText("Save all");
        
        saveAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Save startup options here... Windows we can add it to the registry, Linux.... dunno
				boolean start = startOnStartup.isSelected();
				if(start) {
					//Overwrite the old key, in case it's a new version.
					if(Platform.isWindows()) {
						try {
							WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, WinRegistry.RUN_PATH, Constants.Application.NAME, FileUtils.getJarPath(OptionPanel.class));
						} catch (Exception e1) {
							//TODO problem, we couldn't add it!
						}
					}
				} else {
					if(Platform.isWindows()) {
						try {
							WinRegistry.deleteValue(WinRegistry.HKEY_CURRENT_USER, WinRegistry.RUN_PATH, Constants.Application.NAME);
						} catch (Exception e1) {
							//Doesn't exist, no problem.
						}
					}
				}
				configuration.put("startOnStartup", start);
				try {
					configuration.save();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				//Save everything else
				hotkeySaveButtonActionPerformed(e);
				saveButtonActionPerformed(e);
			}
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(versionLabel)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(startOnStartup)
                        .addComponent(logoLabel)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap(352, Short.MAX_VALUE)
                .addComponent(saveAllButton)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(versionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(startOnStartup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 270, Short.MAX_VALUE)
                .addComponent(saveAllButton)
                .addContainerGap())
        );

		jTabbedPane1.addTab("Main", mainPanel);

		jLabel1.setText("Images");

		imageUploader.setModel(imageModel);

		jLabel2.setText("Text");

		textUploader.setModel(textModel);

		jLabel3.setText("Files");

		fileUploader.setModel(fileModel);

		saveButton.setText("Save");
		saveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveButtonActionPerformed(evt);
			}
		});

		browseButton.setText("Browse Directory");
		browseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				browseButtonActionPerformed(evt);
			}
		});

		automaticUpload.setText("Upload text files to text uploader");
		if(configuration.contains("plainTextUpload")) {
			automaticUpload.setSelected(configuration.getBoolean("plainTextUpload"));
		}

		shortenURLs.setText("Automatically shorten URLs");
		if(configuration.contains("shortenurls")) {
			shortenURLs.setSelected(configuration.getBoolean("shortenurls"));
		}
		
        localCopyCheckbox.setText("Keep a local copy of uploaded images");
        if(configuration.contains("savelocal")) {
        	localCopyCheckbox.setSelected(configuration.getBoolean("savelocal"));
        }
        
		jLabel5.setText("URLs");

		urlShortener.setModel(urlModel);

        javax.swing.GroupLayout uploaderPanelLayout = new javax.swing.GroupLayout(uploaderPanel);
        uploaderPanel.setLayout(uploaderPanelLayout);
        uploaderPanelLayout.setHorizontalGroup(
            uploaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uploaderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(uploaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(localCopyCheckbox)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, uploaderPanelLayout.createSequentialGroup()
                        .addComponent(browseButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 226, Short.MAX_VALUE)
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(uploaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(uploaderPanelLayout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(urlShortener, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(uploaderPanelLayout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(imageUploader, 0, 198, Short.MAX_VALUE))
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(uploaderPanelLayout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(fileUploader, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(uploaderPanelLayout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(textUploader, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel5)
                    .addComponent(shortenURLs)
                    .addComponent(automaticUpload))
                .addContainerGap())
        );
        uploaderPanelLayout.setVerticalGroup(
            uploaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uploaderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imageUploader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textUploader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fileUploader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(urlShortener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(shortenURLs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(automaticUpload)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(localCopyCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 140, Short.MAX_VALUE)
                .addGroup(uploaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(browseButton))
                .addContainerGap())
        );

		jTabbedPane1.addTab("Uploaders", uploaderPanel);
		
		jLabel6.setText("Fullscreen shot:");

        fullHotkeyButton.setText("Not set");
        
        fullHotkeyButton.addKeyListener(new HotkeyChangeListener(fullHotkeyButton));

        jLabel7.setText("Hotkey settings (click the button to change)");

        jLabel8.setText("Crop shot:");

        cropHotkeyButton.setText("Not set");
        
        cropHotkeyButton.addKeyListener(new HotkeyChangeListener(cropHotkeyButton));

        jLabel9.setText("Clipboard upload:");

        clipboardHotkeyButton.setText("Not set");
        
        clipboardHotkeyButton.addKeyListener(new HotkeyChangeListener(clipboardHotkeyButton));

        jLabel10.setText("Active window:");

        activeHotkeyButton.setText("Not set");
        
        activeHotkeyButton.addKeyListener(new HotkeyChangeListener(activeHotkeyButton));

        jLabel12.setText("Open settings:");

        optionsHotkeyButton.setText("Not set");
        
        optionsHotkeyButton.addKeyListener(new HotkeyChangeListener(optionsHotkeyButton));

        jLabel11.setText("Note: All hotkeys are temporarily disabled when you click a button");

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
				hotkeySaveButtonActionPerformed(e);
			}
        });
        
        hotkeyResetButton.setEnabled(false);
        hotkeySaveButton.setEnabled(false);

        GroupLayout hotkeyPanelLayout = new javax.swing.GroupLayout(hotkeyPanel);
        hotkeyPanel.setLayout(hotkeyPanelLayout);
        hotkeyPanelLayout.setHorizontalGroup(
            hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hotkeyPanelLayout.createSequentialGroup()
                .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(hotkeyPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7))
                    .addGroup(hotkeyPanelLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(hotkeyPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel8, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(hotkeyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(optionsHotkeyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(activeHotkeyButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(clipboardHotkeyButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fullHotkeyButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cropHotkeyButton, GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE))))
                .addContainerGap(119, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.TRAILING, hotkeyPanelLayout.createSequentialGroup()
                .addContainerGap(297, Short.MAX_VALUE)
                .addComponent(hotkeySaveButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hotkeyResetButton)
                .addContainerGap())
            .addGroup(hotkeyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addContainerGap(121, Short.MAX_VALUE))
        );
        hotkeyPanelLayout.setVerticalGroup(
            hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hotkeyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(fullHotkeyButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cropHotkeyButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(clipboardHotkeyButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(activeHotkeyButton))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(optionsHotkeyButton))
                    .addGap(40, 40, 40)
                    .addComponent(jLabel11)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 211, Short.MAX_VALUE)
                .addGroup(hotkeyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hotkeyResetButton)
                    .addComponent(hotkeySaveButton))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Hotkeys", hotkeyPanel);

		historyList
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		historyList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()) {
					Object value = historyList.getSelectedValue();
					if(value != null) {
						HistoryEntry entry = (HistoryEntry) value;
						linkField.setText(entry.getUrl());
					}
				}
			}
		});
		
		jScrollPane1.setViewportView(historyList);

		jLabel4.setText("Link:");

		linkField.setEditable(false);

		historyOpen.setText("Open");
		historyOpen.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				historyOpenActionPerformed(evt);
			}
		});

		historyCopy.setText("Copy");
		historyCopy.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				historyCopyActionPerformed(evt);
			}
		});

		historySelect.setText("Select");
		historySelect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				historySelectActionPerformed(evt);
			}
		});

		GroupLayout historyPanelLayout = new GroupLayout(
				historyPanel);
		historyPanel.setLayout(historyPanelLayout);
		historyPanelLayout
				.setHorizontalGroup(historyPanelLayout
						.createParallelGroup(
								GroupLayout.Alignment.LEADING)
						.addGroup(
								historyPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												historyPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																jScrollPane1,
																GroupLayout.DEFAULT_SIZE,
																411,
																Short.MAX_VALUE)
														.addGroup(
																historyPanelLayout
																		.createParallelGroup(
																				GroupLayout.Alignment.LEADING,
																				false)
																		.addGroup(
																				historyPanelLayout
																						.createSequentialGroup()
																						.addComponent(
																								historyOpen)
																						.addPreferredGap(
																								LayoutStyle.ComponentPlacement.RELATED,
																								GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								historySelect)
																						.addPreferredGap(
																								LayoutStyle.ComponentPlacement.RELATED)
																						.addComponent(
																								historyCopy))
																		.addComponent(
																				linkField,
																				GroupLayout.PREFERRED_SIZE,
																				188,
																				GroupLayout.PREFERRED_SIZE)
																		.addComponent(
																				jLabel4)))
										.addContainerGap()));
		historyPanelLayout
				.setVerticalGroup(historyPanelLayout
						.createParallelGroup(
								GroupLayout.Alignment.LEADING)
						.addGroup(
								historyPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel4)
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												linkField,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												historyPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																historyOpen)
														.addComponent(
																historyCopy)
														.addComponent(
																historySelect))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												jScrollPane1,
												GroupLayout.DEFAULT_SIZE,
												363, Short.MAX_VALUE)
										.addContainerGap()));

		jTabbedPane1.addTab("History", historyPanel);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(
				jTabbedPane1, GroupLayout.PREFERRED_SIZE, 500,
				GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(
				jTabbedPane1, GroupLayout.PREFERRED_SIZE, 470,
				GroupLayout.PREFERRED_SIZE));

        logArea.setColumns(20);
        logArea.setRows(5);
        jScrollPane2.setViewportView(logArea);

        javax.swing.GroupLayout logPanelLayout = new javax.swing.GroupLayout(logPanel);
        logPanel.setLayout(logPanelLayout);
        logPanelLayout.setHorizontalGroup(
            logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
        );
        logPanelLayout.setVerticalGroup(
            logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Log", logPanel);
		
		jTabbedPane1.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int index = jTabbedPane1.getSelectedIndex();
				if(index == 4) {
					//Load the log
					File file = new File(Util.getWorkingDirectory(), "log.txt");
					try {
						String contents = StreamUtils.readContents(new FileInputStream(file));
						logArea.setText(contents);
						logArea.setCaretPosition(contents.length());
					} catch(IOException ex) {
						ex.printStackTrace();
					}
				}
				if(previousTab == 2) {
					Logger.getAnonymousLogger().info("Tab changed from hotkey settings, attempting to rebind..");
					//Restart our input manager if we disabled it
					if(!snapper.getKeyManager().hasKeysBound()) {
						snapper.getKeyManager().initializeInput();
					}
				}
				previousTab = index;
			}
		});
		
		//Do any loading/initializing
		loadCurrentHotkeys();
	}
	
	private void loadCurrentHotkeys() {
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
	
	private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if(imageUploader.getSelectedItem() != null) {
			Uploader<?> imageSelection = ((UploaderWrapper) imageUploader.getSelectedItem()).getUploader();
			if(imageSelection != snapper.getUploaderFor(BufferedImage.class)) {
				snapper.setDefaultUploader(imageSelection, true);
			}
		}
		if(textUploader.getSelectedItem() != null) {
			Uploader<?> textSelection = ((UploaderWrapper) textUploader.getSelectedItem()).getUploader();
			if(textSelection != snapper.getUploaderFor(String.class)) {
				snapper.setDefaultUploader(textSelection, true);
			}
		}
		if(fileUploader.getSelectedItem() != null) {
			Uploader<?> fileSelection = ((UploaderWrapper) fileUploader.getSelectedItem()).getUploader();
			if(fileSelection != snapper.getUploaderFor(File.class)) {
				snapper.setDefaultUploader(fileSelection, true);
			}
		}
		if(urlShortener.getSelectedItem() != null) {
			Uploader<?> urlSelection = ((UploaderWrapper) urlShortener.getSelectedItem()).getUploader();
			if(urlSelection != snapper.getUploaderFor(URL.class)) {
				snapper.setDefaultUploader(urlSelection, true);
			}
		}
		HashMap<String, String> uploaders = new HashMap<String, String>();
		for(Entry<Class<?>, Uploader<?>> entry : snapper.getUploaderAssociations().entrySet()) {
			if(entry.getValue() == null) {
				continue;
			}
			uploaders.put(entry.getKey().getName(), entry.getValue().getClass().getName());
		}
		Configuration config = snapper.getConfiguration();
		config.put("uploaders", uploaders);
		config.put("shortenurls", shortenURLs.isSelected());
		config.put("plainTextUpload", automaticUpload.isSelected());
		config.put("savelocal", localCopyCheckbox.isSelected());
		try {
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveButton.setEnabled(false);
	}

	public void setHistory(History history) {
		DefaultListModel model = new DefaultListModel();
		List<HistoryEntry> list = history.getHistory();  
		for(int i = list.size()-1; i >= 0; i--) {
			model.addElement(list.get(i));
		}
		historyList.setModel(model);
	}
	
	public void setImageUploaders(Collection<Uploader<?>> uploaders) {
		Uploader<?> basic = snapper.getUploaderFor(java.awt.image.BufferedImage.class);
		for(Uploader<?> uploader : SortingUtil.sortUploaders(uploaders)) {
			UploaderWrapper wrapper = new UploaderWrapper(uploader);
			imageModel.addElement(wrapper);
			if(basic == uploader) {
				imageModel.setSelectedItem(wrapper);
			}
		}
	}
	
	public void setTextUploaders(Collection<Uploader<?>> uploaders) {
		Uploader<?> basic = snapper.getUploaderFor(String.class);
		for(Uploader<?> uploader : SortingUtil.sortUploaders(uploaders)) {
			UploaderWrapper wrapper = new UploaderWrapper(uploader);
			textModel.addElement(wrapper);
			if(basic == uploader) {
				textModel.setSelectedItem(wrapper);
			}
		}
	}
	
	public void setURLUploaders(Collection<Uploader<?>> uploaders) {
		Uploader<?> basic = snapper.getUploaderFor(URL.class);
		for(Uploader<?> uploader : SortingUtil.sortUploaders(uploaders)) {
			UploaderWrapper wrapper = new UploaderWrapper(uploader);
			urlModel.addElement(wrapper);
			if(basic == uploader) {
				urlModel.setSelectedItem(wrapper);
			}
		}
	}
	
	public void setFileUploaders(Collection<Uploader<?>> uploaders) {
		Uploader<?> basic = snapper.getUploaderFor(File.class);
		for(Uploader<?> uploader : SortingUtil.sortUploaders(uploaders)) {
			UploaderWrapper wrapper = new UploaderWrapper(uploader);
			fileModel.addElement(wrapper);
			if(basic == uploader) {
				fileModel.setSelectedItem(wrapper);
			}
		}
	}
}
