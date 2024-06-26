package acs.tabbychat.gui;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.ColorCodeEnum;
import acs.tabbychat.settings.FormatCodeEnum;
import acs.tabbychat.settings.ITCSetting;
import acs.tabbychat.settings.NotificationSoundEnum;
import acs.tabbychat.settings.TCChatFilter;
import acs.tabbychat.settings.TCSettingBool;
import acs.tabbychat.settings.TCSettingEnum;
import acs.tabbychat.settings.TCSettingTextBox;
import acs.tabbychat.util.TabbyChatUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static acs.tabbychat.util.TabbyChatUtils.loadSettingsFromFile;

public class TCSettingsFilters extends TCSettingsGUI {
    private static final int INVERSE_MATCH_ID = 9301;
    private static final int CASE_SENSE_ID = 9302;
    private static final int HIGHLIGHT_BOOL_ID = 9303;
    private static final int HIGHLIGHT_COLOR_ID = 9304;
    private static final int HIGHLIGHT_FORMAT_ID = 9305;
    private static final int AUDIO_NOTIFICATION_BOOL_ID = 9306;
    private static final int AUDIO_NOTIFICATION_ENUM_ID = 9307;
    private static final int PREV_ID = 9308;
    private static final int NEXT_ID = 9309;
    private static final int FILTER_NAME_ID = 9310;
    private static final int SEND_TO_TAB_BOOL_ID = 9311;
    private static final int SEND_TO_TAB_NAME_ID = 9312;
    private static final int SEND_TO_ALL_TABS_ID = 9313;
    private static final int REMOVE_MATCHES_ID = 9314;
    private static final int EXPRESSION_ID = 9315;
    private static final int ADD_ID = 9316;
    private static final int DEL_ID = 9317;
    private static final int GLOBAL_ID = 9318;
    public TCSettingBool inverseMatch;
    public TCSettingBool caseSensitive;
    public TCSettingBool highlightBool;
    public TCSettingEnum highlightColor;
    public TCSettingEnum highlightFormat;
    public TCSettingBool audioNotificationBool;
    public TCSettingEnum audioNotificationSound;
    public TCSettingTextBox filterName;
    public TCSettingBool sendToTabBool;
    public TCSettingTextBox sendToTabName;
    public TCSettingBool sendToAllTabs;
    public TCSettingBool removeMatches;
    public TCSettingTextBox expressionString;
    public TCSettingBool globalFilter;
    public TreeMap<Integer, TCChatFilter> filterMap = new TreeMap<>();
    protected int curFilterId = 0;
    protected TreeMap<Integer, TCChatFilter> tempFilterMap = new TreeMap<>();

    private final File globalFiltersFile;

    public TCSettingsFilters(TabbyChat _tc) {
        super(_tc);
        this.propertyPrefix = "settings.filters";
        inverseMatch = new TCSettingBool(false, "inverseMatch",
                                         this.propertyPrefix, INVERSE_MATCH_ID);
        caseSensitive = new TCSettingBool(false, "caseSensitive",
                                          this.propertyPrefix, CASE_SENSE_ID);
        highlightBool = new TCSettingBool(true, "highlightBool",
                                          this.propertyPrefix, HIGHLIGHT_BOOL_ID);
        highlightColor = new TCSettingEnum(ColorCodeEnum.YELLOW, "highlightColor",
                                           this.propertyPrefix, HIGHLIGHT_COLOR_ID, FormatCodeEnum.ITALIC);
        highlightFormat = new TCSettingEnum(FormatCodeEnum.BOLD,
                                            "highlightFormat", this.propertyPrefix, HIGHLIGHT_FORMAT_ID, FormatCodeEnum.ITALIC);
        audioNotificationBool = new TCSettingBool(false, "audioNotificationBool",
                                                  this.propertyPrefix, AUDIO_NOTIFICATION_BOOL_ID);
        audioNotificationSound = new TCSettingEnum(NotificationSoundEnum.ORB,
                                                   "audioNotificationSound", this.propertyPrefix, AUDIO_NOTIFICATION_ENUM_ID);
        filterName = new TCSettingTextBox("New", "filterName",
                                          this.propertyPrefix, FILTER_NAME_ID);
        sendToTabBool = new TCSettingBool(false, "sendToTabBool",
                                          this.propertyPrefix, SEND_TO_TAB_BOOL_ID);
        sendToTabName = new TCSettingTextBox("", "sendToTabName",
                                             this.propertyPrefix, SEND_TO_TAB_NAME_ID);
        sendToAllTabs = new TCSettingBool(false, "sendToAllTabs",
                                          this.propertyPrefix, SEND_TO_ALL_TABS_ID);
        removeMatches = new TCSettingBool(false, "removeMatches",
                                          this.propertyPrefix, REMOVE_MATCHES_ID);
        expressionString = new TCSettingTextBox(".*", "expressionString",
                                                this.propertyPrefix, EXPRESSION_ID);
        globalFilter = new TCSettingBool(true, "globalFilter", this.propertyPrefix, GLOBAL_ID);

        this.name = I18n.format("settings.filters.name");
        this.settingsFile = new File(TabbyChatUtils.getServerDir(), "filters.cfg");
        this.globalFiltersFile = new File(ITCSettingsGUI.tabbyChatDir, "global-filters.cfg");
        this.bgcolor = 0x66289f28;
        this.filterName.setCharLimit(50);
        this.sendToTabName.setCharLimit(20);
        this.expressionString.setCharLimit(Integer.MAX_VALUE);
        this.defineDrawableSettings();
    }

    @Override
    public void actionPerformed(GuiButton button) {
        this.storeTempFilter();
        switch (button.id) {
            case ADD_ID -> {
                if (this.tempFilterMap.size() == 0)
                    this.curFilterId = 1;
                else
                    this.curFilterId = this.tempFilterMap.lastKey() + 1;
                this.tempFilterMap.put(this.curFilterId, new TCChatFilter("New" + this.curFilterId));
                this.displayCurrentFilter();
            }
            case DEL_ID -> {
                this.tempFilterMap.remove(this.curFilterId);
                if (!this.displayNextFilter())
                    this.displayPreviousFilter();
            }
            case PREV_ID -> {
                if (this.tempFilterMap.size() > 0 && !this.displayPreviousFilter()) {
                    this.curFilterId = this.tempFilterMap.lastKey();
                    this.displayCurrentFilter();
                }
            }
            case NEXT_ID -> {
                if (this.tempFilterMap.size() > 0 && !this.displayNextFilter()) {
                    this.curFilterId = this.tempFilterMap.firstKey();
                    this.displayCurrentFilter();
                }
            }
        }
        super.actionPerformed(button);
    }

    private void clearDisplay() {
        for (GuiButton drawable : this.buttonList) {
            if (drawable instanceof ITCSetting<?> setting) {
                setting.clear();
            }
        }
    }

    @Override
    public void defineDrawableSettings() {
        this.buttonList.add(this.filterName);
        this.buttonList.add(this.sendToTabBool);
        this.buttonList.add(this.sendToAllTabs);
        this.buttonList.add(this.sendToTabName);
        this.buttonList.add(this.removeMatches);
        this.buttonList.add(this.highlightBool);
        this.buttonList.add(this.highlightColor);
        this.buttonList.add(this.highlightFormat);
        this.buttonList.add(this.audioNotificationBool);
        this.buttonList.add(this.audioNotificationSound);
        this.buttonList.add(this.inverseMatch);
        this.buttonList.add(this.caseSensitive);
        this.buttonList.add(this.expressionString);
        this.buttonList.add(this.globalFilter);
    }

    private void displayCurrentFilter() {
        if (!this.tempFilterMap.containsKey(this.curFilterId)) {
            this.clearDisplay();
        }
        else {
            Properties displayMe = this.tempFilterMap.get(this.curFilterId).getProperties();
            for (GuiButton drawable : this.buttonList) {
                if (drawable instanceof ITCSetting tcDrawable) {
                    if (tcDrawable instanceof TCSettingEnum tcDrawableEnum) {
                        tcDrawableEnum.setTempValueFromProps(displayMe);
                    }
                    else {
                        tcDrawable.setTempValue(displayMe.get(tcDrawable.getProperty()));
                    }
                }
            }
        }
    }

    private boolean displayNextFilter() {
        return displayOtherFilter(true);
    }

    private boolean displayPreviousFilter() {
        return displayOtherFilter(false);
    }

    /**
     * Shows either the next or the previous filter
     * Extracted as only assignment differed
     */
    private boolean displayOtherFilter(boolean nextBool) {
        Entry<Integer, TCChatFilter> next;
        if (nextBool) {
            next = this.tempFilterMap.higherEntry(this.curFilterId);
        }
        else {
            next = this.tempFilterMap.lowerEntry(this.curFilterId);
        }
        if (next == null) {
            this.clearDisplay();
            return false;
        }
        Properties displayMe = next.getValue().getProperties();
        for (GuiButton drawable : this.buttonList) {
            //TODO: figure out way to get type safe properties to prevent unchecked cast here
            if (drawable instanceof ITCSetting tcDrawable) {
                if (tcDrawable instanceof TCSettingEnum tcEnum) {
                    tcEnum.setTempValueFromProps(displayMe);
                }
                else {
                    tcDrawable.setTempValue(displayMe.get(tcDrawable.getProperty()));
                }
            }
        }
        this.curFilterId = next.getKey();
        return true;
    }

    @Override
    public void initDrawableSettings() {
        int effRight = (this.width + DISPLAY_WIDTH) / 2;
        int col1x = (this.width - DISPLAY_WIDTH) / 2 + 55;

        int buttonColor = (this.bgcolor & 0x00ffffff) + 0xff000000;

        PrefsButton newButton = new PrefsButton(ADD_ID, col1x, (this.height + DISPLAY_HEIGHT) / 2
            - LINE_HEIGHT, 45, LINE_HEIGHT, I18n.format("settings.new"));
        PrefsButton delButton = new PrefsButton(DEL_ID, col1x + 50, (this.height + DISPLAY_HEIGHT)
            / 2 - LINE_HEIGHT, 45, LINE_HEIGHT, I18n.format("settings.delete"));
        newButton.bgcolor = this.bgcolor;
        delButton.bgcolor = this.bgcolor;
        this.buttonList.add(newButton);
        this.buttonList.add(delButton);

        this.filterName.setButtonDims(100, 11);
        this.filterName.setLabelLoc(col1x);
        this.filterName.setButtonLoc(
            col1x + 33 + mc.fontRenderer.getStringWidth(this.filterName.description),
            this.rowY(1));

        PrefsButton prevButton = new PrefsButton(PREV_ID, this.filterName.x() - 23, this.rowY(1),
                                                 20, LINE_HEIGHT, "<<");
        PrefsButton nextButton = new PrefsButton(NEXT_ID, this.filterName.x() + 103, this.rowY(1),
                                                 20, LINE_HEIGHT, ">>");
        this.buttonList.add(prevButton);
        this.buttonList.add(nextButton);

        this.sendToTabBool.setButtonLoc(col1x, this.rowY(2));
        this.sendToTabBool.setLabelLoc(col1x + 19);
        this.sendToTabBool.buttonColor = buttonColor;

        this.sendToAllTabs.setButtonLoc(col1x + 20, this.rowY(3));
        this.sendToAllTabs.setLabelLoc(col1x + 39);
        this.sendToAllTabs.buttonColor = buttonColor;

        this.sendToTabName.setLabelLoc(effRight
                                           - mc.fontRenderer.getStringWidth(this.sendToTabName.description) - 55);
        this.sendToTabName.setButtonLoc(effRight - 50, this.rowY(3));
        this.sendToTabName.setButtonDims(50, 11);

        this.removeMatches.setButtonLoc(col1x, this.rowY(4));
        this.removeMatches.setLabelLoc(col1x + 19);
        this.removeMatches.buttonColor = buttonColor;

        this.globalFilter.setLabelLoc(effRight
                                          - mc.fontRenderer.getStringWidth(this.globalFilter.description));
        this.globalFilter.setButtonLoc(
            effRight - mc.fontRenderer.getStringWidth(this.globalFilter.description) - 19,
            this.rowY(4));
        this.globalFilter.buttonColor = buttonColor;

        this.highlightBool.setButtonLoc(col1x, this.rowY(5));
        this.highlightBool.setLabelLoc(col1x + 19);
        this.highlightBool.buttonColor = buttonColor;

        this.highlightColor.setButtonDims(70, 11);
        this.highlightColor.setButtonLoc(
            col1x + 15 + mc.fontRenderer.getStringWidth(this.highlightColor.description),
            this.rowY(6));
        this.highlightColor.setLabelLoc(col1x + 10);

        this.highlightFormat.setButtonDims(60, 11);
        this.highlightFormat.setButtonLoc(effRight - 60, this.rowY(6));
        this.highlightFormat.setLabelLoc(this.highlightFormat.x() - 5
                                             - mc.fontRenderer.getStringWidth(this.highlightFormat.description));

        this.audioNotificationBool.setButtonLoc(col1x, this.rowY(7));
        this.audioNotificationBool.setLabelLoc(col1x + 19);
        this.audioNotificationBool.buttonColor = buttonColor;

        this.audioNotificationSound.setButtonDims(60, 11);
        this.audioNotificationSound.setButtonLoc(effRight - 60, this.rowY(7));
        this.audioNotificationSound.setLabelLoc(this.audioNotificationSound.x() - 5
                                                    - mc.fontRenderer.getStringWidth(this.audioNotificationSound.description));

        this.inverseMatch.setButtonLoc(col1x, this.rowY(8));
        this.inverseMatch.setLabelLoc(col1x + 19);
        this.inverseMatch.buttonColor = buttonColor;

        this.caseSensitive.setLabelLoc(effRight
                                           - mc.fontRenderer.getStringWidth(this.caseSensitive.description));
        this.caseSensitive.setButtonLoc(
            effRight - mc.fontRenderer.getStringWidth(this.caseSensitive.description) - 19,
            this.rowY(8));
        this.caseSensitive.buttonColor = buttonColor;

        this.expressionString.setLabelLoc(col1x);
        this.expressionString.setButtonLoc(
            col1x + 5 + mc.fontRenderer.getStringWidth(this.expressionString.description),
            this.rowY(9));
        this.expressionString.setButtonDims(effRight - this.expressionString.x(), 11);
        this.resetTempVars();
        this.displayCurrentFilter();
    }

    @Override
    public Properties loadSettingsFile() {
        this.filterMap.clear();

        Properties settingsTable = super.loadSettingsFile();
        settingsTable.putAll(loadSettingsFromFile(this.globalFiltersFile));
        //Get anything before a .
        Pattern pattern = Pattern.compile(".+(?=\\.)");
        //Streams don't like int due to the possibility of concurrency
        AtomicInteger count = new AtomicInteger(1);
        settingsTable
            .keySet()
            .stream()
            .map(object -> {
                Matcher matcher = pattern.matcher(object.toString());
                return matcher.find() ? matcher.group() : null;
            })
            .filter(Objects::nonNull)
            .distinct()
            .forEach(loadId -> {
                String loadName = settingsTable.getProperty(loadId + ".filterName");
                TCChatFilter loaded = new TCChatFilter(loadName);

                loaded.inverseMatch = Boolean.parseBoolean(settingsTable.getProperty(loadId
                                                                                         + ".inverseMatch"));
                loaded.caseSensitive = Boolean.parseBoolean(settingsTable.getProperty(loadId
                                                                                          + ".caseSensitive"));
                loaded.highlightBool = Boolean.parseBoolean(settingsTable.getProperty(loadId
                                                                                          + ".highlightBool"));
                loaded.highlightColor = ColorCodeEnum.cleanValueOf(settingsTable.getProperty(loadId
                                                                                                 + ".highlightColor"));
                loaded.highlightFormat = FormatCodeEnum.cleanValueOf(settingsTable.getProperty(loadId
                                                                                                   + ".highlightFormat"));
                loaded.audioNotificationBool = Boolean.parseBoolean(settingsTable.getProperty(loadId
                                                                                                  + ".audioNotificationBool"));
                loaded.audioNotificationSound = TabbyChatUtils.parseSound(settingsTable
                                                                              .getProperty(loadId + ".audioNotificationSound"));
                loaded.sendToTabBool = Boolean.parseBoolean(settingsTable.getProperty(loadId
                                                                                          + ".sendToTabBool"));
                loaded.sendToTabName = TabbyChatUtils.parseString(settingsTable.getProperty(loadId
                                                                                                + ".sendToTabName"));
                loaded.sendToAllTabs = Boolean.parseBoolean(settingsTable.getProperty(loadId
                                                                                          + ".sendToAllTabs"));
                loaded.removeMatches = Boolean.parseBoolean(settingsTable.getProperty(loadId
                                                                                          + ".removeMatches"));
                //parseBoolean returns false in case of null, which is great for backwards compatability
                loaded.globalFilter = Boolean.parseBoolean(settingsTable.getProperty(loadId + ".globalFilter"));

                loaded.compilePattern(TabbyChatUtils.parseString(settingsTable.getProperty(loadId
                                                                                               + ".expressionString")));
                filterMap.put(count.getAndIncrement(), loaded);
            });

        this.resetTempVars();
        return null;
    }

    @Override
    public void mouseClicked(int par1, int par2, int par3) {
        if (this.audioNotificationSound.hovered(par1, par2)) {
            this.audioNotificationSound.mouseClicked(par1, par2, par3);
            mc.thePlayer.playSound(
                ((NotificationSoundEnum) audioNotificationSound.getTempValue()).file(), 1.0F,
                1.0F);
        }
        else
            super.mouseClicked(par1, par2, par3);
    }

    @Override
    public void resetTempVars() {
        this.tempFilterMap.clear();
        Entry<Integer, TCChatFilter> realFilter = this.filterMap.firstEntry();
        if (realFilter != null)
            this.curFilterId = realFilter.getKey();
        while (realFilter != null) {
            this.tempFilterMap.put(realFilter.getKey(), new TCChatFilter(realFilter.getValue()));
            realFilter = this.filterMap.higherEntry(realFilter.getKey());
        }
    }

    @Override
    public void saveSettingsFile() {
        Map<Boolean, List<TCChatFilter>> divideOnGlobal = this.filterMap
            .values()
            .stream()
            .collect(Collectors
                         .groupingBy(filter -> filter.globalFilter));

        Properties globalFiltersTable = new Properties();
        divideOnGlobal.getOrDefault(true, new ArrayList<>())
            .forEach(tcChatFilter -> saveIntoProperties(tcChatFilter, globalFiltersTable));

        Properties settingsTable = new Properties();
        divideOnGlobal.getOrDefault(false, new ArrayList<>())
            .forEach(tcChatFilter -> saveIntoProperties(tcChatFilter, settingsTable));

        List<GuiButton> tmpList = new ArrayList<>(this.buttonList);
        this.buttonList.clear();
        savePropertiesIntoFile(globalFiltersTable, globalFiltersFile);
        super.saveSettingsFile(settingsTable);
        this.buttonList = tmpList;
    }

    private static void saveIntoProperties(TCChatFilter saveFilter, Properties properties) {
        UUID saveId = UUID.randomUUID();
        properties.put(saveId + ".filterName", saveFilter.filterName);
        properties.put(saveId + ".inverseMatch",
                       Boolean.toString(saveFilter.inverseMatch));
        properties.put(saveId + ".caseSensitive",
                       Boolean.toString(saveFilter.caseSensitive));
        properties.put(saveId + ".highlightBool",
                       Boolean.toString(saveFilter.highlightBool));
        properties.put(saveId + ".audioNotificationBool",
                       Boolean.toString(saveFilter.audioNotificationBool));
        properties.put(saveId + ".sendToTabBool",
                       Boolean.toString(saveFilter.sendToTabBool));
        properties.put(saveId + ".sendToAllTabs",
                       Boolean.toString(saveFilter.sendToAllTabs));
        properties.put(saveId + ".removeMatches",
                       Boolean.toString(saveFilter.removeMatches));
        properties.put(saveId + ".highlightColor",
                       saveFilter.highlightColor.name());
        properties.put(saveId + ".highlightFormat",
                       saveFilter.highlightFormat.name());
        properties.put(saveId + ".audioNotificationSound",
                       saveFilter.audioNotificationSound.name());
        properties.put(saveId + ".sendToTabName", saveFilter.sendToTabName);
        properties.put(saveId + ".expressionString", saveFilter.expressionString);
        properties.put(saveId + ".globalFilter", Boolean.toString(saveFilter.globalFilter));
    }

    private void storeTempFilter() {
        if (this.tempFilterMap.containsKey(this.curFilterId)) {
            TCChatFilter storeMe = this.tempFilterMap.get(this.curFilterId);
            storeMe.filterName = this.filterName.getTempValue();
            storeMe.inverseMatch = this.inverseMatch.getTempValue();
            storeMe.caseSensitive = this.caseSensitive.getTempValue();
            storeMe.highlightBool = this.highlightBool.getTempValue();
            storeMe.highlightColor = ColorCodeEnum.valueOf(this.highlightColor.getTempValue()
                                                               .name());
            storeMe.highlightFormat = FormatCodeEnum.valueOf(this.highlightFormat.getTempValue()
                                                                 .name());
            storeMe.audioNotificationBool = this.audioNotificationBool.getTempValue();
            storeMe.audioNotificationSound = NotificationSoundEnum
                .valueOf(this.audioNotificationSound.getTempValue().name());
            storeMe.sendToTabBool = this.sendToTabBool.getTempValue();
            storeMe.sendToAllTabs = this.sendToAllTabs.getTempValue();
            storeMe.sendToTabName = this.sendToTabName.getTempValue();
            storeMe.removeMatches = this.removeMatches.getTempValue();
            storeMe.expressionString = this.expressionString.getTempValue();
            storeMe.globalFilter = this.globalFilter.getTempValue();
        }

    }

    @Override
    public void storeTempVars() {
        this.filterMap.clear();

        Entry<Integer, TCChatFilter> tempFilter = this.tempFilterMap.firstEntry();
        while (tempFilter != null) {
            this.filterMap.put(tempFilter.getKey(), new TCChatFilter(tempFilter.getValue()));
            tempFilter = this.tempFilterMap.higherEntry(tempFilter.getKey());
        }
    }

    public void updateForServer() {
        this.settingsFile = new File(TabbyChatUtils.getServerDir(), "filters.cfg");
    }

    @Override
    public void validateButtonStates() {
        this.inverseMatch.enabled = !this.highlightBool.getTempValue();
        this.caseSensitive.enabled = true;

        this.highlightBool.enabled = !this.removeMatches.getTempValue()
            && !this.inverseMatch.getTempValue();
        this.audioNotificationBool.enabled = !this.removeMatches.getTempValue();
        this.removeMatches.enabled = !this.sendToTabBool.getTempValue()
            && !this.highlightBool.getTempValue() && !this.audioNotificationBool.getTempValue();
        this.sendToTabBool.enabled = !this.removeMatches.getTempValue();

        this.highlightColor.enabled = this.highlightBool.getTempValue();
        this.highlightFormat.enabled = this.highlightBool.getTempValue();
        this.audioNotificationSound.enabled = this.audioNotificationBool.getTempValue();
        this.sendToAllTabs.enabled = this.sendToTabBool.getTempValue();
        this.globalFilter.enabled = true;

        for (GuiButton o : this.buttonList) {
            if (o instanceof ITCSetting<?> tmp) {
                if (this.tempFilterMap.size() == 0)
                    tmp.disable();
                else if (tmp.getType() == ITCSetting.TCSettingType.TEXTBOX)
                    tmp.enable();
                else if (tmp instanceof TCSettingBool tmpBool) {
                    tmpBool.setTempValue(tmpBool.getTempValue()
                                             && tmpBool.enabled());
                }
            }
        }
        this.sendToTabName.func_146184_c(this.sendToTabBool.getTempValue()
                                             && !this.sendToAllTabs.getTempValue());
    }

}
