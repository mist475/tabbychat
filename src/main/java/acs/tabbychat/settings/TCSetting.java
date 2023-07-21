package acs.tabbychat.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import java.util.Properties;

abstract class TCSetting<T> extends GuiButton implements ITCSetting<T> {
    protected static Minecraft mc = Minecraft.getMinecraft();
    public final String categoryName;
    public final String propertyName;
    public int buttonColor = 0xbba5e7e4;
    public String description;
    protected int labelX;
    protected T value;
    protected T tempValue;
    protected T theDefault;

    public TCSetting(T theSetting, String theProperty, String theCategory, int theID) {
        super(theID, 0, 0, "");
        this.labelX = 0;
        this.value = theSetting;
        this.tempValue = theSetting;
        this.theDefault = theSetting;
        this.propertyName = theProperty;
        this.categoryName = theCategory;
        this.description = I18n.format(theCategory + "." + theProperty.toLowerCase());
    }

    public TCSetting(T theSetting, String theProperty, String theCategory, int theID,
                     FormatCodeEnum theFormat) {
        this(theSetting, theProperty, theCategory, theID);
        this.description = theFormat.toCode() + this.description + "\u00A7r";
    }

    @Override
    public void actionPerformed() {
    }

    public int width() {
        return this.width;
    }

    public void width(int _w) {
        this.width = _w;
    }

    public int height() {
        return this.height;
    }

    public void height(int _h) {
        this.height = _h;
    }

    public int x() {
        return xPosition;
    }

    public void x(int _x) {
        xPosition = _x;
    }

    public int y() {
        return yPosition;
    }

    public void y(int _y) {
        yPosition = _y;
    }

    @Override
    public void clear() {
        this.value = this.theDefault;
        this.tempValue = this.theDefault;
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public void drawButton(Minecraft mc, int cursorX, int cursorY) {
    }

    @Override
    public void enable() {
        this.enabled = true;
    }

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public T getDefault() {
        return this.theDefault;
    }

    @Override
    public String getProperty() {
        return this.propertyName;
    }

    @Override
    public T getTempValue() {
        return this.tempValue;
    }

    @Override
    public void setTempValue(T updateVal) {
        this.tempValue = updateVal;
    }

    @Override
    public abstract TCSettingType getType();

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public void setValue(T updateVal) {
        this.value = updateVal;
    }

    @Override
    public Boolean hovered(int cursorX, int cursorY) {
        return cursorX >= this.x() && cursorY >= this.y() && cursorX < this.x() + this.width()
            && cursorY < this.y() + this.height();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadSelfFromProps(Properties readProps) {
        this.setCleanValue((T) readProps.get(this.propertyName));
    }

    @Override
    public void mouseClicked(int par1, int par2, int par3) {
    }

    @Override
    public void reset() {
        this.tempValue = this.value;
    }

    @Override
    public void resetDescription() {
        this.description = this.categoryName.isEmpty() ? "" : I18n.format(this.categoryName + "."
                                                                              + this.propertyName.toLowerCase());
    }

    @Override
    public void save() {
        this.value = this.tempValue;
    }

    @Override
    public void saveSelfToProps(Properties writeProps) {
        writeProps.put(this.propertyName, this.value.toString());
    }

    @Override
    public void setButtonDims(int wide, int tall) {
        this.width(wide);
        this.height(tall);
    }

    @Override
    public void setButtonLoc(int bx, int by) {
        this.x(bx);
        this.y(by);
    }

    @Override
    public void setLabelLoc(int _x) {
        this.labelX = _x;
    }

    @Override
    public void setCleanValue(T updateVal) {
        if (updateVal == null)
            this.clear();
        else
            this.value = updateVal;
    }

}
