package com.example.multifunctionalchat.cli.bot;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.example.multifunctionalchat.cli.splitter.YBotFindCommandSplitter;

import java.util.List;

@Parameters(commandNames = { "//yBot" })
public class YBotChannelInfo {
    @Parameter(
            names = { "channelInfo" },
            order = 1,
            splitter = YBotFindCommandSplitter.class,
            arity = 2,
            required = true
    )
    private List<String> names;

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
