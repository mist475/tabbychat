package acs.tabbychat.api;

import net.minecraft.client.gui.GuiScreen;

/**
 * Base interface for adding additional functionality to {@code GuiChatTC}.<br/>
 * See {@code GuiScreen} for more in-depth explanations OF methods.
 */
public interface IChatExtension {
	
	/**
	 * Run once when the game starts.
	 * Can be used to check compatibility.
	 */
	void load();
	
}
