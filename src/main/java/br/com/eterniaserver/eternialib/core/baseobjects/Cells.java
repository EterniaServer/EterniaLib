package br.com.eterniaserver.eternialib.core.baseobjects;

public class Cells {

    private String cellString;

    public void set(String type, Object value) {
        this.cellString = type + "='" + value + "'";
    }

    public String get() {
        return cellString;
    }

}
