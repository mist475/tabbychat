package acs.tabbychat.settings;

import net.minecraft.client.Minecraft;

import java.util.Properties;

public interface ITCSetting<T> {

    void actionPerformed();

    void clear();

    void disable();

    void drawButton(Minecraft mc, int cursorX, int cursorY);

    void enable();

    boolean enabled();

    T getDefault();

    String getProperty();

    T getTempValue();

    T getValue();

    void setTempValue(T updateVal);

    TCSettingType getType();

    Boolean hovered(int cursorX, int cursorY);

    void loadSelfFromProps(Properties readProps);

    void mouseClicked(int par1, int par2, int par3);

    void reset();

    void resetDescription();

    void save();

    void saveSelfToProps(Properties writeProps);

    void setButtonDims(int wide, int tall);

    void setButtonLoc(int bx, int by);

    void setLabelLoc(int lx);

    void setCleanValue(T uncleanVal);

    void setValue(T updateVal);

    enum TCSettingType {
        BOOL,
        ENUM,
        SLIDER,
        TEXTBOX,
    }
}
