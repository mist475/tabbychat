package acs.tabbychat.settings.files;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.gui.ChatBox;
import acs.tabbychat.util.TabbyChatUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Data structure for advanced settings
 */
public class TCSettingsAdvancedFile {
    private final File settingsFile = new File(TabbyChatUtils.getTabbyChatDir(), "advanced.cfg");
    private final String chatScrollHistoryPropertyName = "chatScrollHistory";
    private final String maxLengthChannelNamePropertyName = "maxLengthChannelName";
    private final String anchoredTopPropertyName = "chatbox.anchoredTop";
    private final String convertUnicodeTextPropertyName = "convertunicodetext";
    private final String multiChatDelayPropertyName = "multiChatDelay";
    private final String chatBoxHeightPropertyName = "chatbox.height";
    private final String chatBoxUnfocHeightPropertyName = "chatBoxUnfocHeight";
    private final String chatBoxYPropertyName = "chatbox.y";
    private final String chatBoxXPropertyName = "chatbox.x";
    private final String textIgnoreOpacityPropertyName = "textignoreopacity";
    private final String chatBoxWidthPropertyName = "chatbox.width";
    private final String chatFadeTicksPropertyName = "chatFadeTicks";
    private final String pinnedPropertyName = "pinchatinterface";
    //Default settings

    public int chatScrollHistory = 100;
    public int maxLengthChannelName = 10;
    public boolean anchoredTop = false;
    public boolean convertUnicodeText = false;
    public int multiChatDelay = 500;
    public int chatBoxHeight = 180;
    public float chatBoxUnfocHeight = 50.0f;
    public int chatBoxY = -36;
    public int chatBoxX = 0;
    public boolean textIgnoreOpacity = true;
    public int chatBoxWidth = 320;
    public float chatFadeTicks = 200.0f;
    public boolean pinned = false;

    public TCSettingsAdvancedFile() {

    }

    public void loadSettingsFile() {
        Properties settingsTable = new Properties();
        //Return default settings
        if (!settingsFile.exists()) {
            return;
        }

        try (FileInputStream fInStream = new FileInputStream(settingsFile); BufferedInputStream bInStream = new BufferedInputStream(fInStream)) {
            settingsTable.load(bInStream);
        }
        catch (Exception e) {
            TabbyChat.printException("Error while reading settings from file '" + settingsFile
                                             + "'", e);
        }

        try {
            chatScrollHistory = Integer.parseInt(settingsTable.getProperty(chatScrollHistoryPropertyName));
            maxLengthChannelName = Integer.parseInt(settingsTable.getProperty(maxLengthChannelNamePropertyName));
            anchoredTop = Boolean.parseBoolean(settingsTable.getProperty(anchoredTopPropertyName));
            convertUnicodeText = Boolean.parseBoolean(settingsTable.getProperty(convertUnicodeTextPropertyName));
            multiChatDelay = Integer.parseInt(settingsTable.getProperty(multiChatDelayPropertyName));
            chatBoxHeight = TabbyChatUtils.parseInteger(settingsTable.getProperty(chatBoxHeightPropertyName),
                                                        ChatBox.absMinH, 10000, 180);
            chatBoxUnfocHeight = Float.parseFloat(settingsTable.getProperty(chatBoxUnfocHeightPropertyName));
            chatBoxX = TabbyChatUtils.parseInteger(settingsTable.getProperty(chatBoxXPropertyName),
                                                   ChatBox.absMinX, 10000, ChatBox.absMinX);
            chatBoxY = TabbyChatUtils.parseInteger(settingsTable.getProperty(chatBoxYPropertyName), -10000,
                                                   ChatBox.absMinY, ChatBox.absMinY);
            textIgnoreOpacity = Boolean.parseBoolean(settingsTable.getProperty(textIgnoreOpacityPropertyName));
            chatBoxWidth = TabbyChatUtils.parseInteger(settingsTable.getProperty(chatBoxWidthPropertyName),
                                                       ChatBox.absMinW, 10000, 320);
            chatFadeTicks = Float.parseFloat(settingsTable.getProperty(chatFadeTicksPropertyName));
            pinned = Boolean.parseBoolean(settingsTable.getProperty(pinnedPropertyName));
        }
        catch (NumberFormatException e) {
            TabbyChatUtils.log.warn("Failed to load (part of) advanced settings, using defaults");
        }

    }


    public void saveSettingsFile() {
        Properties settingsTable = new Properties();
        settingsTable.put(chatScrollHistoryPropertyName, Integer.toString(chatScrollHistory));
        settingsTable.put(maxLengthChannelNamePropertyName, Integer.toString(maxLengthChannelName));
        settingsTable.put(anchoredTopPropertyName, Boolean.toString(anchoredTop));
        settingsTable.put(convertUnicodeTextPropertyName, Boolean.toString(convertUnicodeText));
        settingsTable.put(multiChatDelayPropertyName, Integer.toString(multiChatDelay));
        settingsTable.put(chatBoxHeightPropertyName, Integer.toString(chatBoxHeight));
        settingsTable.put(chatBoxUnfocHeightPropertyName, Float.toString(chatBoxUnfocHeight));
        settingsTable.put(chatBoxYPropertyName, Integer.toString(chatBoxY));
        settingsTable.put(chatBoxXPropertyName, Integer.toString(chatBoxX));
        settingsTable.put(textIgnoreOpacityPropertyName, Boolean.toString(textIgnoreOpacity));
        settingsTable.put(chatBoxWidthPropertyName, Integer.toString(chatBoxWidth));
        settingsTable.put(chatFadeTicksPropertyName, Float.toString(chatFadeTicks));
        settingsTable.put(pinnedPropertyName, Boolean.toString(pinned));
        TabbyChatUtils.saveProperties(settingsTable, this.settingsFile, Pair.of("Created parent dir for advanced tabbychat settings", "Failed to create parent directory for advanced tabbychat settings"), "settings.advanced");
    }
}
