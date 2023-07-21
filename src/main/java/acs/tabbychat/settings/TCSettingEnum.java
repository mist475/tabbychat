package acs.tabbychat.settings;

import acs.tabbychat.util.TabbyChatUtils;
import net.minecraft.client.Minecraft;

import java.util.Properties;

public class TCSettingEnum extends TCSetting<Enum<?>> {
    public TCSettingEnum(Enum<?> theSetting, String theProperty, String theCategory, int theID) {
        super(theSetting, theProperty, theCategory, theID);
        setCommon();
        this.width(30);
        this.height(11);
    }

    public TCSettingEnum(Enum<?> theSetting, String theProperty, String theCategory, int theID,
                         FormatCodeEnum theFormat) {
        super(theSetting, theProperty, theCategory, theID, theFormat);
        setCommon();
    }

    public void setCommon() {
        this.type = "enum";
    }

    @Override
    public void drawButton(Minecraft par1, int cursorX, int cursorY) {
        int centerX = this.x() + this.width() / 2;
        int fgcolor = 0x99a0a0a0;
        if (!this.enabled) {
            fgcolor = -0x995f5f60;
        }
        else if (this.hovered(cursorX, cursorY)) {
            fgcolor = 0x99ffffa0;
        }

        int labelColor = (this.enabled) ? 0xffffff : 0x666666;

        drawRect(this.x() + 1, this.y(), this.x() + this.width() - 1, this.y() + 1, fgcolor);
        drawRect(this.x() + 1, this.y() + this.height() - 1, this.x() + this.width() - 1, this.y()
            + this.height(), fgcolor);
        drawRect(this.x(), this.y() + 1, this.x() + 1, this.y() + this.height() - 1, fgcolor);
        drawRect(this.x() + this.width() - 1, this.y() + 1, this.x() + this.width(), this.y()
            + this.height() - 1, fgcolor);
        drawRect(this.x() + 1, this.y() + 1, this.x() + this.width() - 1, this.y() + this.height()
            - 1, 0xff000000);

        this.drawCenteredString(mc.fontRenderer, this.tempValue.toString(), centerX, this.y() + 2,
                                labelColor);

        this.drawCenteredString(mc.fontRenderer, this.description,
                                this.labelX + mc.fontRenderer.getStringWidth(this.description) / 2, this.y()
                                    + (this.height() - 6) / 2, labelColor);
    }

    @Override
    public TCSettingType getType() {
        return TCSettingType.ENUM;
    }

    @Override
    public void loadSelfFromProps(Properties readProps) {
        String found = (String) readProps.get(this.propertyName);
        if (found == null) {
            this.clear();
            return;
        }
        if (this.propertyName.contains("Color")) {
            this.value = TabbyChatUtils.parseColor(found);
        }
        else if (this.propertyName.contains("Format")) {
            this.value = TabbyChatUtils.parseFormat(found);
        }
        else if (this.propertyName.contains("Sound")) {
            this.value = TabbyChatUtils.parseSound(found);
        }
        else if (this.propertyName.contains("delim")) {
            this.value = TabbyChatUtils.parseDelimiters(found);
        }
        else if (this.propertyName.contains("Stamp")) {
            this.value = TabbyChatUtils.parseTimestamp(found);
        }
    }

    @Override
    public void mouseClicked(int par1, int par2, int par3) {
        if (this.hovered(par1, par2) && this.enabled) {
            if (par3 == 1)
                this.previous();
            else if (par3 == 0)
                this.next();
        }
    }

    @SuppressWarnings("unchecked")
    public void next() {
        Enum<?> eCast = this.tempValue;
        Enum<?>[] E = eCast.getClass().getEnumConstants();
        Enum<?> tmp;
        if (eCast.ordinal() == E.length - 1)
            tmp = Enum.valueOf(eCast.getClass(), E[0].name());
        else {
            tmp = Enum.valueOf(eCast.getClass(), E[eCast.ordinal() + 1].name());
        }
        this.tempValue = tmp;
    }

    @SuppressWarnings("unchecked")
    public void previous() {
        Enum<?> eCast = this.tempValue;
        Enum<?>[] E = eCast.getClass().getEnumConstants();
        if (eCast.ordinal() == 0)
            this.tempValue = Enum.valueOf(eCast.getClass(), E[E.length - 1].name());
        else {
            this.tempValue = Enum.valueOf(eCast.getClass(), E[eCast.ordinal() - 1].name());
        }
    }

    public void setTempValueFromProps(Properties readProps) {
        String found = (String) readProps.get(this.propertyName);
        if (found == null) {
            this.tempValue = this.theDefault;
            return;
        }
        if (this.propertyName.contains("Color")) {
            this.tempValue = TabbyChatUtils.parseColor(found);
        }
        else if (this.propertyName.contains("Format")) {
            this.tempValue = TabbyChatUtils.parseFormat(found);
        }
        else if (this.propertyName.contains("Sound")) {
            this.tempValue = TabbyChatUtils.parseSound(found);
        }
        else if (this.propertyName.contains("delim")) {
            this.tempValue = TabbyChatUtils.parseDelimiters(found);
        }
        else if (this.propertyName.contains("Stamp")) {
            this.tempValue = TabbyChatUtils.parseTimestamp(found);
        }
    }

    @Override
    public void saveSelfToProps(Properties writeProps) {
        writeProps.put(this.propertyName, this.value.name());
    }
}
