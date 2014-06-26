package acs.tabbychat.gui.context;

import acs.tabbychat.core.GuiChatTC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.IIcon;

public class ContextCut extends ChatContext {

	@Override
	public void onClicked() {
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		if(screen instanceof GuiChatTC){
			GuiTextField chat = ((GuiChatTC) screen).inputField2;
			GuiScreen.setClipboardString(chat.getSelectedText());
			String text = chat.getText().replace(chat.getSelectedText(), "");
			((GuiChatTC)screen).inputField2.setText(text);
		}
	}

	@Override
	public String getDisplayString() {
		// TODO Auto-generated method stub
		return "Cut";
	}

	@Override
	public IIcon getDisplayIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPositionValid(int x, int y) {
		return true;
	}

}
