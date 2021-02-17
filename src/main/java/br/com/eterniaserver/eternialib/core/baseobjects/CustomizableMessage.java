package br.com.eterniaserver.eternialib.core.baseobjects;

public class CustomizableMessage {

    public final String text;
    private String notes;

    public CustomizableMessage(String text, String notes) {
        this.text = text;
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}