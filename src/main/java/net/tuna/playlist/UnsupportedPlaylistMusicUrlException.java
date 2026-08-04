package net.tuna.playlist;

public class UnsupportedPlaylistMusicUrlException extends IllegalArgumentException {
    public UnsupportedPlaylistMusicUrlException(String message) {
        super(message);
    }
}
