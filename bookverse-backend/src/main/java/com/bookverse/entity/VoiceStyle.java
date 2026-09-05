package com.bookverse.entity;

// Section 14 of the spec: narrator tone should match the story's genre,
// not be randomly assigned. This enum is the "menu" of tones a story
// can be tagged with; the admin picks one when creating/editing a story,
// and it doubles as the voice preset selector when Phase 10 (free TTS)
// generates narration - both jobs, one field.
public enum VoiceStyle {
    DEEP_SUSPENSEFUL,      // Horror
    DRAMATIC_SERIOUS,      // Thriller, Crime
    WARM_EMOTIONAL,        // Romance, Drama
    ENERGETIC_FRIENDLY,    // Kids & Family, Comedy
    CLEAR_PROFESSIONAL,    // Educational, College/Study
    CONFIDENT_INSPIRATIONAL, // Motivation, Self Development
    NEUTRAL                // fallback for categories without a strong tone
}
