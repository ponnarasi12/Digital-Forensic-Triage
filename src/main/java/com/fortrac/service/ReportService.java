package com.fortrac.service;

import com.fortrac.model.Artifact;
import com.fortrac.model.ForensicCase;
import com.fortrac.model.RiskLevel;
import com.fortrac.repository.ArtifactRepository;
import com.fortrac.repository.CaseRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    public ByteArrayInputStream generatePdfReport(Long caseId) {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Optional<ForensicCase> optCase = caseRepository.findById(caseId);
            ForensicCase fCase = optCase.orElse(new ForensicCase("1042", "Incriminatory Incident", "Lead Analyst", "Automated Triage Case"));

            // Title Header
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Paragraph title = new Paragraph("FORTRAC FORENSIC INVESTIGATION REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph caseNum = new Paragraph("CASE REF #" + fCase.getCaseNumber() + " — " + fCase.getTitle(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLUE));
            caseNum.setAlignment(Element.ALIGN_CENTER);
            caseNum.setSpacingAfter(15);
            document.add(caseNum);

            // Summary Table
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.addCell(new PdfPCell(new Phrase("Investigator:", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
            summaryTable.addCell(new PdfPCell(new Phrase(fCase.getInvestigatorName())));
            summaryTable.addCell(new PdfPCell(new Phrase("NIST SP 800-86 Alignment:", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
            summaryTable.addCell(new PdfPCell(new Phrase("Triage, Correlation, Explainable Scoring")));
            summaryTable.setSpacingAfter(20);
            document.add(summaryTable);

            // Top Priority Evidence Header
            Paragraph evidenceHeader = new Paragraph("CRITICAL & HIGH PRIORITY EVIDENCE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.RED));
            evidenceHeader.setSpacingAfter(10);
            document.add(evidenceHeader);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 4, 2, 2});

            // Table Headers
            PdfPCell c1 = new PdfPCell(new Phrase("Artifact Name", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            PdfPCell c2 = new PdfPCell(new Phrase("File Path", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            PdfPCell c3 = new PdfPCell(new Phrase("Risk Score", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            PdfPCell c4 = new PdfPCell(new Phrase("Level", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));

            c1.setBackgroundColor(Color.LIGHT_GRAY);
            c2.setBackgroundColor(Color.LIGHT_GRAY);
            c3.setBackgroundColor(Color.LIGHT_GRAY);
            c4.setBackgroundColor(Color.LIGHT_GRAY);

            table.addCell(c1);
            table.addCell(c2);
            table.addCell(c3);
            table.addCell(c4);

            List<Artifact> artifacts = artifactRepository.findByCaseIdOrderByRiskScoreDesc(caseId);
            for (Artifact a : artifacts) {
                if (a.getRiskLevel() == RiskLevel.CRITICAL || a.getRiskLevel() == RiskLevel.SUSPICIOUS) {
                    table.addCell(a.getFileName() != null ? a.getFileName() : "N/A");
                    table.addCell(a.getFilePath() != null ? a.getFilePath() : "N/A");
                    table.addCell(String.valueOf(a.getRiskScore()) + "/100");
                    table.addCell(a.getRiskLevel().name());
                }
            }
            document.add(table);

            document.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
