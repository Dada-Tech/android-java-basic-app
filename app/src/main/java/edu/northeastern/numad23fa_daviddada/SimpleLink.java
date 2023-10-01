package edu.northeastern.numad23fa_daviddada;

import androidx.annotation.NonNull;

import java.util.Locale;

public class SimpleLink {
    public String title;
    public String url;

    public int id;
    private static int lastId = 0;

    public SimpleLink(String title, String url) {
        this.url = url;
        this.title = title;
        this.id = ++lastId;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.US,
                "SimpleLink{title='%s', url='%s', id=%d}", title, url, id);
    }
}
