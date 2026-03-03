package net.mattlabs.skipnight.plugin.util;

import net.mattlabs.skipnight.api.config.Config;
import net.mattlabs.skipnight.api.messaging.Messages;
import net.mattlabs.skipnight.api.messaging.MessagesContext;

public class PluginMessagesContext implements MessagesContext {

    private final Messages messages;
    private final Config config;

    public PluginMessagesContext(Messages messages, Config config) {
        this.messages = messages;
        this.config = config;
    }
    @Override
    public Messages.General general() {
        return messages.general();
    }

    @Override
    public boolean isHeaderDisabled() {
        return config.isHeaderDisabled();
    }
}
