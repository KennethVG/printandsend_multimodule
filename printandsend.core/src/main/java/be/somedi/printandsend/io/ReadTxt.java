package be.somedi.printandsend.io;

import be.somedi.printandsend.exceptions.PathNotFoundException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ReadTxt {

    private Path path;
    private Charset charset;

    public ReadTxt() {
        this(null, Charset.forName("windows-1252"));
    }

    public ReadTxt(Path path) {
        this(path, Charset.forName("windows-1252"));
    }

    public ReadTxt(Path path, Charset charset) {
        this.path = path;
        this.charset = charset;
    }

    public Path getPath() {
        if (Files.notExists(path)) {
            throw new PathNotFoundException("Path bestaat niet!");
        }
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Charset getCharset() {
        return charset;
    }

    public String getFileName(){
        return String.valueOf(getPath().getFileName());
    }

    public void deleteTxtFile() throws IOException {
        Files.delete(getPath());
    }

    public void moveTxtFile(Path moveToFolder) throws IOException {
        Files.copy(getPath(), moveToFolder, StandardCopyOption.REPLACE_EXISTING);
    }

    public boolean containsVulAan() throws IOException {
        List<String> fullDocument  = Files.readAllLines(getPath(), charset);
        for (String line : fullDocument){
            if(line.contains("vul_aan")){
                return true;
            }
        }
        return false;
    }

    public boolean containsSentenceToDelete(){
        //TODO: to implement
        return false;
    }

    public String getTextAfterKey(String key) {
        try {
            List<String> fullDocument = Files.readAllLines(getPath(), charset);
            for (String s : fullDocument) {
                if (s.startsWith("#" + key.toUpperCase())) {
                    return s.trim().substring(4);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
