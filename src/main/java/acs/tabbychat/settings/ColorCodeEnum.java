package acs.tabbychat.settings;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

public enum ColorCodeEnum {
    DEFAULT(I18n.format("colors.default"), "", null),
    BLACK(I18n.format("colors.black"), "\u00A70", EnumChatFormatting.BLACK),
    DARKBLUE(I18n.format("colors.darkblue"), "\u00A71", EnumChatFormatting.DARK_BLUE),
    DARKGREEN(I18n.format("colors.darkgreen"), "\u00A72", EnumChatFormatting.DARK_GREEN),
    DARKAQUA(I18n.format("colors.darkaqua"), "\u00A73", EnumChatFormatting.DARK_AQUA),
    DARKRED(I18n.format("colors.darkred"), "\u00A74", EnumChatFormatting.DARK_RED),
    PURPLE(I18n.format("colors.purple"), "\u00A75", EnumChatFormatting.DARK_PURPLE),
    GOLD(I18n.format("colors.gold"), "\u00A76", EnumChatFormatting.GOLD),
    GRAY(I18n.format("colors.gray"), "\u00A77", EnumChatFormatting.GRAY),
    DARKGRAY(I18n.format("colors.darkgray"), "\u00A78", EnumChatFormatting.DARK_GRAY),
    INDIGO(I18n.format("colors.indigo"), "\u00A79", EnumChatFormatting.BLUE),
    BRIGHTGREEN(I18n.format("colors.brightgreen"), "\u00A7a", EnumChatFormatting.GREEN),
    AQUA(I18n.format("colors.aqua"), "\u00A7b", EnumChatFormatting.AQUA),
    RED(I18n.format("colors.red"), "\u00A7c", EnumChatFormatting.RED),
    PINK(I18n.format("colors.pink"), "\u00A7d", EnumChatFormatting.LIGHT_PURPLE),
    YELLOW(I18n.format("colors.yellow"), "\u00A7e", EnumChatFormatting.YELLOW),
    WHITE(I18n.format("colors.white"), "\u00A7f", EnumChatFormatting.WHITE);

    private final String title;
    private final String code;
    private final EnumChatFormatting vanilla;

    ColorCodeEnum(String _name, String _code, EnumChatFormatting _vanilla) {
        this.title = _name;
        this.code = _code;
        this.vanilla = _vanilla;
    }

    public static ColorCodeEnum cleanValueOf(String name) {
        try {
            return ColorCodeEnum.valueOf(name);
        }
        catch (Exception e) {
            return ColorCodeEnum.YELLOW;
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

    public EnumChatFormatting toVanilla() {
        return this.vanilla;
    }

}
