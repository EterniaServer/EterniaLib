package br.com.eterniaserver.eternialib.sql.objects;

public class Values {

    private String values;

    public void set(String... args) {
        this.values = "('" + String.join("', '", args) + ")";
    }

    public String get() {
        return values;
    }

}
