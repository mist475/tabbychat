package acs.tabbychat.gui.context;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class ContextCut extends ChatContext {

    @Override
    public void onClicked() {
        GuiTextField chat = getMenu().screen.inputField;
        GuiScreen.setClipboardString(chat.getSelectedText());
        String text = chat.getText().replace(chat.getSelectedText(), "");
        chat.setText(text);
    }

    @Override
    public String getDisplayString() {
        return "Cut";
    }

    @Override
    public ResourceLocation getDisplayIcon() {
        return new ResourceLocation("tabbychat:textures/gui/icons/cut.png");
    }

    @Override
    public boolean isPositionValid(int x, int y) {
        GuiTextField text = getMenu().screen.inputField;
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
