package com.example.multifunctionalchat.cli.room;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.example.multifunctionalchat.cli.validator.InputRoomNameValidator;
import com.example.multifunctionalchat.cli.validator.InputUsernameValidator;
import lombok.Getter;

import static com.example.multifunctionalchat.cli.RemoveCurlyBraces.removeCurlyBraces;

@Parameters(commandNames = { "//room" })
@Getter
public class RoomRenameCommand {
    @Parameter(
            names = { "rename" },
            order = 1,
            validateWith = InputRoomNameValidator.class,
            required = true
    )
    private String roomName;

    @Parameter(
            names = { "-n" },
            order = 2,
            validateWith = InputUsernameValidator.class,
            required = true
    )
    private String newRoomName;

    public String getRoomName() {
        return removeCurlyBraces(roomName);
    }

    public String getNewRoomName() {
        return removeCurlyBraces(newRoomName);
    }
}
