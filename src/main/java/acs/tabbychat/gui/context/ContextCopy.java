package acs.tabbychat.gui.context;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IIcon;
import acs.tabbychat.core.GuiChatTC;

public class ContextCopy extends ChatContext {

	@Override
	public void onClicked() {
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		if(screen instanceof GuiChatTC){
			GuiScreen.setClipboardString(((GuiChatTC)screen).inputField2.getSelectedText());
		}
	}

	@Override
	public String getDisplayString() {
		return "Copy";
	}

	@Override
	public IIcon getDisplayIcon() {
		return null;
	}

	@Override
	public boolean isPositionValid(int x, int y) {
		return true;
	}

}
