package be.somedi.printandsend.io;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

public class WatchServiceOfDirectory {

    private final WatchService watchService;

    public WatchServiceOfDirectory() throws IOException {
        this.watchService  = FileSystems.getDefault().newWatchService();

    }

}
