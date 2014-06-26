package acs.tabbychat.gui.context;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import com.google.common.collect.Lists;

public class ChatContextMenu extends Gui {
	
	private static List<ChatContext> items = Lists.newArrayList();
	private Minecraft mc = Minecraft.getMinecraft();
	public boolean active;
	private int x;
	private int y;
	
	public ChatContextMenu(int x, int y){
		this.x = x;
		this.y = y;
		int i = 0;
		for(ChatContext item : items){
			if(!item.isLocationValid(x, y))
				continue;
			item.id = i;
			item.parent = this;
			item.xPosition = this.x;
			item.yPosition = this.y + 20 * i;
			i++;
		}
	}
	
	public void drawMenu(int x, int y){
		if(!active)
			return;
		for(ChatContext item : items){
			if(!item.isLocationValid(x, y))
				continue;
			item.drawButton(mc, this.x, this.y + 20 * item.id);
		}
	}
	
	public void buttonClicked(ChatContext item){
		item.actionPreformed();
	}
	
	public static void addContext(Class<? extends ChatContext> item){
		try {
			items.add(item.newInstance());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void insertContextAtPos(int pos, ChatContext item){
		items.add(pos, item);
	}
	
	public static void removeContext(ChatContext item){
		items.remove(item);
	}
	
	public static void removeContext(int pos){
		items.remove(pos);
	}
	
	public static List<ChatContext> getContextList(){
		return items;
	}

}
