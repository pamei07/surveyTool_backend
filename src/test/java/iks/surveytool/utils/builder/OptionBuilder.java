package iks.surveytool.utils.builder;

import iks.surveytool.entities.Option;

import java.util.List;

public class OptionBuilder {
    public Option createOption(Long id, String text) {
        Option newOption = new Option(text);
        newOption.setId(id);
        return newOption;
    }

    public List<Option> createListOfFourValidOptions() {
        Option first = new OptionBuilder()
                .createOption(1L, "First Test Option");
        Option second = new OptionBuilder()
                .createOption(2L, "Second Test Option");
        Option third = new OptionBuilder()
                .createOption(3L, "Third Test Option");
        Option fourth = new OptionBuilder()
                .createOption(4L, "Fourth Test Option");

        return List.of(first, second, third, fourth);
    }
}
