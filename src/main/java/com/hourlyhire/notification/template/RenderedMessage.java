package com.hourlyhire.notification.template;

/** The final text to deliver. If "sensitive", the body is never saved in the database. */
public record RenderedMessage(String subject, String body, boolean sensitive) {

    public String storedBody() {
        if (sensitive) {
            return "[content not stored]";
        }
        return body.length() <= 2000 ? body : body.substring(0, 2000);
    }
}