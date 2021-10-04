package com.example.multifunctionalchat.cli.user;

import com.beust.jcommander.Parameters;
import com.example.multifunctionalchat.cli.RemoveCurlyBraces;
import com.beust.jcommander.Parameter;
import com.example.multifunctionalchat.cli.validator.InputUsernameValidator;

import static com.example.multifunctionalchat.cli.RemoveCurlyBraces.removeCurlyBraces;

@Parameters(commandNames = { "//user" })

public class UserRenameCommand {
    @Parameter(
            names = { "rename" },
            order = 1,
            validateWith = InputUsernameValidator.class,
            required = true
    )
    private String userLogin;

    @Parameter(
            names = { "-l" },
            order = 2,
            validateWith = InputUsernameValidator.class,
            required = true
    )
    private String newUserLogin;

    public String getUserLogin() {
        return removeCurlyBraces(userLogin);
    }

    public String getNewUserLogin() {
        return removeCurlyBraces(newUserLogin);
    }
}
