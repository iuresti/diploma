package org.tunavalley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class DiplomaApplication {


    public static void main(String[] args) throws Exception {
        File inputDir = new File("input");
        File outputDir = new File("output");

        if (!inputDir.exists()) {
            throw new RuntimeException("input directory does not exist");
        }

        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new RuntimeException("output directory can't be created");
        }

        File[] listFiles = inputDir.listFiles(pathname -> pathname.getName().endsWith(".list"));

        if (listFiles == null || listFiles.length == 0) {
            throw new RuntimeException("No .list files found in input directory");
        }

        for (File listFile : listFiles) {
            String baseName = listFile.getName();

            baseName = baseName.substring(0, baseName.lastIndexOf('.'));
            File pdfFile = new File(inputDir, baseName + ".pdf");

            if (!pdfFile.exists()) {
                throw new RuntimeException("Expected template file " + pdfFile.getName() + " does not exist");
            }

            File parentOutput = new File(outputDir, baseName);

            if(!parentOutput.mkdir()){
                throw new RuntimeException("Can't create directory " + parentOutput);
            }

            Scanner scanner = new Scanner(listFile);

            while (scanner.hasNext()) {
                String name = scanner.nextLine().trim();
                if (!name.isEmpty()) {
                    File outputFile = new File(parentOutput, name + ".pdf");
                    writeUsingIText(pdfFile, outputFile, name);
                }
            }
        }
    }

    private static void writeUsingIText(File originalFile, File outputFile, String personName) throws IOException, DocumentException {

        Document document = new Document(new RectangleReadOnly(792.0F, 612.0F));

        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));

        PdfReader reader = new PdfReader(new FileInputStream(originalFile));
        PdfImportedPage page = writer.getImportedPage(reader, 1);

        //open
        document.open();

        PdfContentByte canvas = writer.getDirectContent();
        canvas.addTemplate(page, 0, 0);


        Font helvetica = new Font(Font.FontFamily.HELVETICA, 18);
        helvetica.setColor(BaseColor.WHITE);
        BaseFont bf_helv = helvetica.getCalculatedBaseFont(false);

        canvas.beginText();
        canvas.setColorFill(BaseColor.WHITE);
        canvas.setFontAndSize(bf_helv, chooseFontSize(personName.length()));
        canvas.showTextAligned(Element.ALIGN_LEFT, personName, 370, 270, 0);
        canvas.endText();

        //close
        document.close();


    }

    private static float chooseFontSize(int length) {

        if (length < 27) {
            return 30;
        }
        if (length < 32) {
            return 28;
        }
        if (length < 34) {
            return 23;
        }
        if (length < 38) {
            return 21;
        }

        return 19;
    }
}
