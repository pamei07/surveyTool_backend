package iks.surveytool.utils.builder;

import iks.surveytool.entities.CheckboxGroup;

import java.util.ArrayList;

public class CheckboxGroupBuilder {
    public CheckboxGroup createCheckboxGroup(Long id, boolean multipleSelect, int minSelect, int maxSelect) {
        CheckboxGroup newCheckboxGroup = new CheckboxGroup(multipleSelect, minSelect, maxSelect, new ArrayList<>());
        newCheckboxGroup.setId(id);
        return newCheckboxGroup;
    }
}
