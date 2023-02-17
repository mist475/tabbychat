package acs.tabbychat.threads;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.util.TabbyChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatAllowedCharacters;

import java.lang.reflect.Method;

public class BackgroundChatThread extends Thread {
    String sendChat = "";
    String knownPrefix = null;

    public BackgroundChatThread(String _send) {
        this.sendChat = _send;
    }

    public BackgroundChatThread(String _send, String _prefix) {
        this.sendChat = _send;
        this.knownPrefix = _prefix;
    }

    @Override
    public synchronized void run() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.ingameGUI.getChatGUI().addToSentMessages(this.sendChat);
        String cmdPrefix = "";
        String[] toSplit;
        int start;
        if (this.knownPrefix != null && this.sendChat.startsWith(this.knownPrefix)) {
            cmdPrefix = this.knownPrefix.trim() + " ";
            this.sendChat = this.sendChat.substring(this.knownPrefix.length()).trim();
            toSplit = this.sendChat.split(" ");
            start = 0;
        }
        else {
            toSplit = this.sendChat.split(" ");
            start = 0;
            if (toSplit.length > 0 && toSplit[0].startsWith("/")) {
                if (toSplit[0].startsWith("/msg")) {
                    cmdPrefix = toSplit[0] + " " + toSplit[1] + " ";
                    start = 2;
                }
                else if (!toSplit[0].trim().equals("/")) {
                    cmdPrefix = toSplit[0] + " ";
                    start = 1;
                }
            }
        }
        int suffix = cmdPrefix.length();
        StringBuilder sendPart = new StringBuilder(119);
        for (int word = start; word < toSplit.length; word++) {
            if (sendPart.length() + toSplit[word].length() + suffix > 100) {
                mc.thePlayer.sendChatMessage(cmdPrefix + sendPart.toString().trim());
                try {
                    Thread.sleep(Integer.parseInt(TabbyChat.advancedSettings.multiChatDelay
                                                          .getValue()));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendPart = new StringBuilder(119);
                if (toSplit[word].startsWith("/"))
                    sendPart.append("_");
            }
            sendPart.append(toSplit[word]).append(" ");
        }
        if (sendPart.length() > 0 || cmdPrefix.length() > 0) {
            String message = cmdPrefix + sendPart.toString().trim();
            message = ChatAllowedCharacters.filerAllowedCharacters(message);

            // Check for client commands.
            // Use reflection, so we don't have to import Forge.
            if (TabbyChat.forgePresent) {
                try {
                    Class<?> clntCmdHndlr = Class
                            .forName("net.minecraftforge.client.ClientCommandHandler");
                    Method exeCmd;
                    try {
                        exeCmd = clntCmdHndlr.getMethod("func_71556_a", ICommandSender.class,
                                                        String.class);
                    }
                    catch (NoSuchMethodException e) {
                        exeCmd = clntCmdHndlr.getMethod("executeCommand", ICommandSender.class,
                                                        String.class);
                    }
                    Object instance = clntCmdHndlr.getField("instance").get(null);
                    int value = (Integer) exeCmd.invoke(instance, mc.thePlayer, message);
                    if (value == 1)
                        return;
                }
                catch (Exception e) {
                    TabbyChatUtils.log
                            .warn("Oops, something went wrong while checking the message for Client Commands");
                    e.printStackTrace();
                }

            }
            mc.thePlayer.sendChatMessage(message);
        }
    }
}
