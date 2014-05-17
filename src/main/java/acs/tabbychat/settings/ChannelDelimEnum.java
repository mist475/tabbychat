package acs.tabbychat.settings;

import net.minecraft.client.resources.I18n;

public enum ChannelDelimEnum {
	ANGLES(I18n.format("delims.angles"), "<", ">"),
	BRACES(I18n.format("delims.braces"), "{", "}"),
	BRACKETS(I18n.format("delims.brackets"), "[", "]"),
	PARENTHESIS(I18n.format("delims.parenthesis"), "(", ")"),
	ANGLESPARENSCOMBO(I18n.format("delims.anglesparenscombo"), "<\\(", ")(?: |\u00A7r)?[A-Za-z0-9_]{1,16}>"),
	ANGLESBRACKETSCOMBO(I18n.format("delims.anglesbracketscombo"), "<\\[", "](?: |\u00A7r)?[A-Za-z0-9_]{1,16}>");
	
	private String title;
	private String open;
	private String close;
	
	private ChannelDelimEnum(String title, String open, String close) {
		this.title = title;
		this.open = open;
		this.close = close;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String toString() {
		return this.title;
	}
	
	public void setValue(String _title) {
		for (ChannelDelimEnum tmp : ChannelDelimEnum.values()) {
			if (_title.equals(tmp.title)) {
				this.title = tmp.title;
				this.open = tmp.open;
				this.close = tmp.close;
				break;
			}
		}
	}
	
	public String open() {
		return this.open;
	}
	
	public String close() {
		return this.close;
	}
}
