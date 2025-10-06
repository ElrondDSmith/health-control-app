package org.snp.telegraminputservice.handler;

import org.snp.telegraminputservice.messages.MessagesProperties;

import java.util.List;

public abstract class AbstractInputHandler extends AbstractCommandHandler {

    public AbstractInputHandler(MessagesProperties messagesProperties) {
        super(messagesProperties);
    }

    @Override
    protected boolean allowAnyText() {
        return true;
    }

    @Override
    protected List<String> commands() {
        return List.of();
    }
}
