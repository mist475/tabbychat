package acs.tabbychat.gui;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.ColorCodeEnum;
import acs.tabbychat.settings.FormatCodeEnum;
import acs.tabbychat.settings.TCSettingBool;
import acs.tabbychat.settings.TCSettingEnum;
import acs.tabbychat.settings.TimeStampEnum;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class TCSettingsGeneral extends TCSettingsGUI {
    private static final int TABBYCHAT_ENABLE_ID = 9101;
    private static final int SAVE_CHATLOG_ID = 9102;
    private static final int TIMESTAMP_ENABLE_ID = 9103;
    private static final int TIMESTAMP_STYLE_ID = 9104;
    private static final int GROUP_SPAM_ID = 9105;
    private static final int UNREAD_FLASHING_ID = 9106;
    private static final int TIMESTAMP_COLOR_ID = 9107;
    private static final int UPDATE_CHECK_ENABLE = 9109;
    private static final int SPLIT_CHATLOG = 9110;

    {
        this.propertyPrefix = "settings.general";
    }

    public SimpleDateFormat timeStamp = new SimpleDateFormat();
    public TCSettingBool tabbyChatEnable = new TCSettingBool(true, "tabbyChatEnable",
                                                             this.propertyPrefix, TABBYCHAT_ENABLE_ID);
    public TCSettingBool saveChatLog = new TCSettingBool(false, "saveChatLog", this.propertyPrefix,
                                                         SAVE_CHATLOG_ID);
    public TCSettingBool timeStampEnable = new TCSettingBool(false, "timeStampEnable",
                                                             this.propertyPrefix, TIMESTAMP_ENABLE_ID);
    public TCSettingEnum timeStampStyle = new TCSettingEnum(TimeStampEnum.MILITARY,
                                                            "timeStampStyle", this.propertyPrefix, TIMESTAMP_STYLE_ID, FormatCodeEnum.ITALIC);
    public TCSettingEnum timeStampColor = new TCSettingEnum(ColorCodeEnum.DEFAULT,
                                                            "timeStampColor", this.propertyPrefix, TIMESTAMP_COLOR_ID, FormatCodeEnum.ITALIC);
    public TCSettingBool groupSpam = new TCSettingBool(false, "groupSpam", this.propertyPrefix,
                                                       GROUP_SPAM_ID);
    public TCSettingBool unreadFlashing = new TCSettingBool(true, "unreadFlashing",
                                                            this.propertyPrefix, UNREAD_FLASHING_ID);
    public TCSettingBool updateCheckEnable = new TCSettingBool(true, "updateCheckEnable",
                                                               this.propertyPrefix, UPDATE_CHECK_ENABLE);
    public TCSettingBool splitChatLog = new TCSettingBool(false, "splitChatLog",
                                                          this.propertyPrefix, SPLIT_CHATLOG);

    public TCSettingsGeneral(TabbyChat _tc) {
        super(_tc);
        this.name = I18n.format("settings.general.name");
        this.settingsFile = new File(tabbyChatDir, "general.cfg");
        this.bgcolor = 0x664782be;
        this.defineDrawableSettings();
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == TABBYCHAT_ENABLE_ID) {
            if (tc.enabled())
                tc.disable();
            else {
                tc.enable();
            }
        }
        super.actionPerformed(button);
    }

    private void applyTimestampPattern() {
        if (((ColorCodeEnum) this.timeStampColor.getValue()).toCode().length() > 0) {
            String tsPattern = "'" + ((ColorCodeEnum) this.timeStampColor.getValue()).toCode() +
                    "'" +
                    ((TimeStampEnum) this.timeStampStyle.getValue()).toCode() +
                    "'\u00A7r'";
            this.timeStamp.applyPattern(tsPattern);
        }
        else {
            this.timeStamp.applyPattern(((TimeStampEnum) this.timeStampStyle.getValue()).toCode());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void defineDrawableSettings() {
        this.buttonList.add(this.tabbyChatEnable);
        this.buttonList.add(this.saveChatLog);
        this.buttonList.add(this.timeStampEnable);
        this.buttonList.add(this.timeStampStyle);
        this.buttonList.add(this.timeStampColor);
        this.buttonList.add(this.groupSpam);
        this.buttonList.add(this.unreadFlashing);
        this.buttonList.add(this.updateCheckEnable);
        this.buttonList.add(this.splitChatLog);
    }

    @Override
    public void initDrawableSettings() {
        int effRight = (this.width + DISPLAY_WIDTH) / 2;
        int col1x = (this.width - DISPLAY_WIDTH) / 2 + 55;
        int col2x = this.width / 2 + 25;

        int buttonColor = (this.bgcolor & 0x00ffffff) + 0xff000000;

        this.tabbyChatEnable.setButtonLoc(col1x, this.rowY(1));
        this.tabbyChatEnable.setLabelLoc(col1x + 19);
        this.tabbyChatEnable.buttonColor = buttonColor;

        this.saveChatLog.setButtonLoc(col1x, this.rowY(2));
        this.saveChatLog.setLabelLoc(col1x + 19);
        this.saveChatLog.buttonColor = buttonColor;

        this.splitChatLog.setButtonLoc(col2x, this.rowY(2));
        this.splitChatLog.setLabelLoc(col2x + 19);
        this.splitChatLog.buttonColor = buttonColor;

        this.timeStampEnable.setButtonLoc(col1x, this.rowY(3));
        this.timeStampEnable.setLabelLoc(col1x + 19);
        this.timeStampEnable.buttonColor = buttonColor;

        this.timeStampStyle.setButtonDims(80, 11);
        this.timeStampStyle.setButtonLoc(effRight - 80, this.rowY(4));
        this.timeStampStyle.setLabelLoc(this.timeStampStyle.x() - 10
                                                - mc.fontRenderer.getStringWidth(this.timeStampStyle.description));

        this.timeStampColor.setButtonDims(80, 11);
        this.timeStampColor.setButtonLoc(effRight - 80, this.rowY(5));
        this.timeStampColor.setLabelLoc(this.timeStampColor.x() - 10
                                                - mc.fontRenderer.getStringWidth(this.timeStampColor.description));

        this.groupSpam.setButtonLoc(col1x, this.rowY(6));
        this.groupSpam.setLabelLoc(col1x + 19);
        this.groupSpam.buttonColor = buttonColor;

        this.unreadFlashing.setButtonLoc(col1x, this.rowY(7));
        this.unreadFlashing.setLabelLoc(col1x + 19);
        this.unreadFlashing.buttonColor = buttonColor;

        this.updateCheckEnable.setButtonLoc(col1x, this.rowY(8));
        this.updateCheckEnable.setLabelLoc(col1x + 19);
        this.updateCheckEnable.buttonColor = buttonColor;
    }

    @Override
    public Properties loadSettingsFile() {
        super.loadSettingsFile();
        this.applyTimestampPattern();
        return null;
    }

    @Override
    public void storeTempVars() {
        super.storeTempVars();
        this.applyTimestampPattern();
    }

    @Override
    public void validateButtonStates() {
        this.timeStampColor.enabled = this.timeStampEnable.getTempValue();
    }
}
