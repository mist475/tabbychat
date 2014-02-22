package acs.tabbychat.core;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.Minecraft;

import org.apache.commons.io.FileUtils;

import acs.tabbychat.util.TabbyChatUtils;

import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.core.LiteLoader;

public class LiteModTabbyChat implements InitCompleteListener {
	private static GuiNewChatTC gnc;

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
		gnc = GuiNewChatTC.getInstance();
	}

	@Override
	public void onTick(Minecraft var1, float var2, boolean var3, boolean var4) {
		TabbyChatUtils.chatGuiTick(var1);
	}

	@Override
	public void init(File configPath) {
		String relativeConfig = "tabbychat";
		File liteConfigDir = LiteLoader.getCommonConfigFolder().toPath()
				.resolve(relativeConfig).toFile();
		File mcConfigDir = Minecraft.getMinecraft().mcDataDir.toPath()
				.resolve("config").resolve(relativeConfig).toFile();
		
		// If forge configs exist and liteloader configs don't, copy over.
		if (!liteConfigDir.exists() && mcConfigDir.exists()) {
			try {
				FileUtils.copyDirectory(mcConfigDir, liteConfigDir);
				TabbyChatUtils.log.info("Old configs found! Converting.");
			} catch (IOException e) {
				TabbyChatUtils.log.warning("Old configs found, but unable to convert.\n" + e);
			}
		}
	}

	@Override
	public void upgradeSettings(String version, File configPath,
			File oldConfigPath) {
		// TODO Auto-generated method stub

	}
}
