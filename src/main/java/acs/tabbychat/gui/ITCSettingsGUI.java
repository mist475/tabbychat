package acs.tabbychat.gui;

import java.io.File;
import java.util.Properties;

import net.minecraft.client.gui.GuiButton;
import acs.tabbychat.util.TabbyChatUtils;

public interface ITCSettingsGUI {
	int SAVEBUTTON = 8901;
	int CANCELBUTTON = 8902;
	int MARGIN = 4;
	int LINE_HEIGHT = 14;
	int DISPLAY_WIDTH = 300;
	int DISPLAY_HEIGHT = 180;
	File tabbyChatDir = TabbyChatUtils.getTabbyChatDir();
	/**
	 * 
	 * @param button
	 */
	public void actionPerformed(GuiButton button);
	/**
	 * 
	 */
	public void defineDrawableSettings();
	/**
	 * 
	 * @param x
	 * @param y
	 * @param f
	 */
	public void drawScreen(int x, int y, float f);
	/**
	 * 
	 */
	public void handleMouseInput();
	/**
	 * 
	 */
	public void initDrawableSettings();
	/**
	 * 
	 */
	public void initGui();
	/**
	 * 
	 * @param par1
	 * @param par2
	 */
	public void keyTyped(char par1, int par2);
	/**
	 * Loads config file
	 * @return
	 */
	public Properties loadSettingsFile();
	/**
	 * 
	 * @param par1
	 * @param par2
	 * @param par3
	 */
	public void mouseClicked(int par1, int par2, int par3);
	/**
	 * 
	 */
	abstract void resetTempVars();
	/**
	 * 
	 * @param rowNum
	 * @return
	 */
	abstract int rowY(int rowNum);
	/**
	 * Saves settings
	 */
	abstract void saveSettingsFile();
	/**
	 * Specifies config file
	 * @param preProps
	 */
	abstract void saveSettingsFile(Properties preProps);
	/**
	 * 
	 */
	abstract void storeTempVars();
	/**
	 * 
	 */
	public void validateButtonStates();
}
