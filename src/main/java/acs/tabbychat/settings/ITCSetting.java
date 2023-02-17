package acs.tabbychat.settings;

import net.minecraft.client.Minecraft;

import java.util.Properties;

public interface ITCSetting {

    void actionPerformed();

    void clear();

    void disable();

    void drawButton(Minecraft mc, int cursorX, int cursorY);

    void enable();

    boolean enabled();

    Object getDefault();

    String getProperty();

    Object getTempValue();

    void setTempValue(Object updateVal);

    String getType();

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

    void setCleanValue(Object uncleanVal);

    void setValue(Object updateVal);
}
