package com.example.multifunctionalchat.cli.validator;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.util.regex.Pattern;

public class InputRoomNameValidator implements IParameterValidator {
    public static final String REGEX = "\\{(\\w+\\s*)*\\}";
    @Override
    public void validate(String name, String value) throws ParameterException {
        if (!isValidName(value)) {
            throw new ParameterException(
                    "String parameter " + value + " is not a valid");
        }
    }

    private boolean isValidName(String value) {
        return Pattern
                .compile(REGEX)
                .matcher(value).matches();
    }
}
