package com.bookverse.entity;

// Section 13 of the spec requires supporting BOTH pre-generated MP3
// uploads (Method A, the reliable primary path) AND free/local TTS
// generation (Method B, optional). This tag on StoryAudio records which
// path produced a given audio file - useful for admin visibility and
// for knowing which rows are safe to regenerate/replace.
public enum AudioSourceType {
    UPLOADED,
    TTS_GENERATED
}
