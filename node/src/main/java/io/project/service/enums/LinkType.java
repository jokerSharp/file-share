package io.project.service.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LinkType {

    GET_DOC("files/doc"),
    GET_PHOTO("files/photo");

    private final String link;

    @Override
    public String toString() {
        return link;
    }
}
