package acs.tabbychat.threads;

import net.minecraft.client.resources.I18n;
import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.util.TabbyChatUtils;

public class BackgroundUpdateCheck extends Thread {
	/**
	 * Runs update check
	 */
	public BackgroundUpdateCheck() {
	}

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

		int[] newVersion = { Integer.parseInt(newVersionString[0]),
				Integer.parseInt(newVersionString[1]),
				Integer.parseInt(newVersionString[2]) };
		int[] version = { Integer.parseInt(versionString[0]),
				Integer.parseInt(versionString[1]),
				Integer.parseInt(versionString[2]) };

		for (int i = 0; i < version.length; i++) {
			if (version[i] < newVersion[i]) {
				updateFound = true;
			} else if (version[i] > newVersion[i]) {
				break;
			}

		}

		if (updateFound) {
			TabbyChatUtils.log.info("Update Found!");
			StringBuilder updateReport = new StringBuilder("\u00A77");
			updateReport.append(I18n.format("messages.update1"));
			updateReport.append(current);
			updateReport.append(I18n.format("messages.update2"));
			updateReport.append(newest + ") ");
			updateReport.append(I18n.format("messages.update3"));
			updateReport.append("\u00A7r");
			GuiNewChatTC.getInstance().tc.printMessageToChat(updateReport
					.toString());
		}
	}
}
