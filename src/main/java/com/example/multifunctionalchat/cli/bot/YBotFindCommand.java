package com.example.multifunctionalchat.cli.bot;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.example.multifunctionalchat.cli.splitter.YBotFindCommandSplitter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.example.multifunctionalchat.cli.RemoveCurlyBraces.removeCurlyBraces;

@Parameters(commandNames = { "//yBot" })
public class YBotFindCommand {
    @Parameter(
            names = { "find" },
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

    public List<String> getNames() {
        List<String> list = new ArrayList<>();
        for (String str : names) {
            list.add(removeCurlyBraces(str));
        }
        return list;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public boolean isViewsNumber() {
        return viewsNumber;
    }

    public void setViewsNumber(boolean viewsNumber) {
        this.viewsNumber = viewsNumber;
    }

    public boolean isLikesNumber() {
        return likesNumber;
    }

    public void setLikesNumber(boolean likesNumber) {
        this.likesNumber = likesNumber;
    }
}