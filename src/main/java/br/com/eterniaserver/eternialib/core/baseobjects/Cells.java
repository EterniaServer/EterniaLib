package br.com.eterniaserver.eternialib.core.baseobjects;

public class Cells {

    private String cells;

    public void set(String type, Object value) {
        this.cells = type + "='" + value + "'";
    }

    public String get() {
        return cells;
    }

}
