package acs.tabbychat.util;

import com.google.gson.annotations.Expose;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class TCChatLineFake extends ChatLine {
    //TODO: experiment with old chat messages to check if I can remove this without consequences
    @Expose
    protected IChatComponent chatComponent;

    public TCChatLineFake() {
        super(-1, new ChatComponentText(""), 0);
    }

    public TCChatLineFake(int _counter, IChatComponent _string, int _id) {
        super(_counter, _string, _id);
        if (_string == null) {
            _string = new ChatComponentText("");
        }

        this.chatComponent = _string;
    }

    /**
     * Only used by vanilla, tabbychat itself should use {@link #getChatComponent()} instead
     */
    @Override
    @Deprecated
    public IChatComponent func_151461_a() {
        return getChatComponent();
    }

    public IChatComponent getChatComponent() {
        return this.chatComponent;
    }
}
