package com.nebula.haseeb.winiff.utils.checklist;

/**
 * Created by haseeb on 4/5/16.
 */
public class CheckableListItem {
    String name;
    boolean value;

    public CheckableListItem(String name, boolean value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
