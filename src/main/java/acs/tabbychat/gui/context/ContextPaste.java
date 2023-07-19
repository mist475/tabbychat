package acs.tabbychat.gui.context;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class ContextPaste extends ChatContext {

    @Override
    public void onClicked() {
        getMenu().screen.inputField2.writeText(GuiScreen.getClipboardString());
    }

    @Override
    public ResourceLocation getDisplayIcon() {
        return new ResourceLocation("tabbychat:textures/gui/icons/paste.png");
    }

    @Override
    public String getDisplayString() {
        return "Paste";
    }

    @Override
    public boolean isPositionValid(int x, int y) {
        String clipboard = GuiScreen.getClipboardString();
        return !clipboard.isEmpty();
    }

    @Override
    public Behavior getDisabledBehavior() {
        return Behavior.GRAY;
    }

    @Override
    public List<ChatContext> getChildren() {
        return null;
    }

}
