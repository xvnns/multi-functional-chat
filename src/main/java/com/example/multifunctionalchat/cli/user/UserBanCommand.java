package com.example.multifunctionalchat.cli.user;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.example.multifunctionalchat.cli.validator.InputUsernameValidator;

import static com.example.multifunctionalchat.cli.RemoveCurlyBraces.removeCurlyBraces;

@Parameters(commandNames = { "//user" })
public class UserBanCommand {
    @Parameter(
            names = { "ban" },
            order = 1,
            required = true
    )
    private boolean blockUser;

    @Parameter(
            names = { "-l" },
            order = 2,
            validateWith = InputUsernameValidator.class,
            required = true
    )
    private String login;

    @Parameter(
            names = { "-d" },
            order = 3
    )
    private int minutes;

    public boolean isBlockUser() {
        return blockUser;
    }

    public String getLogin() {
        return removeCurlyBraces(login);
    }

    public int getMinutes() {
        return minutes;
    }
}
