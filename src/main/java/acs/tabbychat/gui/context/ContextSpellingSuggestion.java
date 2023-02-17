package acs.tabbychat.gui.context;

import acs.tabbychat.core.TabbyChat;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class ContextSpellingSuggestion extends ChatContext {

    private String[] suggestions;
    private String title;

    @Override
    public void onClicked() {

    }

    @Override
    public String getDisplayString() {
        return title;
    }

    @Override
    public ResourceLocation getDisplayIcon() {
        return null;
    }

    @Override
    public List<ChatContext> getChildren() {
        List<ChatContext> list = Lists.newArrayList();
        if (suggestions == null) {
            return null;
        }
        for (final String word : suggestions) {
            list.add(makeBaby(word));
        }
        return list;
    }

    @Override
    public boolean isPositionValid(int x, int y) {
        this.title = "Spelling";
        GuiTextField text = getMenu().screen.inputField2;
        int start = text.getNthWordFromCursor(-1);
        int end = text.getNthWordFromCursor(1);
        String word = text.getText().substring(start, end);
        if (!word.isEmpty()) {
            if (!TabbyChat.spellChecker.isSpelledCorrectly(word)) {
                List<String> suggs = TabbyChat.spellChecker.getSuggestions(word, 0);
                suggestions = objectToStringArray(suggs.toArray());
                if (suggestions.length == 0) {
                    this.title = "No Suggestions";
                    return false;
                }
                return true;
            }
        }
        suggestions = null;
        return false;
    }

    private String[] objectToStringArray(Object[] object) {
        String[] array = new String[object.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = object[i].toString();
        }
        return array;
    }

    @Override
    public Behavior getDisabledBehavior() {
        if (suggestions == null)
            return Behavior.HIDE;
        else
            return Behavior.GRAY;
    }

    // Sexy time for spell checker
    private ChatContext makeBaby(final String word) {
        return new ChatContext() {

            @Override
            public void onClicked() {
                GuiTextField field = getMenu().screen.inputField2;
                int start = field.getNthWordFromCursor(-1);
                int end = field.getNthWordFromCursor(1);
                field.setCursorPosition(start);
                field.setSelectionPos(end);
                String sel = field.getSelectedText();
                char pref = sel.charAt(0);
                char suff = sel.charAt(sel.length() - 1);
                if (Character.isLetter(pref))
                    pref = 0;
                if (Character.isLetter(suff))
                    suff = ' ';
                this.getMenu().screen.inputField2.writeText((pref != 0 ? pref : "") + word + suff);
            }

            @Override
            public boolean isPositionValid(int x, int y) {
                return true;
            }

            @Override
            public String getDisplayString() {
                return word;
            }

            @Override
            public ResourceLocation getDisplayIcon() {
                return null;
            }

            @Override
            public Behavior getDisabledBehavior() {
                return Behavior.GRAY;
            }

            @Override
            public List<ChatContext> getChildren() {
                return null;
            }
        };
    }
}
