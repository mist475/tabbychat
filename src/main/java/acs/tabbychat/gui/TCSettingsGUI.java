package acs.tabbychat.gui;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.ITCSetting;
import acs.tabbychat.settings.TCSettingSlider;
import acs.tabbychat.settings.TCSettingTextBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

abstract class TCSettingsGUI extends GuiScreen implements ITCSettingsGUI {
    protected static TabbyChat tc;
    protected static List<TCSettingsGUI> ScreenList = new ArrayList<>();
    protected Minecraft mc;
    protected int lastOpened = 0;
    protected String name;
    protected String propertyPrefix;
    protected int bgcolor = 0x66a5e7e4;
    protected int id = 9000;
    protected File settingsFile;

    private TCSettingsGUI() {
        mc = Minecraft.getMinecraft();
        ScreenList.add(this);
    }

    public TCSettingsGUI(TabbyChat _tc) {
        this();
        tc = _tc;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button instanceof ITCSetting && !Objects.equals(((ITCSetting) button).getType(), "textbox")) {
            ((ITCSetting) button).actionPerformed();
        }
        else if (button.id == SAVEBUTTON) {
            for (TCSettingsGUI screen : ScreenList) {
                screen.storeTempVars();
                screen.saveSettingsFile();
            }
            tc.reloadSettingsData(true);
            mc.displayGuiScreen((GuiScreen) null);
            if (TabbyChat.generalSettings.tabbyChatEnable.getValue())
                tc.resetDisplayedChat();
        }
        else if (button.id == CANCELBUTTON) {
            for (TCSettingsGUI screen : ScreenList) {
                screen.resetTempVars();
            }
            mc.displayGuiScreen((GuiScreen) null);
            if (TabbyChat.generalSettings.tabbyChatEnable.getValue())
                tc.resetDisplayedChat();
        }
        else {
            for (TCSettingsGUI tcSettingsGUI : ScreenList) {
                if (button.id == tcSettingsGUI.id) {
                    mc.displayGuiScreen(tcSettingsGUI);
                }
            }
        }
        this.validateButtonStates();
    }

    /**
     * Define buttons to draw
     */
    @Override
    public void defineDrawableSettings() {
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        int effLeft = (this.width - DISPLAY_WIDTH) / 2;
        int absLeft = effLeft - MARGIN;
        int effTop = (this.height - DISPLAY_HEIGHT) / 2;
        int absTop = effTop - MARGIN;

        drawRect(absLeft, absTop, absLeft + DISPLAY_WIDTH + 2 * MARGIN, absTop + DISPLAY_HEIGHT + 2
                * MARGIN, 0x88000000);
        drawRect(absLeft + 45, absTop, absLeft + 46, absTop + DISPLAY_HEIGHT, 0x66ffffff);

        for (int i = 0; i < ScreenList.size(); i++) {
            if (ScreenList.get(i) == this) {
                int curWidth;
                int tabDist = Math.max(mc.fontRenderer.getStringWidth(ScreenList.get(i).name)
                                               + MARGIN - 40, 25);
                if (0 <= this.lastOpened && this.lastOpened <= 5) {
                    curWidth = 45 + (this.lastOpened * tabDist) / 5;
                    this.lastOpened++;
                }
                else {
                    curWidth = tabDist + 45;
                }
                drawRect(absLeft - curWidth + 45, effTop + 30 * i, absLeft + 45, effTop + 30 * i
                        + 20, ScreenList.get(i).bgcolor);
                this.drawString(mc.fontRenderer,
                                mc.fontRenderer.trimStringToWidth(ScreenList.get(i).name, curWidth - 5),
                                effLeft - curWidth + 45, effTop + 6 + 30 * i, 0xffffff);
            }
            else {
                drawRect(absLeft, effTop + 30 * i, absLeft + 45, effTop + 30 * i + 20,
                         ScreenList.get(i).bgcolor);
            }
        }
        for (Object o : this.buttonList) {
            ((GuiButton) o).drawButton(mc, x, y);
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        for (Object o : this.buttonList) {
            if (o instanceof ITCSetting) {
                ITCSetting tmp = (ITCSetting) o;
                if (Objects.equals(tmp.getType(), "slider")) {
                    ((TCSettingSlider) tmp).handleMouseInput();
                }
            }
        }
    }

    /**
     * Defines various button properties
     */
    @Override
    public void initDrawableSettings() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        int effLeft = (this.width - DISPLAY_WIDTH) / 2;
        int effTop = (this.height - DISPLAY_HEIGHT) / 2;
        this.lastOpened = 0;
        int effRight = (this.width + DISPLAY_WIDTH) / 2;
        int bW = 40;
        int bH = LINE_HEIGHT;
        PrefsButton savePrefs = new PrefsButton(SAVEBUTTON, effRight + 10,
                                                (this.height + DISPLAY_HEIGHT) / 2 - bH, bW, bH, I18n.format("settings.save"));
        this.buttonList.add(savePrefs);
        PrefsButton cancelPrefs = new PrefsButton(CANCELBUTTON, effRight + 10,
                                                  (this.height + DISPLAY_HEIGHT) / 2 - 2 * bH - 2, bW, bH,
                                                  I18n.format("settings.cancel"));
        this.buttonList.add(cancelPrefs);

        for (int i = 0; i < ScreenList.size(); i++) {
            ScreenList.get(i).id = 9000 + i;
            ScreenList.get(i).name = I18n.format(ScreenList.get(i).propertyPrefix + ".name");
            if (ScreenList.get(i) != this) {
                this.buttonList.add(new PrefsButton(ScreenList.get(i).id, effLeft, effTop + 30 * i,
                                                    45, 20, mc.fontRenderer.trimStringToWidth(ScreenList.get(i).name, 35)
                                                            + "..."));
                ((PrefsButton) this.buttonList.get(this.buttonList.size() - 1)).bgcolor = 0x00000000;
            }
        }
        this.defineDrawableSettings();
        this.initDrawableSettings();
        this.validateButtonStates();
        for (Object drawable : this.buttonList) {
            if (drawable instanceof ITCSetting)
                ((ITCSetting) drawable).resetDescription();
        }
    }

    @Override
    public void keyTyped(char par1, int par2) {
        for (Object o : this.buttonList) {
            if (ITCSetting.class.isInstance(o)) {
                ITCSetting tmp = (ITCSetting) o;
                if (Objects.equals(tmp.getType(), "textbox")) {
                    ((TCSettingTextBox) tmp).keyTyped(par1, par2);
                }
            }
        }
        super.keyTyped(par1, par2);
    }

    @Override
    public Properties loadSettingsFile() {
        Properties settingsTable = new Properties();
        if (this.settingsFile == null)
            return settingsTable;
        if (!this.settingsFile.exists())
            return settingsTable;

        try (FileInputStream fInStream = new FileInputStream(this.settingsFile); BufferedInputStream bInStream = new BufferedInputStream(fInStream)) {
            settingsTable.load(bInStream);
        }
        catch (Exception e) {
            TabbyChat.printException("Error while reading settings from file '" + this.settingsFile
                                             + "'", e);
        }
        for (Object drawable : this.buttonList) {
            if (drawable instanceof ITCSetting) {
                ((ITCSetting) drawable).loadSelfFromProps(settingsTable);
            }
        }
        this.resetTempVars();
        return settingsTable;
    }

    @Override
    public void mouseClicked(int par1, int par2, int par3) {
        for (Object o : this.buttonList) {
            if (o instanceof ITCSetting) {
                ITCSetting tmp = (ITCSetting) o;
                if (Objects.equals(tmp.getType(), "textbox") || Objects.equals(tmp.getType(), "enum")
                        || Objects.equals(tmp.getType(), "slider")) {
                    tmp.mouseClicked(par1, par2, par3);
                }
            }
        }
        super.mouseClicked(par1, par2, par3);
    }

    /**
     * Reset temporary Variables
     */
    @Override
    public void resetTempVars() {
        for (Object drawable : this.buttonList) {
            if (drawable instanceof ITCSetting) {
                ((ITCSetting) drawable).reset();
            }
        }
    }

    /**
     * What row to draw on
     */
    @Override
    public int rowY(int rowNum) {
        return (this.height - DISPLAY_HEIGHT) / 2 + (rowNum - 1) * (LINE_HEIGHT + MARGIN);
    }

    @Override
    public void saveSettingsFile(Properties settingsTable) {
        if (this.settingsFile == null)
            return;
        if (!this.settingsFile.getParentFile().exists())
            this.settingsFile.getParentFile().mkdirs();

        for (Object drawable : this.buttonList) {
            if (drawable instanceof ITCSetting) {
                ((ITCSetting) drawable).saveSelfToProps(settingsTable);
            }
        }

        try (FileOutputStream fOutStream = new FileOutputStream(this.settingsFile); BufferedOutputStream bOutStream = new BufferedOutputStream(fOutStream)) {
            settingsTable.store(bOutStream, this.propertyPrefix);
        }
        catch (Exception e) {
            TabbyChat.printException("Error while writing settings to file '" + this.settingsFile
                                             + "'", e);
        }
    }

    @Override
    public void saveSettingsFile() {
        this.saveSettingsFile(new Properties());
    }

    /**
     * Stores temporary variables
     */
    @Override
    public void storeTempVars() {
        for (Object drawable : this.buttonList) {
            if (drawable instanceof ITCSetting) {
                ((ITCSetting) drawable).save();
            }
        }
    }

    @Override
    public void validateButtonStates() {
    }
}
