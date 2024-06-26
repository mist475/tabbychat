package acs.tabbychat.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

public class TCSettingTextBox extends TCSetting<String> {
    protected GuiTextField textBox;
    protected int charLimit = 32;

    public TCSettingTextBox(String theSetting, String theProperty, String theCategory, int theID) {
        super(theSetting, theProperty, theCategory, theID);
        this.width(50);
        this.height(11);
        this.textBox = new GuiTextField(mc.fontRenderer, 0, 0, this.width(), this.height());
        this.textBox.setText(this.value);
    }

    @Override
    public void clear() {
        super.clear();
        this.textBox.setText(this.theDefault);
    }

    @Override
    public void disable() {
        super.disable();
        this.textBox.setEnabled(false);
    }

    @Override
    public void drawButton(Minecraft par1, int cursorX, int cursorY) {
        int labelColor = (this.enabled) ? 0xffffff : 0x666666;

        this.textBox.drawTextBox();
        this.drawCenteredString(mc.fontRenderer, this.description,
                                this.labelX + mc.fontRenderer.getStringWidth(this.description) / 2, this.y()
                                    + (this.height() - 6) / 2, labelColor);
    }

    @Override
    public void enable() {
        super.enable();
        this.textBox.setEnabled(true);
    }

    public void func_146184_c(boolean val) {
        this.enabled = val;
        this.textBox.setEnabled(val);
    }

    @Override
    public String getTempValue() {
        return this.textBox.getText().trim();
    }

    @Override
    public void setTempValue(String theVal) {
        this.textBox.setText(theVal);
    }

    @Override
    public TCSettingType getType() {
        return TCSettingType.TEXTBOX;
    }

    public void keyTyped(char par1, int par2) {
        this.textBox.textboxKeyTyped(par1, par2);
    }

    @Override
    public void mouseClicked(int par1, int par2, int par3) {
        this.textBox.mouseClicked(par1, par2, par3);
    }

    private void reassignField() {
        String tmp = this.textBox.getText();
        this.textBox = new GuiTextField(mc.fontRenderer, this.x(), this.y() + 1, this.width(),
                                        this.height() + 1);
        this.textBox.setMaxStringLength(this.charLimit);
        this.textBox.setText(tmp);
    }

    @Override
    public void reset() {
        if (this.value == null)
            this.value = "";
        this.textBox.setText(this.value);
    }

    @Override
    public void save() {
        this.value = this.textBox.getText().trim();
    }

    @Override
    public void setButtonDims(int wide, int tall) {
        super.setButtonDims(wide, tall);
        this.reassignField();
    }

    @Override
    public void setButtonLoc(int bx, int by) {
        super.setButtonLoc(bx, by);
        this.reassignField();
    }

    public void setCharLimit(int newLimit) {
        this.charLimit = newLimit;
        this.textBox.setMaxStringLength(newLimit);
    }

    public void setDefault(String newDefault) {
        this.theDefault = newDefault;
    }

    @Override
    public void setValue(String updateVal) {
        this.value = updateVal;
        this.tempValue = updateVal;
        this.textBox.setText(updateVal);
    }
}
