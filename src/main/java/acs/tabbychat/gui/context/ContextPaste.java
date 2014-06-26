package acs.tabbychat.gui.context;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import acs.tabbychat.core.GuiChatTC;

public class ContextPaste extends ChatContext {

	@Override
	public void actionPreformed() {
		String clipboard = GuiScreen.getClipboardString();
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		if(screen instanceof GuiChatTC){
			((GuiChatTC)screen).inputField2.writeText(clipboard);
		}
	}

	@Override
	public IIcon getDisplayIcon() {
		IIcon icon = Minecraft.getMinecraft().getTextureMapBlocks().registerIcon("tabbychat:textures/gui/context/paste.png");
		return icon;
	}

	@Override
	public String getDisplayString() {
		// TODO Auto-generated method stub
		return "paste";
	}

	@Override
	public boolean isLocationValid(int x, int y) {
		return true;
	}

}
