package com.example.multifunctionalchat.cli.bot;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import static com.example.multifunctionalchat.cli.RemoveCurlyBraces.removeCurlyBraces;

@Parameters(commandNames = { "//yBot" })
public class YBotChannelInfo {
    @Parameter(
            names = { "channelInfo" },
            required = true
    )
    private String name;

    public String getName() {
        return removeCurlyBraces(name);
    }

    public void setName(String name) {
        this.name = name;
    }
}
