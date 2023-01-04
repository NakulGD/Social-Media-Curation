package timedelayqueue;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * TransientPubSubMessage is a class that extends the PubSubMessage class
 * and adds a lifetime property to each message.
 * It provides several constructors to create TransientPubSubMessage objects
 * and has a method to retrieve the lifetime of a message.
 * It also overrides the isTransient method from its superclass to always return true.
 */

/**
 * Representation Invariant:
 * 'lifetime' >= 0
 *  isTransient = True
 *  Abstraction Function:
 *  The abstraction function for the TransientPubSubMessage class
 *  maps each TransientPubSubMessage instance to the properties
 *  of its underlying PubSubMessage instance (id, timestamp, sender,
 *  receiver, content, and type), as well as its own 'lifetime' property
 *  and the constant value 'true' for its 'isTransient' property.
 *
 *  Thread Safety Argument:
 *  The TransientPubSubMessage class is thread-safe as field values are marked with 'final'
 *  and no methods modify any values
 */
public class TransientPubSubMessage extends PubSubMessage {
    private final int lifetime;
    private final boolean isTransient = true;

    public TransientPubSubMessage(UUID id, Timestamp timestamp, UUID sender,
                                  UUID receiver, String content, MessageType type, int lifetime) {
        super(id, timestamp, sender, receiver, content, type);
        this.lifetime = lifetime;
    }

    public TransientPubSubMessage(UUID id, Timestamp timestamp, UUID sender,
                                  List<UUID> receiver, String content, MessageType type, int lifetime) {
        super(id, timestamp, sender, receiver, content, type);
        this.lifetime = lifetime;
    }

    public TransientPubSubMessage(UUID sender, List<UUID> receiver, String content, int lifetime) {
        super(sender, receiver, content);
        this.lifetime = lifetime;
    }

    public TransientPubSubMessage(UUID sender, UUID receiver, String content, int lifetime) {
        super(sender, receiver, content);
        this.lifetime = lifetime;
    }

    /**
     * @return lifetime
     */
    public int getLifetime() {
        return lifetime;
    }

    /**
     * @return isTransient = True
     */
    @Override
    public boolean isTransient() {
        return isTransient;
    }
}
