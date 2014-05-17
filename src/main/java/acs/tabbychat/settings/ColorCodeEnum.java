package acs.tabbychat.settings;

import net.minecraft.client.resources.I18n;

public enum ColorCodeEnum {
	DEFAULT(I18n.format("colors.default"), ""),
	DARKBLUE(I18n.format("colors.darkblue"), "\u00A71"),
	DARKGREEN(I18n.format("colors.darkgreen"), "\u00A72"),
	DARKAQUA(I18n.format("colors.darkaqua"), "\u00A73"),
	DARKRED(I18n.format("colors.darkred"), "\u00A74"),
	PURPLE(I18n.format("colors.purple"), "\u00A75"),
	GOLD(I18n.format("colors.gold"), "\u00A76"),
	GRAY(I18n.format("colors.gray"), "\u00A77"),
	DARKGRAY(I18n.format("colors.darkgray"), "\u00A78"),
	INDIGO(I18n.format("colors.indigo"), "\u00A79"),
	BRIGHTGREEN(I18n.format("colors.brightgreen"), "\u00A7a"),
	AQUA(I18n.format("colors.aqua"), "\u00A7b"),
	RED(I18n.format("colors.red"), "\u00A7c"),
	PINK(I18n.format("colors.pink"), "\u00A7d"),
	YELLOW(I18n.format("colors.yellow"), "\u00A7e"),
	WHITE(I18n.format("colors.white"), "\u00A7f");
	
	
	private String title;
	private String code;
	
	private ColorCodeEnum(String _name, String _code) {
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

	public static ColorCodeEnum cleanValueOf(String name) {
		try {
			return ColorCodeEnum.valueOf(name);
		} catch (Exception e) {
			return ColorCodeEnum.YELLOW;
		}
	}
}
