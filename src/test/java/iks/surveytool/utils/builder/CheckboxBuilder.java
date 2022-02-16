package iks.surveytool.utils.builder;

import iks.surveytool.entities.Checkbox;

public class CheckboxBuilder {
    public Checkbox createCheckbox(Long id, String text, boolean hasTextField) {
        Checkbox newCheckbox = new Checkbox(text, hasTextField, "Default Text");
        newCheckbox.setId(id);
        return newCheckbox;
    }
}
