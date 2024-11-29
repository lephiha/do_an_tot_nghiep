package com.lephiha.do_an.Model;

import java.util.ArrayList;
import java.util.List;

public class Option {
    private String name;
    private int icon;

    public Option() {
    }

    public Option(int icon, String name) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}

