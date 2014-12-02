package acs.tabbychat.settings;

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.util.TabbyChatUtils;

public class TCChatFilter {
    public boolean inverseMatch = false;
    public boolean caseSensitive = false;
    public boolean highlightBool = true;
    public boolean audioNotificationBool = false;
    public boolean sendToTabBool = false;
    public boolean sendToAllTabs = false;
    public boolean removeMatches = false;

    public ColorCodeEnum highlightColor = ColorCodeEnum.YELLOW;
    public FormatCodeEnum highlightFormat = FormatCodeEnum.BOLD;
    public NotificationSoundEnum audioNotificationSound = NotificationSoundEnum.ORB;
    {
        this.highlightBool = false;
    }

    public String sendToTabName = "";
    public String expressionString = ".*";

    public Pattern expressionPattern = Pattern.compile(this.expressionString);
    // private static final Pattern allFormatCodes =
    // Pattern.compile("(?i)(\\u00A7[0-9A-FK-OR])+");
    public String filterName;
    private int[] lastMatch;
    private String tabName = null;

    public TCChatFilter(String name) {
        this.filterName = name;
    }

    public TCChatFilter(TCChatFilter orig) {
        this(orig.filterName);
        this.copyFrom(orig);
    }

    public boolean applyFilterToDirtyChat(IChatComponent input) {

        // Apply filter
        Matcher findFilterMatches = this.expressionPattern.matcher(input.getUnformattedText());
        boolean foundMatch = false;
        List<Integer> list = Lists.newArrayList();
        while (findFilterMatches.find()) {

            foundMatch = true;
            // If highlighting, store desired locations for format codes
            if (this.highlightBool) {
                list.add(findFilterMatches.start());
                list.add(findFilterMatches.end());
            } else
                break;
        }
        this.lastMatch = ArrayUtils.toPrimitive(list.toArray(new Integer[list.size()]));

        // Pull name of destination tab
        if (this.sendToTabBool && !this.sendToAllTabs) {
            if (this.inverseMatch)
                this.tabName = this.sendToTabName;
            else if (this.sendToTabName.startsWith("%")) {
                int group = TabbyChatUtils.parseInteger(this.sendToTabName.substring(1));
                if (foundMatch && group >= 0 && findFilterMatches.groupCount() >= group) {
                    this.tabName = findFilterMatches.group(group);
                    if (this.tabName == null)
                        this.tabName = this.filterName;
                } else {
                    this.tabName = this.filterName;
                }
            } else {
                this.tabName = this.sendToTabName;
            }
        } else {
            this.tabName = null;
        }

        // Return result status of filter application
        return (!foundMatch && inverseMatch) || (foundMatch && !inverseMatch);

    }

    public void audioNotification() {
        Minecraft.getMinecraft().thePlayer
                .playSound(this.audioNotificationSound.file(), 1.0F, 1.0F);
    }

    public void compilePattern() {
        try {
            if (this.caseSensitive)
                this.expressionPattern = Pattern.compile(this.expressionString);
            else
                this.expressionPattern = Pattern.compile(this.expressionString,
                        Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            TabbyChat.printMessageToChat("Invalid expression entered for filter '"
                    + this.filterName + "', resetting to default.");
            this.expressionString = ".*";
            this.expressionPattern = Pattern.compile(this.expressionString);
        }
    }

    public void compilePattern(String newExpression) {
        this.expressionString = newExpression;
        this.compilePattern();
    }

    public void copyFrom(TCChatFilter orig) {
        this.filterName = orig.filterName;
        this.inverseMatch = orig.inverseMatch;
        this.caseSensitive = orig.caseSensitive;
        this.highlightBool = orig.highlightBool;
        this.audioNotificationBool = orig.audioNotificationBool;
        this.sendToTabBool = orig.sendToTabBool;
        this.sendToAllTabs = orig.sendToAllTabs;
        this.removeMatches = orig.removeMatches;
        this.highlightColor = orig.highlightColor;
        this.highlightFormat = orig.highlightFormat;
        this.audioNotificationSound = orig.audioNotificationSound;
        this.sendToTabName = orig.sendToTabName;
        this.expressionString = orig.expressionString;

        this.compilePattern();
    }

    /**
     * Returns an array containing indexes of the starts and ends of the current
     * filter.
     * Will always be even and be in order. <br />
     * <code>{start1, end1, start2, end2...}</code>
     */
    public int[] getLastMatch() {
        int[] tmp = this.lastMatch;
        this.lastMatch = null;
        return tmp;
    }

    public Properties getProperties() {
        Properties myProps = new Properties();
        myProps.put("filterName", this.filterName);
        myProps.put("inverseMatch", this.inverseMatch);
        myProps.put("caseSensitive", this.caseSensitive);
        myProps.put("highlightBool", this.highlightBool);
        myProps.put("audioNotificationBool", this.audioNotificationBool);
        myProps.put("sendToTabBool", this.sendToTabBool);
        myProps.put("sendToAllTabs", this.sendToAllTabs);
        myProps.put("removeMatches", this.removeMatches);
        myProps.put("highlightColor", this.highlightColor.name());
        myProps.put("highlightFormat", this.highlightFormat.name());
        myProps.put("audioNotificationSound", this.audioNotificationSound.name());
        myProps.put("sendToTabName", this.sendToTabName);
        myProps.put("expressionString", this.expressionString);
        return myProps;
    }

    public String getTabName() {
        String tmp = this.tabName;
        this.tabName = null;
        return tmp;
    }
}
