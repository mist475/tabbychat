package acs.tabbychat.api;

import net.minecraft.client.gui.GuiButton;

public interface IChatMouseExtension extends IChatExtension {
	/**
	 * if returns true, nothing else will be clicked.
	 */
	boolean mouseClicked(int x, int y, int button);

	/**
	 * if returns true, nothing else will be clicked.
	 */
	boolean actionPerformed(GuiButton button);
	
	void handleMouseInput();
}
