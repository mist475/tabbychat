package acs.tabbychat.threads;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.util.TabbyChatUtils;
import net.minecraft.client.resources.I18n;

public class BackgroundUpdateCheck extends Thread {

    @Override
    public void run() {
        String newest = TabbyChat.getNewestVersion();
        String current = TabbyChatUtils.version;

        boolean updateFound = false;

        if (!TabbyChat.generalSettings.tabbyChatEnable.getValue()
                || !TabbyChat.generalSettings.updateCheckEnable.getValue()
                || newest.equalsIgnoreCase("invalid"))
            return;

        String[] newVersionString = newest.split("\\.");
        String[] versionString = current.split("\\.");

        int[] newVersion = new int[newVersionString.length];
        int[] version = new int[versionString.length];

        int i;
        for (i = 0; i < newVersion.length; i++) {
            newVersion[i] = TabbyChatUtils.parseInteger(newVersionString[i], Integer.MIN_VALUE,
                                                        Integer.MAX_VALUE, 0);
        }

        for (i = 0; i < version.length; i++) {
            version[i] = TabbyChatUtils.parseInteger(versionString[i], Integer.MIN_VALUE,
                                                     Integer.MAX_VALUE, 0);
        }

        for (i = 0; i < Math.min(version.length, newVersion.length); i++) {
            if (version[i] < newVersion[i]) {
                updateFound = true;
                break;
            }
            else if (version[i] > newVersion[i]) {
                break;
            }

        }

        if (updateFound) {
            TabbyChatUtils.log.info("Update Found!");
            String updateReport = "\u00A77" + I18n.format("messages.update1") + ' ' +
                    current +
                    I18n.format("messages.update2") + ' ' +
                    newest + ") " +
                    I18n.format("messages.update3") +
                    "\u00A7r";
            TabbyChat.printMessageToChat(updateReport);
        }
    }
}
