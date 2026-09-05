package com.bookverse.entity;

// Mirrors a common content-workflow pattern: a story can be drafted and
// edited by an admin before going live, then taken down later without
// deleting it outright (its progress/favorites/history rows would
// otherwise orphan or need cascading deletes).
public enum StoryStatus {
    DRAFT,
    PUBLISHED,
    UNPUBLISHED
}
