package com.example.multifunctionalchat.cli.user;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.example.multifunctionalchat.cli.RemoveCurlyBraces;
import com.example.multifunctionalchat.cli.validator.InputUsernameValidator;

import static com.example.multifunctionalchat.cli.RemoveCurlyBraces.removeCurlyBraces;

@Parameters(commandNames = { "//user" })
public class UserModeratorCommand {
    @Parameter(
            names = { "moderator" },
            order = 1,
            validateWith = InputUsernameValidator.class,
            required = true
    )
    private String userLogin;

    @Parameter(
            names = { "-n" },
            order = 2,
            required = true
    )
    private boolean isModerator;

    @Parameter(
            names = { "-d" },
            order = 2,
            required = true
    )
    private boolean isNotModerator;

    public String getUserLogin() {
        return removeCurlyBraces(userLogin);
    }

    public boolean isModerator() {
        return isModerator;
    }

    public boolean isNotModerator() {
        return isNotModerator;
    }
}
