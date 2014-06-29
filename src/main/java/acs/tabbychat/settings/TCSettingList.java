package acs.tabbychat.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;

public class TCSettingList extends Gui {
	
	private List<Entry> list = Lists.newArrayList();
	private int currentPage;
	private int xPosition;
	private int yPosition;
	private int width;
	private int height;
	private List<Entry> selected;
	
	public TCSettingList(File file) throws IOException{
		loadEntries(file);
	}
	
	public TCSettingList(List<String> list){
		for(String val : list)
			this.list.add(new Entry(val));
	}

	public void drawList(Minecraft mc, int cursorX, int cursorY) {
		FontRenderer fr = mc.fontRenderer;
		Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, Integer.MIN_VALUE);
		
		Gui.drawRect(xPosition-1, yPosition-1, xPosition, yPosition + height, -0xffffff);
		Gui.drawRect(xPosition-1, yPosition+height, xPosition+width+1, yPosition+height+1, -0xffffff);
		Gui.drawRect(xPosition+width, yPosition+height+1, xPosition+width+1, yPosition-1, -0xffffff);
		Gui.drawRect(xPosition-1, yPosition-1, xPosition+width+1, yPosition, -0xffffff);
		
		int i = 0;
		for(Entry entry : getVisible()){
			fr.drawString(entry.getValue(), xPosition + 5, yPosition + i*(mc.fontRenderer.FONT_HEIGHT + 2), 0x010101);
			i++;
		}
	}
	
	public boolean contains(String srt){
		for(Entry entry : list)
			if(srt.equals(entry.getValue()))
				return true;
		return false;
	}
	
	public void addToList(String str){
		if(!contains(str))
			list.add(new Entry(str));
	}
	
	public void removeFromList(String str){
		for(Entry entry : list){
			if(str.equals(entry.getValue())){
				list.remove(entry);
				this.deselectEntry(entry);
			}
		}
	}
	
	public void clearList(){
		list.clear();
	}
	
	public List<Entry> getEntries(){
		return new ArrayList<Entry>(list);
	}
	
	public List<Entry> getVisible(){
		List<Entry> list = Lists.newArrayList();
		int i = 0;
		while(i <= currentPage){
			list.clear();
			for(Entry entry : this.list){
				list.add(entry);
			}
			i++;
		}
		return list;
	}
	
	public List<Entry> getSelected(){
		return new ArrayList<Entry>(this.selected);
	}
	
	public void selectEntry(Entry entry){
		if(!list.contains(entry))
			list.add(entry);
		if(!entry.isSelected())
			this.selected.add(entry);
	}
	
	public void deselectEntry(Entry entry){
		if(entry.isSelected())
			selected.remove(entry);
	}
	
	public void clearSelection(){
		this.selected.clear();
	}

	public int getTotalPages(){
		double pages = (double)list.size() / Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;;
		return MathHelper.ceiling_double_int(pages);
	}
	
	public void nextPage(){
		this.currentPage = Math.min(currentPage+1, getTotalPages());
	}
	
	public void previousPage(){
		this.currentPage = Math.max(currentPage-1, 0);
	}
	
	public void saveEntries(File file) throws IOException {
		for(Entry entry : list){
			IOUtils.writeLines(list, null, new FileWriter(file));
		}
	}
	
	public void loadEntries(File file) throws IOException{
		list.clear();
		for(String val : IOUtils.readLines(new FileReader(file)))
			list.add(new Entry(val));
	}
	
	public class Entry {
		
		private TCSettingList list = TCSettingList.this;

		private String value;
		
		public Entry(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
		public boolean isSelected(){
			return list.getSelected().contains(this);
		}
		
		public void setSelected(boolean value){
			if(value)
				list.getSelected().add(this);
			else
				list.getSelected().remove(this);
		}
		
		public void remove(){
			list.list.remove(this);
		}
	}
}
