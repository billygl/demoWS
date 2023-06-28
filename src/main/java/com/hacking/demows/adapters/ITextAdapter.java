package com.hacking.demows.adapters;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public class ITextAdapter {

    public static String getPDF(String title, List<String> lines){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer;
        writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        Paragraph paragraph = new Paragraph(title);
        paragraph.setBold();
        document.add(paragraph);
        for (String line : lines) {        
            document.add(new Paragraph(line));
        }
        document.close();
        byte[] bytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
