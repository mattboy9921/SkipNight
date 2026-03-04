package net.mattlabs.skipnight.api.messaging;

/**
 * Provides access to messaging configuration and runtime messaging settings.
 *
 * <p>This interface acts as a lightweight context for the {@link Messages} system,
 * supplying values that are required when constructing message components.</p>
 *
 * <p>Implementations are typically provided by the platform-specific module and
 * passed to {@link Messages#Initialize(MessagesContext)} during plugin startup.</p>
 */
public interface MessagesContext {

    /**
     * Returns the {@link Messages.General} configuration section.
     *
     * <p>This section contains shared message values such as the configured
     * strings for "day", "night", "yes", "no", and the vote header label.</p>
     *
     * @return the general messaging configuration
     */
    Messages.General general();

    /**
     * Indicates whether the vote message header should be disabled.
     *
     * <p>If this returns {@code true}, methods such as
     * {@link Messages#voteHeader()} will return an empty component instead
     * of the standard "[Vote]" prefix.</p>
     *
     * @return {@code true} if the header should be disabled, otherwise {@code false}
     */
    boolean isHeaderDisabled();
}
