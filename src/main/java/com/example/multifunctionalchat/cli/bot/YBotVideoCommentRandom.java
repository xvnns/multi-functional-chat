package com.example.multifunctionalchat.cli.bot;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Getter;

import static com.example.multifunctionalchat.cli.RemoveCurlyBraces.removeCurlyBraces;

@Parameters(commandNames = { "//yBot" })
@Getter
public class YBotVideoCommentRandom {
    @Parameter(
            names = { "videoCommentRandom" },
            order = 1,
            required = true
    )
    private String names;

    public String getNames() {
        return removeCurlyBraces(names) ;
    }
}
