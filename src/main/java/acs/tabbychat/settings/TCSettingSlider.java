package acs.tabbychat.settings;

import acs.tabbychat.util.TabbyChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class TCSettingSlider extends TCSetting implements ITCSetting {
    private final int buttonOffColor = 0x44ffffff;
    public String units = "%";
    protected float minValue;
    protected float maxValue;
    protected float sliderValue;
    private int sliderX;
    private boolean dragging = false;

    private TCSettingSlider(Object theSetting, String theProperty, String theCategory, int theID) {
        super(theSetting, theProperty, theCategory, theID);
        this.type = "slider";
        setSliderValue();
        this.width(100);
        this.height(11);
    }

    public TCSettingSlider(Float theSetting, String theProperty, String theCategory, int theID,
                           float minVal, float maxVal) {
        this(theSetting, theProperty, theCategory, theID);
        this.minValue = minVal;
        this.maxValue = maxVal;
    }

    @Override
    public void clear() {
        super.clear();
        setSliderValue();
    }

    public void setSliderValue() {
        this.sliderValue = ((Float) this.tempValue - this.minValue)
                / (this.maxValue - this.minValue);
    }

    @Override
    public void drawButton(Minecraft par1, int cursorX, int cursorY) {
        int fgcolor = 0x99a0a0a0;
        if (!this.enabled) {
            fgcolor = -0x995f5f60;
        }
        else if (this.hovered(cursorX, cursorY)) {
            fgcolor = 0x99ffffa0;
            if (this.dragging) {
                this.sliderX = cursorX - 1;
                this.sliderValue = (float) (this.sliderX - (this.x() + 1)) / (this.width() - 5);
                if (this.sliderValue < 0.0f)
                    this.sliderValue = 0.0f;
                else if (this.sliderValue > 1.0f)
                    this.sliderValue = 1.0f;
            }
        }
        int labelColor = (this.enabled) ? 0xffffff : 0x666666;
        int buttonColor = (this.enabled) ? this.buttonColor : this.buttonOffColor;

        Gui.drawRect(this.x(), this.y() + 1, this.x() + 1, this.y() + this.height() - 1, fgcolor);
        Gui.drawRect(this.x() + 1, this.y(), this.x() + this.width() - 1, this.y() + 1, fgcolor);
        Gui.drawRect(this.x() + 1, this.y() + this.height() - 1, this.x() + this.width() - 1,
                     this.y() + this.height(), fgcolor);
        Gui.drawRect(this.x() + this.width() - 1, this.y() + 1, this.x() + this.width(), this.y()
                + this.height() - 1, fgcolor);
        Gui.drawRect(this.x() + 1, this.y() + 1, this.x() + this.width() - 1,
                     this.y() + this.height() - 1, 0xff000000);

        this.sliderX = Math.round(this.sliderValue * (this.width() - 5)) + this.x() + 1;
        Gui.drawRect(this.sliderX, this.y() + 1, this.sliderX + 1, this.y() + 2,
                     buttonColor & 0x88ffffff);
        Gui.drawRect(this.sliderX + 1, this.y() + 1, this.sliderX + 2, this.y() + 2, buttonColor);
        Gui.drawRect(this.sliderX + 2, this.y() + 1, this.sliderX + 3, this.y() + 2,
                     buttonColor & 0x88ffffff);
        Gui.drawRect(this.sliderX, this.y() + 2, this.sliderX + 1, this.y() + this.height() - 2,
                     buttonColor);
        Gui.drawRect(this.sliderX + 1, this.y() + 2, this.sliderX + 2,
                     this.y() + this.height() - 2, buttonColor & 0x88ffffff);
        Gui.drawRect(this.sliderX + 2, this.y() + 2, this.sliderX + 3,
                     this.y() + this.height() - 2, buttonColor);
        Gui.drawRect(this.sliderX, this.y() + this.height() - 2, this.sliderX + 1,
                     this.y() + this.height() - 1, buttonColor & 0x88ffffff);
        Gui.drawRect(this.sliderX + 1, this.y() + this.height() - 2, this.sliderX + 2, this.y()
                + this.height() - 1, buttonColor);
        Gui.drawRect(this.sliderX + 2, this.y() + this.height() - 2, this.sliderX + 3, this.y()
                + this.height() - 1, buttonColor & 0x88ffffff);

        int valCenter;
        if (this.sliderValue < 0.5f)
            valCenter = Math.round(0.7f * this.width());
        else
            valCenter = Math.round(0.2f * this.width());

        String valLabel = Math.round(this.sliderValue
                                             * (this.maxValue - this.minValue) + this.minValue)
                + this.units;
        this.drawCenteredString(mc.fontRenderer, valLabel, valCenter + this.x(), this.y() + 2,
                                buttonColor);

        this.drawCenteredString(mc.fontRenderer, this.description,
                                this.labelX + mc.fontRenderer.getStringWidth(this.description) / 2, this.y()
                                        + (this.height() - 6) / 2, labelColor);
    }

    @Override
    public Float getTempValue() {
        this.tempValue = this.sliderValue * (this.maxValue - this.minValue) + this.minValue;
        return (Float) this.tempValue;
    }

    @Override
    public void setTempValue(Object theVal) {
        super.setTempValue(theVal);
        setSliderValue();
    }

    @Override
    public Float getValue() {
        return (Float) this.value;
    }

    public void handleMouseInput() {
        if (mc.currentScreen == null)
            return;
        int mX = Mouse.getEventX() * mc.currentScreen.width / mc.displayWidth;
        int mY = mc.currentScreen.height - Mouse.getEventY() * mc.currentScreen.height
                / mc.displayHeight - 1;
        if (!this.hovered(mX, mY))
            return;

        int var1 = Mouse.getEventDWheel();
        if (var1 != 0) {
            if (var1 > 1) {
                var1 = 3;
            }

            if (var1 < -1) {
                var1 = -3;
            }

            if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)) {
                var1 *= -7;
            }
        }
        this.sliderValue += (float) var1 / 100;
        if (this.sliderValue < 0.0f)
            this.sliderValue = 0.0f;
        else if (this.sliderValue > 1.0f)
            this.sliderValue = 1.0f;
        this.tempValue = this.sliderValue * (this.maxValue - this.minValue) + this.minValue;
    }

    @Override
    public void mouseClicked(int par1, int par2, int par3) {
        if (par3 == 0 && this.hovered(par1, par2) && this.enabled) {
            this.sliderX = par1 - 1;
            this.sliderValue = (float) (this.sliderX - (this.x() + 1)) / (this.width() - 5);
            if (this.sliderValue < 0.0f)
                this.sliderValue = 0.0f;
            else if (this.sliderValue > 1.0f)
                this.sliderValue = 1.0f;

            if (!this.dragging)
                this.dragging = true;
        }
    }

    @Override
    public void mouseReleased(int par1, int par2) {
        this.dragging = false;
    }

    @Override
    public void reset() {
        super.reset();
        setSliderValue();
    }

    @Override
    public void save() {
        this.tempValue = this.sliderValue * (this.maxValue - this.minValue) + this.minValue;
        super.save();
    }

    @Override
    public void setCleanValue(Object updateVal) {
        if (updateVal == null)
            this.clear();
        else
            this.value = TabbyChatUtils.median(this.minValue, this.maxValue,
                                               Float.parseFloat((String) updateVal));
    }

    public void setRange(float theMin, float theMax) {
        this.minValue = theMin;
        this.maxValue = theMax;
        setSliderValue();
    }
}
