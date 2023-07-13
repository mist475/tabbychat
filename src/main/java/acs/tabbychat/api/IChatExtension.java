package acs.tabbychat.api;

/**
 * Base interface for adding additional functionality to {@link acs.tabbychat.core.GuiChatTC}.<br/>
 * See {@link net.minecraft.client.gui.GuiScreen} for more in-depth explanations OF methods.
 */
public interface IChatExtension {

    /**
     * Run once when the game starts.
     * Can be used to check compatibility.
     */
    void load();

}
