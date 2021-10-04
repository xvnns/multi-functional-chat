package com.example.multifunctionalchat.cli.room;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.example.multifunctionalchat.cli.validator.InputRoomNameValidator;
import lombok.Getter;

import static com.example.multifunctionalchat.cli.RemoveCurlyBraces.removeCurlyBraces;

@Parameters(commandNames = { "//room" })
public class RoomRemoveCommand {
    @Parameter(
            names = { "remove" },
            order = 1,
            validateWith = InputRoomNameValidator.class,
            required = true
    )
    private String roomName;

    public String getRoomName() {
        return removeCurlyBraces(roomName);
    }
}
