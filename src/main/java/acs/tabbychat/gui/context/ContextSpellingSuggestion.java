package acs.tabbychat.gui.context;

import java.util.List;

import com.google.common.collect.Lists;
import com.swabunga.spell.event.SpellChecker;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.gui.context.ChatContext.Behavior;
import acs.tabbychat.jazzy.TCSpellCheckListener;
import acs.tabbychat.jazzy.TCSpellCheckManager;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

public class ContextSpellingSuggestion extends ChatContext {

	private String[] suggestions;
	
	@Override
	public void onClicked() {

	}

	@Override
	public String getDisplayString() {
		return "Spelling";
	}

	@Override
	public ResourceLocation getDisplayIcon() {
		return null;
	}

	@Override
	public List<ChatContext> getChildren() {
		List<ChatContext> list = Lists.newArrayList();
		if(suggestions == null)
			list.add(makeBaby("No suggestions", false));
		else
			for(final String word : suggestions){
				list.add(makeBaby(word, true));
			}
		return list;
	}

	@Override
	public boolean isPositionValid(int x, int y) {
		GuiTextField text = getMenu().screen.inputField2;
		int start = text.getNthWordFromCursor(-1);
		int end = text.getNthWordFromCursor(1);
		String word = text.getText().substring(start,end);
		if(word != null && !word.isEmpty()){
			if(!TabbyChat.spellChecker.isSpelledCorrectly(word)){
				List<String> suggs = TabbyChat.spellChecker.getSuggestions(word, 5);
				suggestions = suggs.toArray(new String[suggs.size()]);
				text.setCursorPosition(start);
				text.setSelectionPos(end);
				return suggestions.length > 0;
			}
		}
		suggestions = null;
		return false;
	}
	
	@Override
	public Behavior getDisabledBehavior() {
		if(suggestions == null)
			return Behavior.HIDE;
		else
			return Behavior.GRAY;
	}

	// Sexy time for spell checker ಠ_ಠ
	private ChatContext makeBaby(final String word, final boolean enabled){
		return new ChatContext() {
			
			@Override
			public void onClicked() {
				this.getMenu().screen.inputField2.writeText(word);
			}
			
			@Override
			public boolean isPositionValid(int x, int y) {
				return enabled;
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
