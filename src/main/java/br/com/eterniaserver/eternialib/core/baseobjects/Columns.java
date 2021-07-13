package br.com.eterniaserver.eternialib.core.baseobjects;

public class Columns {

    private String columnString;

    public void set(String... args) {
        this.columnString = "(" + String.join(", ", args) + ")";
    }

    public String get() {
        return columnString;
    }

}
