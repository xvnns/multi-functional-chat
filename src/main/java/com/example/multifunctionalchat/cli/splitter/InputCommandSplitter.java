package com.example.multifunctionalchat.cli.splitter;

import com.beust.jcommander.converters.IParameterSplitter;

import java.util.List;
import static java.util.Arrays.asList;

public class InputCommandSplitter implements IParameterSplitter {

    @Override
    public List<String> split(String s) {
        String splitRegex = "\\s+(?=((\\\\[\\{]|[^\\{])*\\{(\\\\[\\{]|[^\\}])*\\})*(\\\\[\\}]|[^\\}])*\\s*)";
        return asList(s.split(splitRegex));
    }
}
