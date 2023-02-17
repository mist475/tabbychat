package acs.tabbychat.api;

import net.minecraft.client.gui.GuiScreen;

public interface IChatUpdateExtension extends IChatExtension {

    /**
     * Run every time the chat is opened
     *
     * @param screen The instance of the GuiScreen
     */
    void initGui(GuiScreen screen);

    void updateScreen();

    /**
     * Runs when the chat is closed
     */
    void onGuiClosed();
}
