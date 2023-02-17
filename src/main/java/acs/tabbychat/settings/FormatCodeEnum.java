package acs.tabbychat.settings;

import net.minecraft.client.resources.I18n;

public enum FormatCodeEnum {
    DEFAULT(I18n.format("formats.default"), ""),
    BOLD(I18n.format("formats.bold"), "\u00A7l"),
    STRIKED(I18n.format("formats.striked"), "\u00A7m"),
    UNDERLINE(I18n.format("formats.underline"), "\u00A7n"),
    ITALIC(I18n.format("formats.italic"), "\u00A7o"),
    MAGIC(I18n.format("formats.magic"), "\u00A7k");

    private final String title;
    private final String code;

    FormatCodeEnum(String _name, String _code) {
        this.title = _name;
        this.code = _code;
    }

    public static FormatCodeEnum cleanValueOf(String name) {
        try {
            return FormatCodeEnum.valueOf(name);
        }
        catch (Exception e) {
            return FormatCodeEnum.DEFAULT;
        }
    }

    @Override
    public String toString() {
        return this.code + this.title + "\u00A7r";
    }

    public String toCode() {
        return this.code;
    }

    public String color() {
        return this.title;
    }
}
