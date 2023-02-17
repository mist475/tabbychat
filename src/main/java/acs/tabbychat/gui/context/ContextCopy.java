package acs.tabbychat.gui.context;

import acs.tabbychat.core.GuiChatTC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class ContextCopy extends ChatContext {

    @Override
    public void onClicked() {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiChatTC) {
            GuiScreen.setClipboardString(((GuiChatTC) screen).inputField2.getSelectedText());
        }
    }

    @Override
    public String getDisplayString() {
        return "Copy";
    }

    @Override
    public ResourceLocation getDisplayIcon() {
        return new ResourceLocation("tabbychat:textures/gui/icons/copy.png");
    }

    @Override
    public boolean isPositionValid(int x, int y) {
        GuiTextField text = menu.screen.inputField2;
        return text != null && !text.getSelectedText().isEmpty();
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
