package com.bookverse.dto;

import com.bookverse.entity.Role;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
