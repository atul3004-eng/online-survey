package com.kiptoo2000.survey.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.kiptoo2000.survey.model.*;
import com.kiptoo2000.survey.bean.SchoolHealthSurveyBean;
import java.io.*;
import java.util.*;
import javax.faces.model.SelectItem;

public class SchoolHealthPdf {
    private static final Map<String, String> GROUPS = new HashMap<String, String>();
    static {
        GROUPS.put("institutionType", "institutionTypes");
        GROUPS.put("currentStatus", "statusOptions");
        GROUPS.put("targetedTopics", "targetedTopics");
        GROUPS.put("targetPopulation", "targetPopulations");
        GROUPS.put("primaryGrades", "primaryGrades");
        GROUPS.put("preparatoryGrades", "preparatoryGrades");
        GROUPS.put("secondaryGrades", "secondaryGrades");
        GROUPS.put("coverage", "coverageOptions");
        GROUPS.put("performanceIndicators", "indicatorOptions");
        GROUPS.put("partnersInvolved", "yesNoOptions");
        GROUPS.put("coordinationMechanism", "yesNoOptions");
        GROUPS.put("documentsDeveloped", "documentStatusOptions");
        GROUPS.put("humanResources", "humanResources");
        GROUPS.put("capacityBuilding", "documentStatusOptions");
        GROUPS.put("trainingFrequency", "trainingFrequencies");
        GROUPS.put("infrastructureSupport", "infrastructureSupports");
    }
    public byte[] generate(SchoolHealthResponse response, java.util.List<SchoolHealthAnswer> questions,
            SchoolHealthSurveyBean bean) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 42, 42, 42, 42);
        PdfWriter.getInstance(document, bytes);
        byte[] fontBytes;
        try (InputStream stream = getClass().getResourceAsStream("/fonts/DejaVuSans.ttf")) {
            if (stream == null) { throw new IOException("Missing PDF font"); }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[8192]; int size;
            while ((size = stream.read(chunk)) != -1) { buffer.write(chunk, 0, size); }
            fontBytes = buffer.toByteArray();
        }
        BaseFont base = BaseFont.createFont("DejaVuSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED,
                true, fontBytes, null);
        Font normal = new Font(base, 10); Font bold = new Font(base, 11, Font.BOLD);
        document.open();
        document.add(new Paragraph("School Health Questionnaire", new Font(base, 18, Font.BOLD)));
        document.add(new Paragraph("Response #" + response.getId() + " | Submitted: " + response.getSubmittedOn(), normal));
        Map<String, java.util.List<String>> values = new LinkedHashMap<String, java.util.List<String>>();
        for (SchoolHealthAnswer answer : response.getAnswers()) {
            if (!values.containsKey(answer.getQuestionKey())) { values.put(answer.getQuestionKey(), new ArrayList<String>()); }
            values.get(answer.getQuestionKey()).add(answer.getAnswerValue());
        }
        String section = null;
        for (SchoolHealthAnswer question : questions) {
            if (!question.getSectionName().equals(section)) {
                section = question.getSectionName();
                addBlock(document, section, bold);
            }
            java.util.List<String> saved = values.get(question.getQuestionKey());
            StringBuilder answer = new StringBuilder();
            if (saved != null) {
                for (String value : saved) {
                    String label = value;
                    String group = GROUPS.get(question.getQuestionKey());
                    if (group != null) {
                        for (SelectItem option : bean.options(group)) {
                            if (value.equals(option.getValue())) { label = option.getLabel(); break; }
                        }
                    }
                    if (answer.length() > 0) { answer.append("; "); }
                    answer.append(label);
                }
            }
            PdfPTable pair = new PdfPTable(1);
            pair.setWidthPercentage(100); pair.setSpacingBefore(7); pair.setKeepTogether(true); pair.setSplitLate(false);
            pair.addCell(cell(question.getQuestionLabel(), bold));
            pair.addCell(cell(answer.length() == 0 ? "Not answered" : answer.toString(), normal));
            document.add(pair);
        }
        document.close();
        return bytes.toByteArray();
    }
    private void addBlock(Document document, String text, Font font) throws DocumentException {
        PdfPTable table = new PdfPTable(1); table.setWidthPercentage(100); table.setSpacingBefore(7);
        table.setSplitLate(false);
        table.addCell(cell(text, font)); document.add(table);
    }
    private PdfPCell cell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER); cell.setPadding(3);
        if (text.matches("(?s).*[\u0600-\u06ff].*")) { cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL); }
        return cell;
    }
}
