package net.tuna.music;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MusicSource {
    private String provider;
    private String resourceId;
}
