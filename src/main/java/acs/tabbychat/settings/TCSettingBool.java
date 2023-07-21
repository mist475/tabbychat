package acs.tabbychat.settings;

import net.minecraft.client.Minecraft;

import java.util.Properties;

public class TCSettingBool extends TCSetting<Boolean> {

    public TCSettingBool(Boolean theSetting, String theProperty, String theCategory, int theID) {
        super(theSetting, theProperty, theCategory, theID);
        setCommon();
    }

    public TCSettingBool(Boolean theSetting, String theProperty, String theCategory, int theID,
                         FormatCodeEnum theFormat) {
        super(theSetting, theProperty, theCategory, theID, theFormat);
        setCommon();
    }

    public void setCommon() {
        this.type = "bool";
        this.width(9);
        this.height(9);
    }

    @Override
    public void actionPerformed() {
        this.toggle();
    }

    @Override
    public void drawButton(Minecraft par1, int cursorX, int cursorY) {
        int centerX = this.x() + this.width() / 2;
        int centerY = this.y() + this.height() / 2;
        int tmpWidth = 9;
        int tmpHeight = 9;
        int tmpX = centerX - 4;
        int tmpY = centerY - 4;
        int fgcolor = 0x99a0a0a0;
        if (!this.enabled) {
            fgcolor = -0x995f5f60;
        }
        else if (this.hovered(cursorX, cursorY)) {
            fgcolor = 0x99ffffa0;
        }

        int labelColor = (this.enabled) ? 0xffffff : 0x666666;

        drawRect(tmpX + 1, tmpY, tmpX + tmpWidth - 1, tmpY + 1, fgcolor);
        drawRect(tmpX + 1, tmpY + tmpHeight - 1, tmpX + tmpWidth - 1, tmpY + tmpHeight, fgcolor);
        drawRect(tmpX, tmpY + 1, tmpX + 1, tmpY + tmpHeight - 1, fgcolor);
        drawRect(tmpX + tmpWidth - 1, tmpY + 1, tmpX + tmpWidth, tmpY + tmpHeight - 1, fgcolor);
        drawRect(tmpX + 1, tmpY + 1, tmpX + tmpWidth - 1, tmpY + tmpHeight - 1, 0xff000000);
        if (this.tempValue) {
            drawRect(centerX - 2, centerY, centerX - 1, centerY + 1, this.buttonColor);
            drawRect(centerX - 1, centerY + 1, centerX, centerY + 2, this.buttonColor);
            drawRect(centerX, centerY + 2, centerX + 1, centerY + 3, this.buttonColor);
            drawRect(centerX + 1, centerY + 2, centerX + 2, centerY, this.buttonColor);
            drawRect(centerX + 2, centerY, centerX + 3, centerY - 2, this.buttonColor);
            drawRect(centerX + 3, centerY - 2, centerX + 4, centerY - 4, this.buttonColor);
        }

        this.drawCenteredString(mc.fontRenderer, this.description,
                                this.labelX + mc.fontRenderer.getStringWidth(this.description) / 2, this.y()
                                    + (this.height() - 6) / 2, labelColor);
    }

    @Override
    public TCSettingType getType() {
        return TCSettingType.BOOL;
    }

    public void toggle() {
        this.tempValue = !(Boolean) this.tempValue;
    }

    @Override
    public void loadSelfFromProps(Properties readProps) {
        Object result = readProps.get(this.propertyName);
        if (result != null) {
            this.setCleanValue(Boolean.parseBoolean(result.toString()));
        }
        else {
            this.setCleanValue(false);
        }
    }

}
