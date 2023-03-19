package acs.tabbychat.core;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.lwjgl.input.Keyboard;

public class GuiSleepTC extends GuiChatTC {

    @Override
    public void initGui() {
        super.initGui();
        GuiButton leaveBed = new GuiButton(1, this.width / 2 - 100, this.height - 40,
                                           I18n.format("multiplayer.stopSleeping"));
        this.buttonList.add(leaveBed);
    }

    @Override
    public void keyTyped(char c, int code) {
        switch (code) {
            case Keyboard.KEY_ESCAPE:
                this.playerWakeUp();
                break;
            case Keyboard.KEY_RETURN:
            case Keyboard.KEY_NUMPADENTER:
                this.sendChat(true);
            default:
                super.keyTyped(c, code);
                break;
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 1)
            playerWakeUp();
        else
            super.actionPerformed(button);
    }

    /**
     * Wakes up the player
     */
    private void playerWakeUp() {
        NetHandlerPlayClient var1 = mc.thePlayer.sendQueue;
        var1.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 3));
    }

    @Override
    public void updateScreen() {
        if (!mc.thePlayer.isPlayerSleeping())
            mc.displayGuiScreen(null);
        super.updateScreen();
    }
}
