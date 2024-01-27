package org.semenov.service.enums;

/*
 * Идентификторы ресурсов при генерации ссылки
 */
public enum LinkType {
    GET_DOC("file/get-doc"),
    GET_PHOTO("file/get-photo");
    private final String link;

    LinkType(String link) {
        this.link = link;
    }

    @Override
    public String toString(){
        return link;
    }

}
