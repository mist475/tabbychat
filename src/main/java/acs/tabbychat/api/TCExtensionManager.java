package acs.tabbychat.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Stores the registered extension classes.
 */
public class TCExtensionManager {

    public static final TCExtensionManager INSTANCE = new TCExtensionManager();
    private final List<Class<? extends IChatExtension>> list = Lists.newArrayList();

    private TCExtensionManager() {
    }

    public List<Class<? extends IChatExtension>> getExtensions() {
        return ImmutableList.copyOf(list);
    }

    public void registerExtension(Class<? extends IChatExtension> ext) {
        if (!list.contains(ext))
            list.add(ext);
    }

    public void unregisterExtension(Class<? extends IChatExtension> ext) {
        list.remove(ext);
    }
}
