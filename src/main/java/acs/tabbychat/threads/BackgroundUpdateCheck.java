package acs.tabbychat.threads;

import net.minecraft.client.resources.I18n;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.util.TabbyChatUtils;

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
            } else if (version[i] > newVersion[i]) {
                break;
            }

        }

        if (updateFound) {
            TabbyChatUtils.log.info("Update Found!");
            StringBuilder updateReport = new StringBuilder("\u00A77");
            updateReport.append(I18n.format("messages.update1") + ' ');
            updateReport.append(current);
            updateReport.append(I18n.format("messages.update2") + ' ');
            updateReport.append(newest + ") ");
            updateReport.append(I18n.format("messages.update3"));
            updateReport.append("\u00A7r");
            TabbyChat.printMessageToChat(updateReport.toString());
        }
    }
}
