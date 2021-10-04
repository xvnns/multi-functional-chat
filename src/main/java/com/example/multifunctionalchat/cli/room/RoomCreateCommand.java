package com.example.multifunctionalchat.cli.room;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.example.multifunctionalchat.cli.validator.InputRoomNameValidator;
import lombok.Getter;

import static com.example.multifunctionalchat.cli.RemoveCurlyBraces.removeCurlyBraces;

@Parameters(commandNames = { "//room" })
@Getter
public class RoomCreateCommand {
    @Parameter(
            names = { "create" },
            order = 1,
            validateWith = InputRoomNameValidator.class,
            required = true
    )
    private String roomName;

    @Parameter(
            names = { "-c" },
            order = 2
    )
    private boolean isPrivate;

    public String getRoomName() {
        return removeCurlyBraces(roomName);
    }

    public boolean isPrivate() {
        return isPrivate;
    }
}
