package acs.tabbychat.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class PrefsButton extends GuiButton {
    protected int bgcolor = 0xDD000000;
    protected boolean hasControlCodes = false;
    protected String type;

    /**
	 * 
	 */
    public PrefsButton() {
        super(9999, 0, 0, 1, 1, "");
    }

    /**
     * @param _id
     * @param _x
     * @param _y
     * @param _w
     * @param _h
     * @param _title
     */
    public PrefsButton(int _id, int _x, int _y, int _w, int _h, String _title) {
        super(_id, _x, _y, _w, _h, _title);
    }

    /**
     * @param _id
     * @param _x
     * @param _y
     * @param _w
     * @param _h
     * @param _title
     * @param _bgcolor
     */
    public PrefsButton(int _id, int _x, int _y, int _w, int _h, String _title, int _bgcolor) {
        super(_id, _x, _y, _w, _h, _title);
        this.bgcolor = _bgcolor;
    }

    /**
     * Sets name of button
     * 
     * @param newtitle
     */
    protected void title(String newtitle) {
        this.displayString = newtitle;
    }

    /**
     * Returns name of button
     * 
     * @return
     */
    protected String title() {
        return this.displayString;
    }

    /**
     * Returns button width
     * 
     * @return
     */
    public int width() {
        return this.width;
    }

    /**
     * Sets button width
     * 
     * @param _w
     */
    public void width(int _w) {
        this.width = _w;
    }

    /**
     * Returns button height
     * 
     * @return
     */
    public int height() {
        return this.height;
    }

    /**
     * Sets button height
     * 
     * @param _h
     */
    public void height(int _h) {
        this.height = _h;
    }

    /**
     * Return x position
     * 
     * @return
     */
    public int x() {
        return xPosition;
    }

    /**
     * Set x position
     * 
     * @param _x
     */
    public void x(int _x) {
        xPosition = _x;
    }

    /**
     * Return y position
     * 
     * @return
     */
    public int y() {
        return yPosition;
    }

    /**
     * Set y position
     * 
     * @param _y
     */
    public void y(int _y) {
        yPosition = _y;
    }

    /**
     * @return
     */
    protected int adjustWidthForControlCodes() {
        String cleaned = this.displayString.replaceAll("(?i)\u00A7[0-9A-FK-OR]", "");
        boolean bold = (this.displayString.replaceAll("(?i)\u00A7L", "").length() != this.displayString
                .length());
        int badWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(this.displayString);
        int goodWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(cleaned);
        if (bold)
            goodWidth += cleaned.length();
        return (badWidth > goodWidth) ? badWidth - goodWidth : 0;
    }

    public void drawButton(Minecraft mc, int cursorX, int cursorY) {
        if (this.visible) {
            FontRenderer fr = mc.fontRenderer;
            drawRect(this.x(), this.y(), this.x() + this.width(), this.y() + this.height(),
                    this.bgcolor);
            boolean hovered = cursorX >= this.x() && cursorY >= this.y()
                    && cursorX < this.x() + this.width() && cursorY < this.y() + this.height();

            if (bgcolor == 0xDD000000 || bgcolor == 0x99999999) {
                drawRect(this.x() - 1, this.y() - 1, this.x(), this.y() + this.height(), 0xc0c0c0c0);
                drawRect(this.x() - 1, this.y() - 1, this.x() + this.width() + 1, this.y(),
                        0xc0c0c0c0);
                drawRect(this.x() - 1, this.y() + this.height(), this.x() + this.width() + 1,
                        this.y() + this.height() + 1, 0x70707070);
                drawRect(this.x() + this.width(), this.y() - 1, this.x() + this.width() + 1,
                        this.y() + this.height() + 1, 0x70707070);
            }

            int var7 = 0xa0a0a0;
            if (!this.enabled) {
                var7 = -0x5f5f60;
            } else if (hovered) {
                var7 = 0xffffa0;
            }

            if (this.hasControlCodes) {
                int offset = this.adjustWidthForControlCodes();
                this.drawCenteredString(fr, this.displayString, this.x() + (this.width() + offset)
                        / 2, this.y() + (this.height() - 8) / 2, var7);
            } else
                this.drawCenteredString(fr, this.displayString, this.x() + this.width() / 2,
                        this.y() + (this.height() - 8) / 2, var7);
        }
    }
}
