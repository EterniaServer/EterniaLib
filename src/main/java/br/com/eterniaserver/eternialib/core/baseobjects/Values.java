package br.com.eterniaserver.eternialib.core.baseobjects;

public class Values {

    private String valueString;

    public void set(Object... args) {
        StringBuilder stringBuilder = new StringBuilder("('");
        for (int i = 0; i < args.length; i++) {
            if (i + 1 == args.length) stringBuilder.append(args[i]).append("')");
            else stringBuilder.append(args[i]).append("', '");
        }
        this.valueString = stringBuilder.toString();
    }

    public String get() {
        return valueString;
    }

}
