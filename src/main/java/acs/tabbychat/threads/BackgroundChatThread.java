package acs.tabbychat.threads;

import acs.tabbychat.core.TabbyChat;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraftforge.client.ClientCommandHandler;
import org.apache.commons.lang3.StringUtils;

public class BackgroundChatThread extends Thread {
    String sendChat;
    String knownPrefix;

    final Minecraft mc;

    public BackgroundChatThread(String _send) {
        this.sendChat = _send;
        mc = Minecraft.getMinecraft();
    }

    public BackgroundChatThread(String _send, String _prefix) {
        this.sendChat = _send;
        this.knownPrefix = _prefix;
        mc = Minecraft.getMinecraft();
    }

    @Override
    public synchronized void run() {
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
            // command
            if (toSplit.length > 0 && toSplit[0].startsWith("/")) {
                if (toSplit[0].startsWith("/msg")) {
                    cmdPrefix = toSplit[0] + " " + toSplit[1] + " ";
                    start = 2;
                }

                // /fmsg is added by lotr to message groups of players
                else if (toSplit[0].startsWith("/fmsg") && toSplit.length > 1) {
                    // /fmsg bind or unbind sets a default fellowship and should not be parsed as a multiline comment
                    if (!toSplit[1].endsWith("bind")) {
                        String[] fShipNameArray = this.sendChat.split("\"");
                        //targeted name is contained with double quotes
                        if (fShipNameArray.length > 1) {
                            cmdPrefix = toSplit[0] + " \"" + fShipNameArray[1] + "\" ";
                            //count number of spaces in fShipNameArray and set start accordingly
                            start = 2 + StringUtils.countMatches(fShipNameArray[1], " ");
                        }
                        // default
                        else {
                            cmdPrefix = "/fmsg ";
                            start = 1;
                        }
                    }
                }
                else if (!toSplit[0].trim().equals("/")) {
                    cmdPrefix = toSplit[0] + " ";
                    start = 1;
                }
            }
        }


        sendChat(start,toSplit,cmdPrefix);

    }

    private void sendChat(int start, String[] toSplit, String cmdPrefix) {
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

        if (!(sendPart.length() == 0) || !(cmdPrefix.length() == 0)) {
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
