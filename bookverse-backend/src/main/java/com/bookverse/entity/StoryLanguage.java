package com.bookverse.entity;

// Section 7 of the spec: exactly two languages supported initially.
// Kept as its own enum (not reusing anything) since language is a
// story-specific concern - books don't currently model language at all.
public enum StoryLanguage {
    ENGLISH,
    HINDI
}
