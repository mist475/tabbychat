package acs.tabbychat.gui.context;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.IIcon;

public class ContextCopy extends ChatContext {

	@Override
	public void actionPreformed() {
		
	}

	@Override
	public String getDisplayString() {
		return "Copy";
	}

	@Override
	public IIcon getDisplayIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLocationValid(int x, int y) {
		return true;
	}

}
