package br.com.eterniaserver.eternialib.uuidfetcher.enums;

public enum UUIDResponseStatus {

    OK(200),
    NOT_FOUND(404);

    private final int code;

    UUIDResponseStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

}
