package net.mattlabs.skipnight.api.messaging;

public interface MessagesContext {

    Messages.General general();

    boolean isHeaderDisabled();
}
