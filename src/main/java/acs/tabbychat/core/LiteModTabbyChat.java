package acs.tabbychat.core;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.Minecraft;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import acs.tabbychat.util.TabbyChatUtils;

import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.core.LiteLoader;

public class LiteModTabbyChat implements InitCompleteListener {
	private static Logger log = TabbyChatUtils.log;

	@Override
	public String getName() {
		return TabbyChatUtils.name;
	}

	@Override
	public String getVersion() {
		return TabbyChatUtils.version;
	}

	@Override
	public void onInitCompleted(Minecraft var1, LiteLoader var2) {
		TabbyChat.liteLoaded = true;
		GuiNewChatTC.getInstance();
	}

	@Override
	public void onTick(Minecraft var1, float var2, boolean var3, boolean var4) {
		TabbyChatUtils.chatGuiTick(var1);
	}

	@Override
	public void init(File configPath) {
		String relativeConfig = "tabbychat";
		File liteConfigDir = new File(LiteLoader.getCommonConfigFolder(),
				relativeConfig);
		File mcConfigDir = new File(LiteLoader.getGameDirectory(),
				new StringBuilder().append("config").append(File.separatorChar)
						.append(relativeConfig).toString());

		// If forge old exist and liteloader configs don't, copy over.
		if (!liteConfigDir.exists() && mcConfigDir.exists()) {
			try {
				FileUtils.copyDirectory(mcConfigDir, liteConfigDir);
				log.info("Old configs found! Converting.");
			} catch (IOException e) {
				log.warn("Old configs found, but unable to convert.\n" + e);
			}
		}
		TabbyChatUtils.startup();
	}

	@Override
	public void upgradeSettings(String version, File configPath,
			File oldConfigPath) {
		// TODO Auto-generated method stub

	}
}
