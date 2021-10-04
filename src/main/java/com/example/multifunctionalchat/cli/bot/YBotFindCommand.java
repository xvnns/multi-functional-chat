package com.example.multifunctionalchat.cli.bot;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.example.multifunctionalchat.cli.splitter.YBotFindCommandSplitter;
import lombok.Getter;

import java.util.List;

@Parameters(commandNames = { "//yBot" })
@Getter
public class YBotFindCommand {
    @Parameter(
            names = { "find" },
            order = 1,
            splitter = YBotFindCommandSplitter.class,
            arity = 2,
            required = true
    )
    private List<String> n;

    @Parameter(
            names = { "-v" },
            order = 2
    )
    private boolean viewsNumber;

    @Parameter(
            names = { "-l" },
            order = 3
    )
    private boolean likesNumber;
}