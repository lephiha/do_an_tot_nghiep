package com.lephiha.do_an.Model;

public class Setting {
    private int icon;
    private String id;
    private String name;

    public Setting(int icon, String id, String name) {
        this.icon = icon;
        this.id = id;
        this.name = name;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
