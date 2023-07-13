package acs.tabbychat.util;

import com.google.gson.annotations.Expose;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.Objects;

public class TCChatLineFake extends ChatLine {
    protected int updateCounterCreated = -1;
    @Expose
    protected IChatComponent chatComponent;
    protected int chatLineID;

    public TCChatLineFake() {
        super(-1, new ChatComponentText(""), 0);
    }

    public TCChatLineFake(int _counter, IChatComponent _string, int _id) {
        super(_counter, _string, _id);
        this.updateCounterCreated = _counter;
        this.chatComponent = Objects.requireNonNullElseGet(_string, () -> new ChatComponentText(""));
        this.chatLineID = _id;
    }

    @Override
    @Deprecated
    public IChatComponent func_151461_a() {
        return getChatComponent();
    }

    public IChatComponent getChatComponent() {
        return this.chatComponent;
    }

    @Override
    public int getUpdatedCounter() {
        return this.updateCounterCreated;
    }

    @Override
    public int getChatLineID() {
        return this.chatLineID;
    }
}
