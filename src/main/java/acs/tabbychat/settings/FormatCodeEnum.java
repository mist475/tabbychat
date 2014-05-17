package acs.tabbychat.settings;

import net.minecraft.client.resources.I18n;

public enum FormatCodeEnum {
	DEFAULT(I18n.format("formats.default"), ""),
	BOLD(I18n.format("formats.bold"), "\u00A7l"),
	STRIKED(I18n.format("formats.striked"), "\u00A7m"),
	UNDERLINE(I18n.format("formats.underline"), "\u00A7n"),
	ITALIC(I18n.format("formats.italic"), "\u00A7o");
	
	private String title;
	private String code;
	
	private FormatCodeEnum(String _name, String _code) {
		this.title = _name;
		this.code = _code;
	}
	
	public String toString() {
		return this.code + this.title + "\u00A7r";
	}
	
	public String toCode() {
		return this.code;
	}
	
	public String color() {
		return this.title;
	}
	
	public static FormatCodeEnum cleanValueOf(String name) {
		try {
			return FormatCodeEnum.valueOf(name);
		} catch (Exception e) {
			return FormatCodeEnum.DEFAULT;
		}
	}
}
