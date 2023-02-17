package acs.tabbychat.gui;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.TCSettingBool;
import acs.tabbychat.settings.TCSettingSlider;
import acs.tabbychat.settings.TCSettingTextBox;
import acs.tabbychat.settings.files.TCSettingsAdvancedFile;
import acs.tabbychat.util.TabbyChatUtils;
import net.minecraft.client.resources.I18n;

import java.io.File;
import java.util.Properties;

/**
 * UI handling for advanced settings
 * Actual settings file handling happens in {@link TCSettingsAdvancedFile}
 */
public class TCSettingsAdvanced extends TCSettingsGUI {
    private static final int CHAT_SCROLL_HISTORY_ID = 9401;
    private static final int MAXLENGTH_CHANNEL_NAME_ID = 9402;
    private static final int MULTICHAT_DELAY_ID = 9403;
    private static final int CHATBOX_UNFOC_HEIGHT_ID = 9406;
    private static final int CHAT_FADE_TICKS_ID = 9408;
    private static final int TEXT_IGNORE_OPACITY_ID = 9410;
    private static final int CONVERT_UNICODE_TEXT_ID = 9411;

    private final TCSettingsAdvancedFile settings = new TCSettingsAdvancedFile();
    public TCSettingTextBox chatScrollHistory;
    public TCSettingTextBox maxLengthChannelName;
    public TCSettingTextBox multiChatDelay;
    public TCSettingSlider chatBoxUnfocHeight;
    public TCSettingSlider chatFadeTicks;
    public TCSettingBool textIgnoreOpacity;
    public TCSettingBool convertUnicodeText;

    public TCSettingsAdvanced(TabbyChat _tc) {
        super(_tc);
        propertyPrefix = "settings.advanced";

        chatScrollHistory = new TCSettingTextBox("100", "chatScrollHistory",
                                                 propertyPrefix, CHAT_SCROLL_HISTORY_ID);
        maxLengthChannelName = new TCSettingTextBox("10",
                                                    "maxLengthChannelName", propertyPrefix, MAXLENGTH_CHANNEL_NAME_ID);
        multiChatDelay = new TCSettingTextBox("500", "multiChatDelay",
                                              propertyPrefix, MULTICHAT_DELAY_ID);
        chatBoxUnfocHeight = new TCSettingSlider(50.0f, "chatBoxUnfocHeight",
                                                 propertyPrefix, CHATBOX_UNFOC_HEIGHT_ID, 20.0f, 100.0f);
        chatFadeTicks = new TCSettingSlider(200.0f, "chatFadeTicks",
                                            propertyPrefix, CHAT_FADE_TICKS_ID, 10.0f, 2000.0f);

        textIgnoreOpacity = new TCSettingBool(false, "textignoreopacity",
                                              propertyPrefix, TEXT_IGNORE_OPACITY_ID);
        convertUnicodeText = new TCSettingBool(false, "convertunicodetext",
                                               propertyPrefix, CONVERT_UNICODE_TEXT_ID);
        this.name = I18n.format("settings.advanced.name");
        this.settingsFile = new File(tabbyChatDir, "advanced.cfg");
        this.bgcolor = 0x66802e94;
        this.chatScrollHistory.setCharLimit(3);
        this.maxLengthChannelName.setCharLimit(2);
        this.multiChatDelay.setCharLimit(4);
        this.defineDrawableSettings();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void defineDrawableSettings() {
        this.buttonList.add(this.chatScrollHistory);
        this.buttonList.add(this.maxLengthChannelName);
        this.buttonList.add(this.multiChatDelay);
        this.buttonList.add(this.chatBoxUnfocHeight);
        this.buttonList.add(this.chatFadeTicks);
        this.buttonList.add(this.textIgnoreOpacity);
        this.buttonList.add(this.convertUnicodeText);
    }

    @Override
    public void initDrawableSettings() {
        int col1x = (this.width - DISPLAY_WIDTH) / 2 + 55;

        int buttonColor = (this.bgcolor & 0x00ffffff) + 0xff000000;

        this.chatScrollHistory.setLabelLoc(col1x);
        this.chatScrollHistory.setButtonLoc(
                col1x + 5 + mc.fontRenderer.getStringWidth(this.chatScrollHistory.description),
                this.rowY(1));
        this.chatScrollHistory.setButtonDims(30, 11);

        this.maxLengthChannelName.setLabelLoc(col1x);
        this.maxLengthChannelName.setButtonLoc(
                col1x + 5 + mc.fontRenderer.getStringWidth(this.maxLengthChannelName.description),
                this.rowY(2));
        this.maxLengthChannelName.setButtonDims(20, 11);

        this.multiChatDelay.setLabelLoc(col1x);
        this.multiChatDelay.setButtonLoc(
                col1x + 5 + mc.fontRenderer.getStringWidth(this.multiChatDelay.description),
                this.rowY(3));
        this.multiChatDelay.setButtonDims(40, 11);

        this.chatBoxUnfocHeight.setLabelLoc(col1x);
        this.chatBoxUnfocHeight.setButtonLoc(
                col1x + 5 + mc.fontRenderer.getStringWidth(this.chatBoxUnfocHeight.description),
                this.rowY(4));
        this.chatBoxUnfocHeight.buttonColor = buttonColor;

        this.chatFadeTicks.setLabelLoc(col1x);
        this.chatFadeTicks.setButtonLoc(
                col1x + 5 + mc.fontRenderer.getStringWidth(this.chatFadeTicks.description),
                this.rowY(5));
        this.chatFadeTicks.buttonColor = buttonColor;
        this.chatFadeTicks.units = "";

        this.textIgnoreOpacity.setButtonLoc(col1x, this.rowY(6));
        this.textIgnoreOpacity.setLabelLoc(col1x + 19);
        this.textIgnoreOpacity.buttonColor = buttonColor;

        this.convertUnicodeText.setButtonLoc(col1x, this.rowY(7));
        this.convertUnicodeText.setLabelLoc(col1x + 19);
        this.convertUnicodeText.buttonColor = buttonColor;
    }

    @Override
    public Properties loadSettingsFile() {
        //Refreshes settings
        settings.loadSettingsFile();

        chatScrollHistory.setValue(Integer.toString(settings.chatScrollHistory));
        maxLengthChannelName.setValue(Integer.toString(settings.maxLengthChannelName));
        ChatBox.anchoredTop = settings.anchoredTop;
        convertUnicodeText.setValue(settings.convertUnicodeText);
        multiChatDelay.setValue(Integer.toString(settings.multiChatDelay));
        ChatBox.current.height = TabbyChatUtils.parseInteger(String.valueOf(settings.chatBoxHeight),
                                                             ChatBox.absMinH, 10000, 180);
        chatBoxUnfocHeight.setValue(settings.chatBoxUnfocHeight);
        ChatBox.current.y = TabbyChatUtils.parseInteger(String.valueOf(settings.chatBoxY), -10000,
                                                        ChatBox.absMinY, ChatBox.absMinY);
        ChatBox.current.x = TabbyChatUtils.parseInteger(String.valueOf(settings.chatBoxX),
                                                        ChatBox.absMinX, 10000, ChatBox.absMinX);
        textIgnoreOpacity.setValue(settings.textIgnoreOpacity);
        ChatBox.current.width = TabbyChatUtils.parseInteger(String.valueOf(settings.chatBoxWidth),
                                                            ChatBox.absMinW, 10000, 320);
        chatFadeTicks.setValue(settings.chatFadeTicks);
        ChatBox.pinned = settings.pinned;
        return null;
    }

    @Override
    public void saveSettingsFile() {
        try {
            settings.anchoredTop = ChatBox.anchoredTop;
            settings.convertUnicodeText = convertUnicodeText.getValue();
            settings.chatBoxHeight = ChatBox.current.height;
            settings.chatBoxUnfocHeight = chatBoxUnfocHeight.getValue();
            settings.chatBoxY = ChatBox.current.y;
            settings.chatBoxX = ChatBox.current.x;
            settings.textIgnoreOpacity = textIgnoreOpacity.getValue();
            settings.chatBoxWidth = ChatBox.current.width;
            settings.chatFadeTicks = chatFadeTicks.getValue();
            settings.pinned = ChatBox.pinned;
            //Save last to prevent information from getting lost if one of these values is invalid
            settings.chatScrollHistory = Integer.parseInt(chatScrollHistory.getValue());
            settings.maxLengthChannelName = Integer.parseInt(maxLengthChannelName.getValue());
            settings.multiChatDelay = Integer.parseInt(multiChatDelay.getValue());
        }
        catch (NumberFormatException e) {
            TabbyChatUtils.log.warn("Invalid format in advanced settings");
        }
        settings.saveSettingsFile();
    }
}
