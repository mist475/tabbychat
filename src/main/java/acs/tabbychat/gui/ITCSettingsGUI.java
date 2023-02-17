package acs.tabbychat.gui;

import acs.tabbychat.util.TabbyChatUtils;
import net.minecraft.client.gui.GuiButton;

import java.io.File;
import java.util.Properties;

public interface ITCSettingsGUI {
    int SAVEBUTTON = 8901;
    int CANCELBUTTON = 8902;
    int MARGIN = 4;
    int LINE_HEIGHT = 14;
    int DISPLAY_WIDTH = 300;
    int DISPLAY_HEIGHT = 180;
    File tabbyChatDir = TabbyChatUtils.getTabbyChatDir();

    void actionPerformed(GuiButton button);

    void defineDrawableSettings();

    void drawScreen(int x, int y, float f);

    void handleMouseInput();

    void initDrawableSettings();


    void initGui();

    void keyTyped(char par1, int par2);

    /**
     * Loads config file
     */
    Properties loadSettingsFile();

    void mouseClicked(int par1, int par2, int par3);

    void resetTempVars();

    int rowY(int rowNum);

    /**
     * Saves settings
     */
    void saveSettingsFile();

    /**
     * Specifies config file
     */
    void saveSettingsFile(Properties preProps);

    void storeTempVars();

    void validateButtonStates();
}
