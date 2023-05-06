package acs.tabbychat.threads;

import acs.tabbychat.core.TabbyChat;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraftforge.client.ClientCommandHandler;

public class BackgroundChatThread extends Thread {
    String sendChat;
    String knownPrefix;

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
                // /fmsg is added by lotr to message groups of players
                if (toSplit[0].startsWith("/msg") || toSplit[0].startsWith("/fmsg")) {
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

            //Check if command is client side
            if (ClientCommandHandler.instance.executeCommand(mc.thePlayer, message) == 1) {
                return;
            }

            mc.thePlayer.sendChatMessage(message);
        }
    }
}
