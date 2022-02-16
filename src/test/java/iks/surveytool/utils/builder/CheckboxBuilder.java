package iks.surveytool.utils.builder;

import iks.surveytool.entities.Checkbox;

import java.util.List;

public class CheckboxBuilder {
    public Checkbox createCheckbox(Long id, String text, boolean hasTextField) {
        Checkbox newCheckbox = new Checkbox(text, hasTextField, "Default Text");
        newCheckbox.setId(id);
        return newCheckbox;
    }

    public List<Checkbox> createListOfFourValidCheckboxes() {
        Checkbox first = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox second = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);
        Checkbox third = new CheckboxBuilder()
                .createCheckbox(3L, "Third Test Checkbox", true);
        Checkbox fourth = new CheckboxBuilder()
                .createCheckbox(4L, "Fourth Test Checkbox", false);

        return List.of(first, second, third, fourth);
    }

    public List<Checkbox> createListOfThreeValidCheckboxes() {
        Checkbox first = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox second = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", false);
        Checkbox third = new CheckboxBuilder()
                .createCheckbox(3L, "Third Test Checkbox", true);

        return List.of(first, second, third);
    }
}
