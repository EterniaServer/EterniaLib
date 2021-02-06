package br.com.eterniaserver.eternialib.sql.objects;

public class Columns {

    private String columns;

    public void set(String... args) {
        this.columns = "(" + String.join(", ", args) + ")";
    }

    public String get() {
        return columns;
    }

}
