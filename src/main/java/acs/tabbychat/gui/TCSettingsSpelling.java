package acs.tabbychat.gui;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.TCSettingList;
import acs.tabbychat.settings.TCSettingList.Entry;
import acs.tabbychat.settings.TCSettingTextBox;

public class TCSettingsSpelling extends TCSettingsGUI {

	private static final int WORD_INPUT = 9501;
	
	{
		this.propertyPrefix = "settings.spelling";
	}
	
	private TCSettingTextBox wordInput = new TCSettingTextBox("", "", "", WORD_INPUT);
	private PrefsButton addWord = new PrefsButton();
	private PrefsButton removeWords = new PrefsButton();
	private PrefsButton clearWords = new PrefsButton();
	
	public TCSettingList spellingList;
	
	private File dictionary = new File(tabbyChatDir, "dictionary.txt");
	
	public TCSettingsSpelling(TabbyChat _tc) {
		super(_tc);
		try {
			spellingList = new TCSettingList(dictionary);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.name = I18n.format("settings.spelling.name");
		this.settingsFile = new File(tabbyChatDir, "spellcheck.cfg");
		this.bgcolor = 0xffcc00be;
		this.defineDrawableSettings();
	}
	
	@Override
	public void defineDrawableSettings(){
		this.buttonList.add(wordInput);
		this.buttonList.add(addWord);
		this.buttonList.add(removeWords);
		this.buttonList.add(clearWords);
	}
	
	@Override
	public void initDrawableSettings(){
		this.addWord.displayString = ">";
		this.addWord.width(20);
		
		this.removeWords.displayString = "<";
		this.removeWords.width(20);
		
		this.clearWords.displayString = "<<";
		this.clearWords.width(20);
	}
	
	@Override
	public void onGuiClosed(){
		try {
			this.spellingList.saveEntries(dictionary);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button){
		super.actionPerformed(button);
		
		if(button.equals(this.addWord)){
			this.spellingList.addToList(this.wordInput.getValue());
			this.wordInput.setValue("");
		} else if(button.equals(this.removeWords)){
			for(Entry entry : this.spellingList.getSelected()){
				entry.setSelected(false);
				entry.remove();
			}
		}else if(button.equals(this.clearWords)){
			this.spellingList.clearList();
		}
	}

}
