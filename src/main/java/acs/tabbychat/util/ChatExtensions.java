package acs.tabbychat.util;

import acs.tabbychat.api.IChatExtension;

import java.util.ArrayList;
import java.util.List;

public class ChatExtensions {

    private final List<IChatExtension> list = new ArrayList<>();

    public ChatExtensions(List<Class<? extends IChatExtension>> list) {
        for (Class<? extends IChatExtension> ext : list) {
            try {
                IChatExtension exten = ext.getDeclaredConstructor().newInstance();
                exten.load();
                this.list.add(exten);
            }
            catch (Exception e) {
                TabbyChatUtils.log.error("Unable to initialize " + ext.getName(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends IChatExtension> List<T> getListOf(Class<T> extClass) {
        List<T> t = new ArrayList<>();
        for (IChatExtension ext : list) {
            if (extClass.isInstance(ext))
                t.add((T) ext);
        }
        return t;
    }

}
