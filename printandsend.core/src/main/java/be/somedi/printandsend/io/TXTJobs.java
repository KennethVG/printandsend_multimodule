package be.somedi.printandsend.io;

import be.somedi.printandsend.model.Address;
import be.somedi.printandsend.model.UMFormat;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;

public class TXTJobs {

    //CONSTANTEN:
    static final String BETREFT = "betreft";
    static final String GEACHTE = "geachte";
    static final String BESLUIT = "besluit";
    static final String[] closure = {"vriendelijke groeten", "met vriendelijke", "vriendelijke hoogachting", "collegiale"};
    static final String LEEG = "leeg";
    private static final String VUL_AAN = "vul_aan";

    private static final int LINE_LENGTH = 75;
    private static final int LINE_LENGTH_SUMMARY = 74;
    private static final int SUMMARY_MAX_LENGTH = 7;

    private final Path path;
    private List<String> allLines = new ArrayList<>();

    public TXTJobs(Path path) {
        this(path, Charset.forName("windows-1252"));
    }

    private TXTJobs(Path path, Charset charset) {
        this.path = path;
        try {
            allLines = FileUtils.readLines(path.toFile(), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path getPath() {
        return path;
    }

    private List<String> getAllLines() {
        return allLines;
    }

    public Address getAddressOfPatient() {
        Address address = new Address();
        String streetAndNumber = getTextAfterKey("PS");
        Pattern pattern = Pattern.compile("([^\\d]+)\\s?(.+)");
        Matcher matcher = pattern.matcher(streetAndNumber);
        while (matcher.find()) {
            address.setStreet(matcher.group(1));
            address.setNumber(matcher.group(2));
        }
        address.setZip(getTextAfterKey("PP"));
        address.setCity(getTextAfterKey("PA"));
        return address;
    }

    public String getResearchDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDate parsedDate = LocalDate.parse(getTextAfterKey("UD"), formatter);
        formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return formatter.format(parsedDate);
    }

    public String getMedidocGender() {
        String gender = getTextAfterKey("PG").trim();
        if (gender.equalsIgnoreCase("M")) {
            return "Y";
        } else if (gender.equalsIgnoreCase("F")) {
            return "X";
        } else {
            String externalIdPatient = getTextAfterKey("PC");
            if (externalIdPatient == null) return "Z";
            externalIdPatient = externalIdPatient.toUpperCase();
            if (externalIdPatient.startsWith("M")) {
                return "Y";
            } else if (externalIdPatient.startsWith("V")) {
                return "X";
            } else return "Z";
        }
    }

    String getFileName() {
        return String.valueOf(getPath().getFileName());
    }

    boolean deleteTxtFile() {
        return FileUtils.deleteQuietly(getPath().toFile());
    }

    void moveTxtFile(Path moveToFolder) throws IOException {
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

    public boolean containsSentenceToDelete() {
        Path path = Paths.get("C:\\PrintApp\\deleteFileList.txt");
        if (Files.exists(path)) {
            List<String> allItems;
            try {
                allItems = Files.readAllLines(path);
                for (String line : allLines) {
                    for (String item : allItems) {
                        if (line.trim().toLowerCase().contains(item.trim().toLowerCase())) {
                            return true;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getExternalIdOfCaregiverTo() {
        String externalIdOfCaregiver = getTextAfterKey("DR");
        if (externalIdOfCaregiver == null || externalIdOfCaregiver.equals("") || externalIdOfCaregiver.length() > 7) {
            externalIdOfCaregiver = null;
        }
        return externalIdOfCaregiver;
    }

    public String getExternalIdOfCaregiverFrom() {
        String externalIdOfCaregiver = getTextAfterKey("UA");
        if (externalIdOfCaregiver == null || externalIdOfCaregiver.equals("") || externalIdOfCaregiver.length() > 7) {
            return null;
        } else {
            boolean vanOpstal = externalIdOfCaregiver.equalsIgnoreCase("C6904") || externalIdOfCaregiver.equalsIgnoreCase("D6904");
            return vanOpstal ? "S690V" : "S" + externalIdOfCaregiver.substring(1);
        }
    }

    public String getTextAfterKey(String key) {
        for (String s : getAllLines()) {
            if (s.trim().startsWith("#" + key.toUpperCase())) {
                return s.trim().substring(4);
            }
        }
        return "";
    }

    public int getIndex(String... word) {
        for (int i = 0; i < allLines.size(); i++) {
            String oneLine = allLines.get(i).trim();
            boolean startWith = Arrays.stream(word).anyMatch(w -> startsWithIgnoreCase(oneLine, w));
            boolean contains = Arrays.stream(word).anyMatch(w -> containsIgnoreCase(deleteWhitespace(oneLine), deleteWhitespace(w)));
            if (startWith || contains) {
                return i;
            }
        }
        return 0;
    }

    public String getBodyOfTxt(UMFormat format) {

        StringBuilder result = new StringBuilder();
        int startIndex = getIndex(BETREFT) != 0 ? getIndex(BETREFT) : getIndex(GEACHTE);
        int startSummaryIndex = getIndex(BESLUIT);
        int endIndex = getIndex(closure);

        String oneLine;
        if (startIndex != 0 && endIndex != 0) {
            // Er is een besluit. Max. 7 lijnen starten met ]
            if (startSummaryIndex != 0 && format == UMFormat.MEDIDOC) {
                result.append(buildBody(startIndex, startSummaryIndex + 1));
                for (int j = startSummaryIndex + 1; j < endIndex && j < startSummaryIndex + SUMMARY_MAX_LENGTH;
                     j++) {
                    oneLine = allLines.get(j).trim();
                    if (oneLine.length() > LINE_LENGTH) {
                        String summary = left(oneLine, LINE_LENGTH_SUMMARY);
                        int summaryIndex = lastIndexOf(summary, " ");
                        if (summaryIndex != -1) {
                            result.append("]")
                                    .append(summary, 0, summaryIndex)
                                    .append("\r\n").append("]")
                                    .append(substring(oneLine, summaryIndex)).append("\r\n");
                        }
                    } else if (oneLine.equals("")) {
                        result.append(oneLine).append("\r\n");
                    } else {
                        result.append("]").append(oneLine).append("\r\n");
                    }
                }
                if (endIndex > startSummaryIndex + SUMMARY_MAX_LENGTH) {
                    result.append(buildBody(startSummaryIndex + SUMMARY_MAX_LENGTH, endIndex));
                }
            } else if (startSummaryIndex != 0 && format == UMFormat.MEDAR) {
                result.append(buildBody(startIndex, startSummaryIndex));
                result.append("/CONCL\r\n").append(buildBody(startSummaryIndex, endIndex));
            } else {
                result.append(buildBody(startIndex, endIndex));
            }
        } else {
            return LEEG;
        }
        return result.toString().trim();
    }

    private String buildBody(int startIndex, int endIndex) {
        StringBuilder result = new StringBuilder();
        for (int i = startIndex; i < endIndex; i++) {
            String oneLine = allLines.get(i);
            if (oneLine.length() > LINE_LENGTH) {
                String first75 = left(oneLine, LINE_LENGTH);
                int index = lastIndexOf(first75, " ");
                result.append(substring(first75, 0, index)).append("\r\n").append(substring
                        (oneLine, index)).append("\r\n");
            } else {
                result.append(oneLine).append("\r\n");
            }
        }
        return result.toString();
    }
}
