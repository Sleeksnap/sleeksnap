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
package org.sleeksnap.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A VERY SIMPLE Linux Desktop file builder
 * 
 * @author Nikki
 * @link http://standards.freedesktop.org/desktop-entry-spec/desktop-entry-spec-latest.html
 *
 */
public class DesktopEntryBuilder {
	
	/**
	 * The groups in this desktop file
	 */
	private List<DesktopEntryGroup> groups = new LinkedList<DesktopEntryGroup>();
	
	/**
	 * The main group. Files MUST have this, even if it is empty.
	 */
	private DesktopEntryGroup currentGroup = new DesktopEntryGroup("Desktop Entry");

	/**
	 * Construct an empty builder
	 */
	public DesktopEntryBuilder() {
		
	}
	
	/**
	 * Start a desktop entry group
	 * @param groupName
	 * 			The group name to start
	 * @return
	 * 			The current instance, for chaining.
	 */
	public DesktopEntryBuilder startGroup(String groupName) {
		if(currentGroup != null) {
			endGroup();
		}
		currentGroup = new DesktopEntryGroup(groupName);
		return this;
	}
	
	/**
	 * Add an entry to the current group.
	 * @param key
	 * 			The entry key
	 * @param value
	 * 			The entry value
	 * @return
	 * 			The current instance, for chaining.
	 */
	public DesktopEntryBuilder addEntry(String key, Object value) {
		if(currentGroup == null) {
			throw new NullPointerException("No group started.");
		}
		currentGroup.entries.put(key, value);
		return this;
	}
	
	/**
	 * Add a comment before the next entry or group
	 * @param comment
	 * 			The comment to add
	 * @return
	 * 			The current instance, for chaining.
	 */
	public DesktopEntryBuilder addComment(String comment) {
		if(currentGroup == null) {
			throw new NullPointerException("No group started.");
		}
		currentGroup.entries.put("#", comment);
		return this;
	}
	
	/**
	 * End the current group, this is automatically done on startGroup and build
	 * @return
	 * 			The current instance, for chaining.
	 */
	public DesktopEntryBuilder endGroup() {
		groups.add(currentGroup);
		currentGroup = null;
		return this;
	}
	
	/**
	 * Build the Autostart file
	 * @return
	 * 			The completed file
	 */
	public String build() {
		if(currentGroup != null) {
			endGroup();
		}
		StringBuilder sb = new StringBuilder();
		
		for(DesktopEntryGroup group : groups) {
			sb.append('[').append(group.groupName).append(']').append('\n');
			for(Entry<String, Object> entry : group.entries.entrySet()) {
				sb.append(entry.getKey()).append(entry.getKey().equals("#") ? ' ' : '=');
				if(entry.getValue().getClass().isArray()) {
					Object[] arr = (Object[]) entry.getValue();
					for(int i = 0; i < arr.length; i++) {
						sb.append(arr[i].toString().replace(";", "\\;")).append(';');
					}
				} else {
					sb.append(entry.getValue().toString());
				}
				sb.append('\n');
			}
		}
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return build();
	}
	
	/**
	 * A holding class for desktop groups
	 * 
	 * @author Nikki
	 *
	 */
	public class DesktopEntryGroup {
		
		private String groupName;
		
		private Map<String, Object> entries = new LinkedHashMap<String, Object>();
		
		public DesktopEntryGroup(String groupName) {
			this.groupName = groupName;
		}
		
	}
}
