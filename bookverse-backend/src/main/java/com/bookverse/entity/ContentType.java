package com.bookverse.entity;

// Every piece of content on BookVerse is one of these types.
// Having this as an enum (not free text) keeps filtering/sorting on the
// Explore page reliable - the frontend can build a dropdown from this
// fixed list instead of guessing what strings exist in the database.
public enum ContentType {
    BOOK,
    NOVEL,
    MANGA,
    MANHWA,
    MANHUA,
    COMIC,
    GRAPHIC_NOVEL,
    LIGHT_NOVEL,
    STUDY_BOOK,
    CHILDRENS_BOOK
}
