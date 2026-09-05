package com.bookverse.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

// The admin sends the COMPLETE list of episode ids for a story, in the
// new desired order - e.g. [5, 3, 4] means "episode 5 is now #1, episode
// 3 is now #2, episode 4 is now #3". Simpler for the frontend to build
// (drag-and-drop naturally produces "the new full order") than sending
// individual position swaps.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReorderEpisodesRequestDTO {
    @NotEmpty(message = "Episode order list cannot be empty")
    private List<Long> episodeIdsInOrder;
}
