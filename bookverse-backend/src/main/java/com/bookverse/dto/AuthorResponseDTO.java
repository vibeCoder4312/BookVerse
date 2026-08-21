package com.bookverse.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorResponseDTO {
    private Long id;
    private String name;
    private String bio;
    private String photoUrl;
    private long bookCount; // how many books this author has - handy for admin listing
}
