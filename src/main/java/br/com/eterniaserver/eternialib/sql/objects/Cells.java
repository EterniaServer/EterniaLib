package br.com.eterniaserver.eternialib.sql.objects;

public class Cells {

    private String cells;

    public void set(String type, String value) {
        this.cells = type + "='" +value + "'";
    }

    public String get() {
        return cells;
    }

}
