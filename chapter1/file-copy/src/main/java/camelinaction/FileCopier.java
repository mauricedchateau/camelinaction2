package camelinaction;

import java.io.File;
import java.nio.file.Files;
public class FileCopier {

    public static void main(String[] args) throws Exception {
        File inboxDirectory = new File("data/inbox");
        File outboxDirectory = new File("data/outbox");
        
        outboxDirectory.mkdir();

        Files.list(inboxDirectory.toPath())
            .forEach(source -> {
                File dest = new File(
                        outboxDirectory.getPath()
                        + File.separator
                        + source.getFileName().toString());
                try {
                    Files.copy(source, dest.toPath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
    }
}
