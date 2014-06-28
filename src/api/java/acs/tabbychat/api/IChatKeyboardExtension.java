package acs.tabbychat.api;

public interface IChatKeyboardExtension extends IChatExtension {

	/**
	 * if returns true, nothing else will be clicked
	 */
	boolean keyTyped(char c, int code);
	
	void handleKeyboardInput();
}
