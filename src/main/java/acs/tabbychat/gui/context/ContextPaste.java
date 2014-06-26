package acs.tabbychat.gui.context;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import acs.tabbychat.core.GuiChatTC;

public class ContextPaste extends ChatContext {

	@Override
	public void onClicked() {
		parent.screen.inputField2.writeText(GuiScreen.getClipboardString());
	}

	@Override
	public IIcon getDisplayIcon() {
		return null;
	}

	@Override
	public String getDisplayString() {
		// TODO Auto-generated method stub
		return "Paste";
	}

	@Override
	public boolean isPositionValid(int x, int y) {
		String clipboard = GuiScreen.getClipboardString();
		return clipboard != null && !clipboard.isEmpty();
	}

}
