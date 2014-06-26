package acs.tabbychat.gui.context;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.IIcon;

public class ContextCut extends ChatContext {

	@Override
	public void actionPreformed() {
		// TODO Auto-generated method stub

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
	public boolean isLocationValid(int x, int y) {
		return true;
	}

}
