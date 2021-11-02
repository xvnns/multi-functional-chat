package com.example.multifunctionalchat.cli.splitter;

import com.beust.jcommander.converters.IParameterSplitter;

import java.util.List;

import static java.util.Arrays.asList;

public class YBotFindCommandSplitter implements IParameterSplitter {
    @Override
    public List<String> split(String s) {
        return asList(s.split("\\|\\|"));
    }
}
