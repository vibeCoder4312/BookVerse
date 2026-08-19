package com.bookverse.entity;

// An enum restricts a field to a fixed set of values.
// A user's role can ONLY ever be USER or ADMIN - nothing else.
// This is safer than storing role as a plain String, because the
// compiler catches typos (e.g. "ADMNI") that a String field would allow.
public enum Role {
    USER,
    ADMIN
}
