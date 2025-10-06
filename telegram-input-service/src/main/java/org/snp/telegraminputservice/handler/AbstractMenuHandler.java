package org.snp.telegraminputservice.handler;

import org.snp.telegraminputservice.messages.MessagesProperties;

public abstract class AbstractMenuHandler extends AbstractCommandHandler {

    public AbstractMenuHandler(MessagesProperties messagesProperties) {
        super(messagesProperties);
    }

    @Override
    protected boolean allowAnyText() {
        return false;
    }
}
