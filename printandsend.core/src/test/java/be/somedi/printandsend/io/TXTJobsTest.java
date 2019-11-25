package be.somedi.printandsend.io;

import be.somedi.printandsend.model.Address;
import be.somedi.printandsend.model.UMFormat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TXTJobsTest {

    private TXTJobs txtJobs;

    @Before
    public void setup() {
        Path path = Paths.get("src/test/resources/MSE_183030005_2976737_A9671.txt");
        txtJobs = new TXTJobs(path);
    }

    @Test
    public void getTextAfterKey() {
        String result = txtJobs.getTextAfterKey("DR");
        assertEquals("A9671", result);

        result = txtJobs.getTextAfterKey("PS");
        assertEquals("BOSSESTRAAT 5", result);
    }

    @Test
    public void getBody() {
        String result = txtJobs.getBodyOfTxt(UMFormat.MEDIDOC);
        String expected = "Betreft : uw patiënt(e) Van Acker Sylvie geboren op 14/09/1982   en\r\n" +
                " wonende \r\n" +
                "BOSSESTRAAT 5 te 2220 Heist-op-den-Berg. \r\n" +
                "\r\n" +
                " Consultatie : 09/02/2018 met referentienr : 183030005 \r\n" +
                "\r\n" +
                "Dit is een test van Kenneth Van Gijsel.\r\n" +
                "Dit is een hele lange zin, langer dan 75 karakters en dat mag niet in\r\n" +
                " Medidoc formaat. Dit moet gesplits worden.\r\n" +
                "En dan liefst dat dit mee op de vorige zin komt. En nu gaan we het nog een\r\n" +
                " beetje moeilijker maken. Door nog een zin\r\n" +
                "toe te voegen die veel te lang is. Is zien hoe het programma hier mee\r\n" +
                " omgaat!\r\n" +
                "Werkt niet ...\r\n" +
                "\r\n" +
                "BESLUIT:\r\n" +
                "]Mijn besluit regel 1.\r\n" +
                "]Mijn besluit regel 2.\r\n" +
                "]Mijn besluit regel 3.\r\n" +
                "]Mijn besluit regel 4.\r\n" +
                "]Mijn besluit regel 5.\r\n" +
                "]Mijn besluit regel 6.\r\n" +
                "Mijn besluit regel 7.\r\n" +
                "Mijn besluit regel 8.";

        assertEquals(expected, result);
    }

    @Test
    public void getFileName() {
        String actual = txtJobs.getFileName();
        String expected = "MSE_183030005_2976737_A9671.txt";
        assertEquals(expected, actual);
    }

    @Test
    public void containsVulAan() {
        Path path = Paths.get("src/test/resources/vul_aan.txt");
        txtJobs = new TXTJobs(path);
        assertTrue(txtJobs.containsVulAan());
    }

    @Test
    public void containsSentenceToDelete() {
        Path path = Paths.get("src/test/resources/geenverslag.txt");
        txtJobs = new TXTJobs(path);
        assertTrue(txtJobs.containsSentenceToDelete());
    }

    @Test
    public void bevatStandaardHeader() {
        Path path = Paths.get("src/test/resources/MSE_183030005_2976737_A9671.txt");
        txtJobs = new TXTJobs(path);
        assertTrue(txtJobs.getIndex(TXTJobs.GEACHTE) != 0);
        assertTrue(txtJobs.getIndex(TXTJobs.BETREFT) != 0);
    }

    @Test
    public void geenHeader(){
        Path path = Paths.get("src/test/resources/MSE_183030005_2976737_S5754.txt");
        txtJobs = new TXTJobs(path);
        assertTrue(txtJobs.getIndex(TXTJobs.BETREFT) == 0 && txtJobs.getIndex(TXTJobs.GEACHTE) == 0);
        assertEquals(TXTJobs.LEEG, txtJobs.getBodyOfTxt(UMFormat.MEDIDOC));
    }

    @Test
    public void headerZonderGeachte(){
        Path path = Paths.get("src/test/resources/MSE_181210127_2976738_S6686.txt");
        txtJobs = new TXTJobs(path);
        assertTrue(txtJobs.getIndex(TXTJobs.BETREFT) != 0);
    }

    @Test
    public void headerZonderBetreft(){
        Path path = Paths.get("src/test/resources/MSE_179430104_2818577_Z8025.txt");
        txtJobs = new TXTJobs(path);
        assertTrue(txtJobs.getIndex(TXTJobs.GEACHTE) != 0);
    }

    @Test
    public void bevatStandaardFooter() {
        Path path = Paths.get("src/test/resources/MSE_181210127_2976738_S8533.txt");
        txtJobs = new TXTJobs(path);
        assertTrue(txtJobs.getIndex(TXTJobs.closure) != 0);

        path = Paths.get("src/test/resources/MSE_183030005_2976737_A9671.txt");
        txtJobs = new TXTJobs(path);
        assertTrue(txtJobs.getIndex(TXTJobs.closure) != 0);
    }

    @Test
    public void footerMetDubbeleBegroeting() {
        Path path = Paths.get("src/test/resources/MSE_181210127_2976738_Z6686.txt");
        txtJobs = new TXTJobs(path);
        assertTrue(txtJobs.getIndex(TXTJobs.closure) != 0);
    }

    @Test
    public void footerZonderMet() {
        Path path = Paths.get("src/test/resources/MSE_4190022145_3525292_B8060.txt");
        txtJobs = new TXTJobs(path);
        assertTrue(txtJobs.getIndex(TXTJobs.closure) != 0);
    }

    @Test
    public void footerFout(){
        Path path = Paths.get("src/test/resources/slechte_footer.txt");
        txtJobs = new TXTJobs(path);
        assertEquals(0, txtJobs.getIndex(TXTJobs.closure));
        assertEquals(TXTJobs.LEEG, txtJobs.getBodyOfTxt(UMFormat.MEDIDOC));
    }

    @Test
    public void footerMetMeerdereWhitespaces() {
        Path path = Paths.get("src/test/resources/MSE_4190022015_3524709_A7938.txt");
        txtJobs = new TXTJobs(path);
        assertTrue(txtJobs.getIndex(TXTJobs.closure) != 0);
    }

    @Test
    public void bevatBesluit() {
        int index = txtJobs.getIndex(TXTJobs.BESLUIT);
        assertTrue(index != 0);
        assertEquals(35, index);
    }

    @Test
    public void testCopyAndDelete() throws IOException {
        Path newPath = Paths.get("src/test/resources/copy.txt");
        txtJobs.moveTxtFile(newPath);
        assertTrue(Files.exists(newPath));
        Files.delete(newPath);
        assertFalse(Files.exists(newPath));
    }

    @Test
    public void getAddressOfPatient() {
        Address addressOfPatient = txtJobs.getAddressOfPatient();
        assertEquals("Heist-op-den-Berg", addressOfPatient.getCity());
        assertEquals("2220", addressOfPatient.getZip());
        assertEquals("5", addressOfPatient.getNumber());
        assertEquals("BOSSESTRAAT", addressOfPatient.getStreet().trim());
    }

    @Test
    public void getResearchDate() {
        String researchDate = txtJobs.getResearchDate();
        assertEquals("20180209", researchDate);
    }

    @Test
    public void getMedidocGender() {
        String medidocGender = txtJobs.getMedidocGender();
        assertEquals("X", medidocGender);
    }

    @Test
    public void externalIdZonderVofM(){
        Path path = Paths.get("src/test/resources/nieuwExternalId.txt");
        txtJobs = new TXTJobs(path);

        String medidocGender =txtJobs.getMedidocGender();
        assertEquals("X", medidocGender);

        String externalId = txtJobs.getTextAfterKey("PC");
        assertEquals("00555202", externalId);
    }
}