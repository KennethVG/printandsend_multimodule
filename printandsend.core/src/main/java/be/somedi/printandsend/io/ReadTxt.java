package be.somedi.printandsend.io;

import be.somedi.printandsend.exceptions.PathNotFoundException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.*;

public class ReadTxt {

    //CONSTANTEN:
    private static final String BETREFT = "Betreft";
    private static final String MVG = "Met vriendelijke groeten";
    private static final String MCG = "Met collegiale groeten";
    private static final String MCH = "Met collegiale hoogachting";
    private static final String BESLUIT = "BESLUIT";
    private static final String VUL_AAN = "vul_aan";
    private static final int MAX_LINE_LENGTH = 75;

    private Path path;
    private List<String> allLines = new ArrayList<>();

    public ReadTxt() {
        this(null, Charset.forName("windows-1252"));
    }

    public ReadTxt(Path path) {
        this(path, Charset.forName("windows-1252"));
    }

    public ReadTxt(Path path, Charset charset) {
        this.path = path;
        try {
            allLines = Files.readAllLines(path, charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path getPath() {
        if (Files.notExists(path)) {
            throw new PathNotFoundException("Path bestaat niet!");
        }
        return path;
    }

    public List<String> getAllLines() {
        try {
            return Files.readAllLines(path, Charset.forName("windows-1252"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public void setAllLines(List<String> allLines) {
        this.allLines = allLines;
    }

    public String getFileName() {
        return String.valueOf(getPath().getFileName());
    }

    public void deleteTxtFile() throws IOException {
        Files.delete(getPath());
    }

    public void moveTxtFile(Path moveToFolder) throws IOException {
        Files.copy(getPath(), moveToFolder, StandardCopyOption.REPLACE_EXISTING);
    }

    public boolean containsVulAan() {
        for (String line : allLines) {
            if (line.contains(VUL_AAN)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsSentenceToDelete() throws IOException {
        URL url = getClass().getClassLoader().getResource("deleteFileList.txt");
        if (url != null) {
            Path path = new File(url.getFile()).toPath();
            List<String> allItems = Files.readAllLines(path);

            for (String line : allLines) {
                for (String item : allItems) {
                    if (line.toLowerCase().contains(item.toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getExternalId() {
        String externalIdOfCaregiver = getTextAfterKey("DR");
        if (externalIdOfCaregiver == null || externalIdOfCaregiver.equals("") || externalIdOfCaregiver.length() > 7) {
            externalIdOfCaregiver = null;
        }
        return externalIdOfCaregiver;
    }

    public String getTextAfterKey(String key) {
        for (String s : getAllLines()) {
            if (s.trim().startsWith("#" + key.toUpperCase())) {
                return s.trim().substring(4);
            }
        }
        return "";
    }

    public String getBodyOfTxt() {
        StringBuilder result = new StringBuilder();
        int startIndex = 0;
        int endIndex = 0;
        int summaryIndex = 0;

        for (int i = 0; i < allLines.size(); i++) {
            String oneLine = allLines.get(i).trim();
            if (startsWith(oneLine, BETREFT)) {
                startIndex = i;
            } else if (startsWith(oneLine.toUpperCase(), BESLUIT)) {
                summaryIndex = i;
            } else if (startsWith(oneLine, MVG) || startsWith(oneLine, MCH) || startsWith(oneLine, MCG)) {
                endIndex = i;
            }
        }

        if (summaryIndex != 0) {
            if (endIndex - summaryIndex <= 7) {
                putBracketBeforeLineOfSummary(summaryIndex, endIndex);
            } else {
                putBracketBeforeLineOfSummary(summaryIndex, summaryIndex + 8);
            }
        }

        removeFromList(startIndex, endIndex);

        for (int i = 0; i < allLines.size(); i++) {
            String oneLine = allLines.get(i);

            if (oneLine.length() > MAX_LINE_LENGTH) {
                String firstPart = left(oneLine, MAX_LINE_LENGTH);
                String secondPart = substring(oneLine, MAX_LINE_LENGTH) + " ";
                result.append(firstPart).append("\r\n");

                String newLine = secondPart + allLines.get(++i);
                while (newLine.length() > MAX_LINE_LENGTH) {
                    firstPart = left(newLine, MAX_LINE_LENGTH);
                    result.append(firstPart).append("\r\n");
                    secondPart = substring(newLine, MAX_LINE_LENGTH) + " ";
                    newLine = secondPart + allLines.get(++i);
                }

                result.append(newLine).append("\r\n\r\n");

            } else {
                result.append(oneLine).append("\r\n");
            }
        }
        return result.toString();
    }

    private void putBracketBeforeLineOfSummary(int summaryIndex, int endIndex) {
        for (int i = summaryIndex + 1; i < endIndex; i++) {
            String newLine = "] " + allLines.get(i);
            if (!newLine.equals("] ")) {
                allLines.remove(i);
                allLines.add(i, newLine);
            }
        }
    }

    private void removeFromList(int startIndex, int endIndex) {
        List<String> removeList = new ArrayList<>();
        if (startIndex != 0) {
            for (int i = 0; i < startIndex; i++) {
                removeList.add(allLines.get(i));
            }
        }
        if (endIndex != 0) {
            for (int i = endIndex; i < allLines.size(); i++) {
                removeList.add(allLines.get(i));
            }
        }
        removeList.removeIf(s -> s.equals(""));
        allLines.removeAll(removeList);
    }
}
