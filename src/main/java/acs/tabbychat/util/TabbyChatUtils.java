package acs.tabbychat.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acs.tabbychat.core.ChatChannel;
//import acs.tabbychat.core.FilterTest;
import acs.tabbychat.core.GuiChatTC;
import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TCChatLine;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.gui.ITCSettingsGUI;
import acs.tabbychat.settings.ChannelDelimEnum;
import acs.tabbychat.settings.ColorCodeEnum;
import acs.tabbychat.settings.FormatCodeEnum;
import acs.tabbychat.settings.NotificationSoundEnum;
import acs.tabbychat.settings.TimeStampEnum;
import acs.tabbychat.threads.BackgroundChatThread;

import com.mumfrey.liteloader.core.LiteLoader;

public class TabbyChatUtils {
	private static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
	private static Calendar logDay = Calendar.getInstance();
	private static File logDir = new File(new File(
			Minecraft.getMinecraft().mcDataDir, "logs"), "TabbyChat");
	private static SimpleDateFormat logNameFormat = new SimpleDateFormat(
			"'_'MM-dd-yyyy'.txt'");
	public final static String version = "1.11.00";
	public final static String name = "TabbyChat";
	public final static String modid = "tabbychat";
	public static Logger log = LogManager.getLogger(name);
	private static Minecraft mc = Minecraft.getMinecraft();

	/**
	 * 
	 * @param mc
	 */
	public static void chatGuiTick(Minecraft mc) {
		// Find Forge. Really should only be ran once on startup.
		try {
			Class.forName("net.minecraftforge.common.MinecraftForge");
			TabbyChat.forgePresent = true;
		} catch (ClassNotFoundException e) {
		}
		
		if (mc.currentScreen == null)
			return;
		if (!(mc.currentScreen instanceof GuiChat))
			return;
		if (mc.currentScreen.getClass() == GuiChatTC.class)
			return;

		String inputBuffer = "";
		try {
			int ind = 0;
			for (Field fields : GuiChat.class.getDeclaredFields()) {
				if (fields.getType() == String.class) {
					if (ind == 1) {
						fields.setAccessible(true);
						inputBuffer = (String) fields.get(mc.currentScreen);
						break;
					}
					ind++;
				}
			}
		} catch (Exception e) {
			TabbyChat.printException("Unable to display chat interface", e);
		}
		mc.displayGuiScreen(new GuiChatTC(inputBuffer));
	}

	/**
	 * 
	 * @param lines
	 * @return
	 */
	
	public static String chatLinesToString(List<TCChatLine> lines) {
		StringBuilder result = new StringBuilder(500);
		for (TCChatLine line : lines) {
			result.append(line.getChatLineString().getFormattedText()).append(
					"\n");
		}
		return result.toString().trim();
	}
	

	/**
	 * 
	 * @return
	 */
	public static ServerData getServerData() {
		Minecraft mc = Minecraft.getMinecraft();
		ServerData serverData = null;
		for (Field field : Minecraft.class.getDeclaredFields()) {
			if (field.getType() == ServerData.class) {
				field.setAccessible(true);
				try {
					serverData = (ServerData) field.get(mc);
				} catch (Exception e) {
					TabbyChat.printException(
							"Unable to find server information", e);
				}
				break;
			}
		}
		return serverData;
	}

	/**
	 * 
	 * @return
	 */
	public static File getServerDir() {
		String ip = getServerIp();
		if (ip.contains(":")) {
			ip = ip.replaceAll(":", "(") + ")";
		}
		return new File(ITCSettingsGUI.tabbyChatDir, ip);
	}

	/**
	 * Returns the IP of the current server.
	 * 
	 * @return
	 */
	public static String getServerIp() {
		String ip;
		if (Minecraft.getMinecraft().isSingleplayer()) {
			ip = "singleplayer";
		} else if (getServerData() == null) {
			ip = "unknown";
		} else {
			ip = getServerData().serverIP;
		}
		return ip;
	}

	/**
	 * Returns the directory the the configs are stored.
	 * 
	 * @return
	 */
	public static File getTabbyChatDir() {
		if (TabbyChat.liteLoaded) {
			return new File(LiteLoader.getCommonConfigFolder(), "tabbychat");
		} else {
			return new File(new File(Minecraft.getMinecraft().mcDataDir,
					"config"), "tabbychat");
		}
	}

	/**
	 * 
	 * @param _gnc
	 */
	public static void hookIntoChat(GuiNewChatTC _gnc) {
		if (Minecraft.getMinecraft().ingameGUI.getChatGUI().getClass() != GuiNewChatTC.class) {
			try {
				Class IngameGui = GuiIngame.class;
				Field persistantGuiField = IngameGui.getDeclaredFields()[6];
				persistantGuiField.setAccessible(true);
				persistantGuiField
						.set(Minecraft.getMinecraft().ingameGUI, _gnc);

				int tmp = 0;
				for (Field fields : GuiNewChat.class.getDeclaredFields()) {
					if (fields.getType() == List.class) {
						fields.setAccessible(true);
						if (tmp == 0) {
							_gnc.sentMessages = (List) fields.get(_gnc);
						} else if (tmp == 1) {
							_gnc.backupLines = (List) fields.get(_gnc);
						} else if (tmp == 2) {
							_gnc.chatLines = (List) fields.get(_gnc);
							break;
						}
						tmp++;
					}
				}
			} catch (Exception e) {
				TabbyChat.printException("Error loading chat hook.", e);
			}
		}
	}

	/**
	 * 
	 * @param _gui
	 * @param className
	 * @return
	 */
	public static boolean is(Gui _gui, String className) {
		try {
			return _gui.getClass().getSimpleName().contains(className);
		} catch (Throwable e) {
		}
		return false;
	}

	/**
	 * 
	 * @param arr
	 * @param glue
	 * @return
	 */
	public static String join(String[] arr, String glue) {
		if (arr.length < 1)
			return "";
		else if (arr.length == 1)
			return arr[0];
		StringBuilder bucket = new StringBuilder();
		for (String s : Arrays.copyOf(arr, arr.length - 1)) {
			bucket.append(s);
			bucket.append(glue);
		}
		bucket.append(arr[arr.length - 1]);
		return bucket.toString();
	}

	/**
	 * Logs chat.
	 * 
	 * @param theChat
	 * @param theChannel
	 */
	public static void logChat(String theChat, ChatChannel theChannel) {
		Calendar tmpcal = Calendar.getInstance();
		File fileDir;
		try {
			if (theChannel.getTitle().equals(null)) {
				theChannel = new ChatChannel("default");
			}
		} catch (NullPointerException e) {
			theChannel = new ChatChannel("default");
		}
		if (getServerIp() == "singleplayer") {
			IntegratedServer ms = Minecraft.getMinecraft()
					.getIntegratedServer();
			String worldName = ms.getWorldName();
			fileDir = new File(new File(new File(logDir, "singleplayer"),
					worldName), theChannel.getTitle());

		} else {
			fileDir = new File(new File(logDir, getServerIp()),
					theChannel.getTitle());
		}
		if (!fileDir.exists())
			fileDir.mkdirs();

		if (theChannel.getLogFile() == null
				|| tmpcal.get(Calendar.DAY_OF_YEAR) != logDay
						.get(Calendar.DAY_OF_YEAR)) {
			logDay = tmpcal;
			theChannel.setLogFile(new File(fileDir, theChannel.getTitle()
					+ logNameFormat.format(logDay.getTime())));
		}

		if (!theChannel.getLogFile().exists()) {
			try {
				fileDir.mkdirs();
				theChannel.getLogFile().createNewFile();
			} catch (Exception e) {
				TabbyChat.printErr("Cannot create log file : '"
						+ e.getLocalizedMessage() + "' : " + e.toString());
				return;
			}
		}

		try {
			FileOutputStream logStream = new FileOutputStream(
					theChannel.getLogFile(), true);
			PrintStream logPrint = new PrintStream(logStream);
			logPrint.println(theChat);
			logPrint.close();
		} catch (Exception e) {
			TabbyChat.printErr("Cannot write to log file : '"
					+ e.getLocalizedMessage() + "' : " + e.toString());
			return;
		}
	}

	/**
	 * 
	 * @param val1
	 * @param val2
	 * @param val3
	 * @return
	 */
	public static Float median(float val1, float val2, float val3) {
		if (val1 < val2 && val1 < val3)
			return Math.min(val2, val3);
		else if (val1 > val2 && val1 > val3)
			return Math.max(val2, val3);
		else
			return val1;
	}

	/**
	 * 
	 * @param _input
	 * @return
	 */
	public static ColorCodeEnum parseColor(Object _input) {
		if (_input == null)
			return null;
		String input = _input.toString();
		try {
			return ColorCodeEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * 
	 * @param _input
	 * @return
	 */
	public static ChannelDelimEnum parseDelimiters(Object _input) {
		if (_input == null)
			return null;
		String input = _input.toString();
		try {
			return ChannelDelimEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * 
	 * @param _input
	 * @param min
	 * @param max
	 * @return
	 */
	public static Float parseFloat(Object _input, float min, float max) {
		if (_input == null)
			return null;
		String input = _input.toString();
		Float result;
		try {
			result = Float.valueOf(input);
			result = Math.max(min, result);
			result = Math.min(max, result);
		} catch (NumberFormatException e) {
			result = null;
		}
		return result;
	}

	/**
	 * 
	 * @param _input
	 * @return
	 */
	public static FormatCodeEnum parseFormat(Object _input) {
		if (_input == null)
			return null;
		String input = _input.toString();
		try {
			return FormatCodeEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * 
	 * @param _input
	 * @param min
	 * @param max
	 * @param fallback
	 * @return
	 */
	public static Integer parseInteger(String _input, int min, int max,
			int fallback) {
		Integer result;
		try {
			result = Integer.parseInt(_input);
			result = Math.max(min, result);
			result = Math.min(max, result);
		} catch (NumberFormatException e) {
			result = fallback;
		}
		return result;
	}

	/**
	 * 
	 * @param _input
	 * @return
	 */
	public static int parseInteger(String _input) {
		NumberFormat formatter = NumberFormat.getInstance();
		boolean state = formatter.isParseIntegerOnly();
		formatter.setParseIntegerOnly(true);
		ParsePosition pos = new ParsePosition(0);
		int result = formatter.parse(_input, pos).intValue();
		formatter.setParseIntegerOnly(state);
		if (_input.length() == pos.getIndex())
			return result;
		else
			return -1;
	}

	/**
	 * 
	 * @param _input
	 * @return
	 */
	public static NotificationSoundEnum parseSound(Object _input) {
		if (_input == null)
			return NotificationSoundEnum.ORB;
		String input = _input.toString();
		try {
			return NotificationSoundEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return NotificationSoundEnum.ORB;
		}
	}

	/**
	 * 
	 * @param _input
	 * @return
	 */
	public static String parseString(Object _input) {
		if (_input == null)
			return " ";
		else
			return _input.toString();
	}

	/**
	 * 
	 * @param _input
	 * @return
	 */
	public static TimeStampEnum parseTimestamp(Object _input) {
		if (_input == null)
			return null;
		String input = _input.toString();
		try {
			return TimeStampEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * 
	 * @param stamp
	 * @param line
	 * @param id
	 * @param status
	 * @return
	 */
	
	public static List<TCChatLine> stringToChatLines(int stamp, String line,
			int id, boolean status) {
		// List<String> lineSplit =
		// Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(line,
		// stringWidth);
		List<String> lineSplit = Arrays.asList(line.split("\n"));
		List<TCChatLine> result = new ArrayList<TCChatLine>(lineSplit.size());
		boolean first = true;
		for (String split : lineSplit) {
			if (first) {
				result.add(new TCChatLine(stamp, new ChatComponentText(split),
						id, status));
				first = false;
			} else
				result.add(new TCChatLine(stamp, new ChatComponentText(" "
						+ split), id, status));
		}
		return result;
	}

	/**
	 * 
	 * @param currentMap
	 * @param _left
	 * @param _right
	 * @return
	 */
	public static LinkedHashMap<String, ChatChannel> swapChannels(
			LinkedHashMap<String, ChatChannel> currentMap, int _left, int _right) {
		// Ensure ordering of 'indices' is 0<=_left<_right<=end
		if (_left == _right)
			return currentMap;
		else if (_left > _right) {
			int _tmp = _left;
			_left = _right;
			_right = _tmp;
		}
		if (_right >= currentMap.size())
			return currentMap;

		// Convert map to array for access by index
		String[] arrayCopy = new String[currentMap.size()];
		arrayCopy = currentMap.keySet().toArray(arrayCopy);
		// Swap array entries using passed index arguments
		String tmp = arrayCopy[_left];
		arrayCopy[_left] = arrayCopy[_right];
		arrayCopy[_right] = tmp;
		// Create new map and populate
		int n = arrayCopy.length;
		LinkedHashMap<String, ChatChannel> returnMap = new LinkedHashMap(n);
		for (int i = 0; i < n; i++) {
			returnMap.put(arrayCopy[i], currentMap.get(arrayCopy[i]));
		}
		return returnMap;
	}

	/**
	 * 
	 * @param toSend
	 */
	public static void writeLargeChat(String toSend) {
		List<String> actives = TabbyChat.getInstance().getActive();
		BackgroundChatThread sendProc;
		if (!TabbyChat.getInstance().enabled() || actives.size() != 1)
			sendProc = new BackgroundChatThread(toSend);
		else {
			ChatChannel active = TabbyChat.getInstance().channelMap.get(actives
					.get(0));
			String tabPrefix = active.cmdPrefix;
			boolean hiddenPrefix = active.hidePrefix;

			if (TabbyChat.advancedSettings.convertUnicodeText.getValue()) {
				toSend = convertUnicode(toSend);
			}

			if (tabPrefix != null && tabPrefix.length() > 0) {
				if (!hiddenPrefix)
					sendProc = new BackgroundChatThread(toSend, tabPrefix);
				else if (!toSend.startsWith("/"))
					sendProc = new BackgroundChatThread(tabPrefix + " "
							+ toSend, tabPrefix);
				else
					sendProc = new BackgroundChatThread(toSend);
			} else
				sendProc = new BackgroundChatThread(toSend);
		}
		sendProc.start();
	}

	/**
	 * Converts strings to unicode. Essentially replaces \\uabcd with \uabcd.
	 * 
	 * @param chat
	 * @return
	 */
	public static String convertUnicode(String chat) {
		String newChat = "";
		for (String s : chat.split("\\\u0000")) {
			if (s.contains("u")) {
				try {
					newChat += StringEscapeUtils.unescapeJava(s);
				} catch (IllegalArgumentException e) {
					newChat += s;
				}
			} else
				newChat += s;
		}
		return newChat;
	}

	/**
	 * <p>
	 * Word Wrap
	 * </p>
	 * 
	 * Splits an IChatComponent by the chat width. It is smart enough to not cut
	 * off words or create orphans/widows.
	 * 
	 * @param component
	 *            The chat that will get split.
	 * @param limit
	 *            Max length of each item.
	 * @return
	 */
	public static List<IChatComponent> split(IChatComponent component, int limit) {

		Iterator<IChatComponent> iter = component.iterator();
		List<IChatComponent> chatcomponent = new ArrayList();
		List<IChatComponent> result = new ArrayList();
		
		while(iter.hasNext()){
			IChatComponent chat = iter.next();
			
			String s = chat.getUnformattedTextForChat();
			ChatStyle style = chat.getChatStyle();
			
			String[] parts = s.split(String.format(WITH_DELIMITER, " "));
			for(String str : parts){
				IChatComponent partcomp = new ChatComponentText(str);
				partcomp.setChatStyle(style); // TODO StackOverflowError!!!!
				chatcomponent.add(partcomp);
			}
		}
		
		IChatComponent chatline = null;
		for (IChatComponent word : chatcomponent) {
			if (chatline == null)
				chatline = word;
			else {
				if (mc.fontRenderer.getStringWidth(chatline.getFormattedText() + " " + word
						.getFormattedText()) <= limit)
					chatline = chatline.appendSibling(word);
				else {
					result.add(chatline);
					chatline = word;
				}
			}
		}
		result.add(chatline);		
		return result;

	}

	/**
	 * Takes the substring of a paragraph-symbol formatted string
	 * 
	 * @param formatted
	 *            The string with formatting characters.
	 * @param beginIndex
	 *            the beginning index, inclusive.
	 * @param endIndex
	 *            the ending index, exclusive.
	 * @return A formatted substring
	 */
	public static String substringWithFormatters(String formatted,
			int beginIndex, int endIndex) {
		int length = formatted.length();
		int unformattedIndex = 0, actualStartIndex = -1, actualEndIndex = -1;
		for (int i = 0; i < length; i++) {
			if (actualStartIndex == -1 && unformattedIndex == beginIndex)
				actualStartIndex = i;
			if (unformattedIndex == endIndex) {
				actualEndIndex = i;
				break;
			}
			if (formatted.charAt(i) == '\u00a7') {
				i++; // Skip next character as well
				continue;
			}
			unformattedIndex++;
		}
		if (actualStartIndex == -1 || actualEndIndex == -1)
			throw new StringIndexOutOfBoundsException();
		return formatted.substring(actualStartIndex, actualEndIndex);
	}

	private TabbyChatUtils() {
	}
	
	public static IChatComponent mergeChatComponent(IChatComponent[] icc) {
		if (icc.length == 0 || icc == null)
			return null;
		IChatComponent newChat = null;
		for (IChatComponent chat : icc) {
			if (newChat != null)
				newChat = newChat.appendSibling(chat);
			else
				newChat = chat;
		}

		return newChat;

	}
}
