package pl.gajewski.chatapp.exceptions;

import pl.gajewski.chatapp.commands.AbstractCmd;

public class CommandResponseException extends Exception {

    private AbstractCmd command;

    public CommandResponseException(String detailMessage) {
        super(detailMessage);
    }

    public CommandResponseException(String detailMessage, AbstractCmd command) {
        super(detailMessage);
        this.command = command;
    }

    public AbstractCmd getCommand() {
        return command;
    }

}