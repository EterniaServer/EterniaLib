package br.com.eterniaserver.eternialib.core.baseobjects;

public class Columns {

    private String columns;

    public void set(String... args) {
        this.columns = "(" + String.join(", ", args) + ")";
    }

    public String get() {
        return columns;
    }

}
