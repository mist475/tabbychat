package acs.tabbychat.settings;

import acs.tabbychat.gui.PrefsButton;
import acs.tabbychat.jazzy.TCSpellCheckManager;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TCSettingList extends Gui {

    private List<Entry> list = Lists.newArrayList();
    private List<Entry> selected = Lists.newArrayList();
    private final File dictionary;
    private int currentPage = 1;
    private int id = 0;
    private int xPosition, yPosition;
    private int width, height;

    public TCSettingList(File file) {
        this.dictionary = file;
    }

    public void drawList(Minecraft mc, int cursorX, int cursorY) {
        Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, Integer.MIN_VALUE);

        Gui.drawRect(xPosition - 1, yPosition - 1, xPosition, yPosition + height, -0xffffff);
        Gui.drawRect(xPosition - 1, yPosition + height, xPosition + width + 1, yPosition + height
                + 1, -0xffffff);
        Gui.drawRect(xPosition + width, yPosition + height + 1, xPosition + width + 1,
                yPosition - 1, -0xffffff);
        Gui.drawRect(xPosition - 1, yPosition - 1, xPosition + width + 1, yPosition, -0xffffff);

        int i = 0;
        for (Entry entry : getVisible()) {
            entry.setPos(i);
            entry.drawButton(mc, cursorX, cursorY);
            i++;
        }
    }

    public boolean contains(String srt) {
        for (Entry entry : list)
            if (srt.equals(entry.displayString))
                return true;
        return false;
    }

    public void addToList(String str) {
        if (str == null || str.isEmpty())
            return;
        if (!contains(str)) {
            char[] chararray = str.toCharArray();
            int j = 0;
            label_1: for (Entry entry : list) {
                char[] chararray1 = entry.displayString.toCharArray();
                for (int i = 0; i < Math.min(chararray.length, chararray1.length); i++) {
                    char c = chararray[i], c1 = chararray1[i];
                    if (c > c1)
                        break;
                    if (c == c1)
                        continue;
                    if (c < c1) {
                        list.add(j, new Entry(id, str));
                        break label_1;
                    }
                }
                j++;
            }
            if (!contains(str))
                list.add(new Entry(id, str));
            id++;
        }
    }

    public void removeFromList(String str) {
        for (Entry entry : list) {
            if (str.equals(entry.displayString)) {
                list.remove(entry);
                this.deselectEntry(entry);
            }
        }
    }

    public void clearList() {
        list.clear();
    }

    public List<Entry> getEntries() {
        return new ArrayList<Entry>(list);
    }

    public List<Entry> getVisible() {
        List<Entry> list = Lists.newArrayList();
        int i = 0;
        int i1 = 0;
        while (i < currentPage) {
            list.clear();
            for (int i2 = 0; i1 < this.list.size(); i2++) {
                if (list.size() >= 8)
                    break;
                Entry entry = this.list.get(i1);
                entry.setPos(i2);
                list.add(entry);
                i1++;
            }
            i++;
        }
        return list;
    }

    public List<Entry> getSelected() {
        return new ArrayList<Entry>(this.selected);
    }

    public void selectEntry(Entry entry) {
        if (!list.contains(entry))
            list.add(entry);
        if (!entry.isSelected())
            this.selected.add(entry);
    }

    public void deselectEntry(Entry entry) {
        if (entry.isSelected())
            selected.remove(entry);
    }

    public void clearSelection() {
        this.selected.clear();
    }

    public int getTotalPages() {
        double pages = list.size() / 8D;
        return MathHelper.ceiling_double_int(pages);
    }

    public Object getPageNum() {
        return this.currentPage;
    }

    public void nextPage() {
        this.currentPage = Math.min(currentPage + 1, getTotalPages());
    }

    public void previousPage() {
        this.currentPage = Math.max(currentPage - 1, 1);
    }

    public void saveEntries() throws IOException {
        FileWriter writer = new FileWriter(dictionary);
        for (Entry entry : list) {
            writer.write(entry.displayString);
            writer.write("\n");
        }
        writer.close();
        loadEntries();
    }

    public void loadEntries() throws IOException {
        clearList();
        for (String val : IOUtils.readLines(new FileReader(dictionary))) {
            TCSpellCheckManager.getInstance().addToIgnoredWords(val);
            addToList(val);
        }
    }

    public void mouseClicked(int x, int y, int button) {
        if (x > x() && x < x() + width() && y > y() && y < y() + height())
            for (Entry entry : getVisible()) {
                if (y > entry.y() && y < entry.y() + entry.height()) {
                    entry.func_146113_a(Minecraft.getMinecraft().getSoundHandler());
                    actionPerformed(entry);
                    return;
                }
            }
    }

    private void actionPerformed(Entry entry) {
        entry.setSelected(!entry.isSelected());
    }

    public int width() {
        return width;
    }

    public void width(int w) {
        this.width = w;
    }

    public int height() {
        return height;
    }

    public void height(int h) {
        this.height = h;
    }

    public int x() {
        return xPosition;
    }

    public void x(int x) {
        this.xPosition = x;
    }

    public int y() {
        return yPosition;
    }

    public void y(int y) {
        this.yPosition = y;
    }

    public class Entry extends PrefsButton {

        private TCSettingList list = TCSettingList.this;
        private int pos;

        public Entry(int id, String value) {
            super(id, TCSettingList.this.x(), 0, TCSettingList.this.width(), 12, value);
        }

        public void setPos(int y) {
            this.pos = y;
        }

        public boolean isSelected() {
            return list.getSelected().contains(this);
        }

        public void setSelected(boolean value) {
            if (value)
                list.selectEntry(this);
            else
                list.deselectEntry(this);
        }

        @Override
        public void drawButton(Minecraft mc, int x, int y) {
            if (this.isSelected())
                this.bgcolor = 0xDD999999;
            else
                this.bgcolor = 0xDD000000;
            this.y(list.y() + (pos * 12));
            super.drawButton(mc, x, y);
        }

        public void remove() {
            list.list.remove(this);
        }

        public void keyClicked(int x, int y, int button) {

        }
    }
}
