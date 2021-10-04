package com.example.multifunctionalchat.cli.room;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.example.multifunctionalchat.cli.validator.InputRoomNameValidator;
import com.example.multifunctionalchat.cli.validator.InputUsernameValidator;
import lombok.Getter;

import static com.example.multifunctionalchat.cli.RemoveCurlyBraces.removeCurlyBraces;

@Parameters(commandNames = { "//room" })
public class RoomDisconnectCommand {
    @Parameter(
            names = { "disconnect" },
            order = 1,
            validateWith = InputRoomNameValidator.class,
            required = true
    )
    private String roomName;

    @Parameter(
            names = { "-l" },
            validateWith = InputUsernameValidator.class,
            order = 2
    )
    private String userLogin;

    @Parameter(
            names = { "-m" },
            order = 3
    )
    private String minutes;

    public String getRoomName() {
        return removeCurlyBraces(roomName);
    }

    public String getUserLogin() {
        return removeCurlyBraces(userLogin);
    }

    public String getMinutes() {
        return removeCurlyBraces(minutes);
    }
}
