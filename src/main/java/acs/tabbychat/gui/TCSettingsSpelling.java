package acs.tabbychat.gui;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.TCSettingBool;
import acs.tabbychat.settings.TCSettingList;
import acs.tabbychat.settings.TCSettingList.Entry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class TCSettingsSpelling extends TCSettingsGUI {

    private static final int SPELL_CHECK_ENABLE = 9108;
    private static final int ADD_WORD = 9502;
    private static final int REMOVE_WORD = 9503;
    private static final int CLEAR_WORDS = 9504;

    private static final int NEXT = 9506;
    private static final int PREV = 9507;
    private static final int OPEN = 9508;
    private static final int RELOAD = 9509;

    {
        this.propertyPrefix = "settings.spelling";
    }

    public TCSettingBool spellCheckEnable = new TCSettingBool(true, "spellCheckEnable",
            this.propertyPrefix, SPELL_CHECK_ENABLE);
    private GuiTextField wordInput;
    private PrefsButton addWord = new PrefsButton(ADD_WORD, 0, 0, 15, 12, ">");
    private PrefsButton removeWords = new PrefsButton(REMOVE_WORD, 0, 0, 15, 12, "<");
    private PrefsButton clearWords = new PrefsButton(CLEAR_WORDS, 0, 0, 15, 12, "<<");
    private PrefsButton next = new PrefsButton(NEXT, 0, 0, 15, 12, "->");
    private PrefsButton prev = new PrefsButton(PREV, 0, 0, 15, 12, "<-");
    private PrefsButton open = new PrefsButton(OPEN, 0, 0, 85, 15, "");
    private PrefsButton reload = new PrefsButton(RELOAD, 0, 0, 85, 15, "");

    private File dictionary = new File(tabbyChatDir, "dictionary.txt");
    public TCSettingList spellingList = new TCSettingList(dictionary);

    public TCSettingsSpelling(TabbyChat _tc) {
        super(_tc);
        this.name = I18n.format("settings.spelling.name");
        this.settingsFile = new File(tabbyChatDir, "spellcheck.cfg");
        this.bgcolor = 0x66ffb62f;
        this.defineDrawableSettings();
    }

    @Override
    public void saveSettingsFile() {
        try {
            this.spellingList.saveEntries();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.saveSettingsFile();
    }

    @Override
    public Properties loadSettingsFile() {
        super.loadSettingsFile();
        try {
            dictionary.getParentFile().mkdirs();
            dictionary.createNewFile();
            spellingList.loadEntries();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void defineDrawableSettings() {
        this.buttonList.add(this.spellCheckEnable);
        this.buttonList.add(addWord);
        this.buttonList.add(removeWords);
        this.buttonList.add(clearWords);
        this.buttonList.add(next);
        this.buttonList.add(prev);
        this.buttonList.add(open);
        this.buttonList.add(reload);
    }

    @Override
    public void initDrawableSettings() {
        int col1x = (this.width - DISPLAY_WIDTH) / 2 + 55;
        int col2x = this.width / 2 + 25;

        int buttonColor = (this.bgcolor & 0x00ffffff) + 0xff000000;

        this.spellCheckEnable.setButtonLoc(col1x, this.rowY(1));
        this.spellCheckEnable.setLabelLoc(col1x + 19);
        this.spellCheckEnable.buttonColor = buttonColor;

        this.spellingList.x(col2x);
        this.spellingList.y(rowY(4));
        this.spellingList.width(100);
        this.spellingList.height(96);

        this.wordInput = new GuiTextField(mc.fontRenderer, col1x, rowY(6), 75, 12);
        this.wordInput.setCanLoseFocus(true);

        this.open.displayString = I18n.format("settings.spelling.opendictionary");
        this.open.x(col1x);
        this.open.y(rowY(10));

        this.reload.displayString = I18n.format("settings.spelling.reloaddictionary");
        this.reload.x(col1x);
        this.reload.y(rowY(9));

        this.addWord.x(col2x - 25);
        this.addWord.y(rowY(5));

        this.removeWords.x(col2x - 25);
        this.removeWords.y(rowY(6));

        this.clearWords.x(col2x - 25);
        this.clearWords.y(rowY(7));

        this.next.x(col2x + 53);
        this.next.y(rowY(10));

        this.prev.x(col2x + 33);
        this.prev.y(rowY(10));

    }

    @Override
    public void drawScreen(int x, int y, float f) {
        int col1x = (this.width - DISPLAY_WIDTH) / 2 + 55;
        int col2x = this.width / 2 + 45;
        super.drawScreen(x, y, f);
        this.wordInput.drawTextBox();
        this.spellingList.drawList(mc, x, y);
        this.drawString(fontRendererObj, I18n.format("settings.spelling.userdictionary"), col1x,
                rowY(3), 0xffffff);
        this.drawString(
                fontRendererObj,
                I18n.format("book.pageIndicator", this.spellingList.getPageNum(),
                        this.spellingList.getTotalPages()), col2x, rowY(3), 0xffffff);
    }

    @Override
    public void initGui() {
        super.initGui();
        try {
            spellingList.loadEntries();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        this.wordInput.mouseClicked(x, y, button);
        this.spellingList.mouseClicked(x, y, button);
    }

    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);
        this.wordInput.textboxKeyTyped(c, i);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
        case ADD_WORD:
            this.spellingList.addToList(this.wordInput.getText());
            this.wordInput.setText("");
            break;
        case REMOVE_WORD:
            for (Entry entry : this.spellingList.getSelected()) {
                entry.remove();
            }
            break;
        case CLEAR_WORDS:
            this.spellingList.clearList();
            break;
        case NEXT:
            this.spellingList.nextPage();
            break;
        case PREV:
            this.spellingList.previousPage();
            break;
        case OPEN:
            try {
                if (Desktop.isDesktopSupported())
                    Desktop.getDesktop().open(dictionary);
            } catch (IOException e) {
            }
            break;
        case RELOAD:
            try {
                this.spellingList.loadEntries();
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        }
        super.actionPerformed(button);
    }
}
