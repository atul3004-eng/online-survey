package com.kiptoo2000.surveyadmin.service;

import com.kiptoo2000.surveyadmin.persistence.JpaUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.IntConsumer;
import javax.persistence.EntityManager;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class SchoolHealthExcelExport {
    private final java.util.function.Supplier<EntityManager> entityManagers;

    public SchoolHealthExcelExport() { this(JpaUtil::createEntityManager); }

    public SchoolHealthExcelExport(java.util.function.Supplier<EntityManager> entityManagers) {
        this.entityManagers = entityManagers;
    }
    private static final String FROM = " FROM school_health_response r LEFT JOIN school_health_answer a"
            + " ON a.response_id = r.response_id WHERE r.status = 'SUBMITTED' AND r.response_id <= ?1";
    public static final String[] HEADERS = {"Response ID", "Submitted On", "Institution", "Email",
        "Phone", "Language", "Section", "Question", "Answer"};

    public byte[] generate(IntConsumer progress) throws IOException {
        EntityManager em = entityManagers.get();
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        workbook.setCompressTempFiles(true);
        try {
            Number max = (Number) em.createNativeQuery("SELECT MAX(response_id) FROM school_health_response"
                    + " WHERE status = 'SUBMITTED'").getSingleResult();
            long upperId = max == null ? 0 : max.longValue();
            long total = ((Number) em.createNativeQuery("SELECT COUNT(*)" + FROM)
                    .setParameter(1, upperId).getSingleResult()).longValue();
            progress.accept(5);
            Sheet sheet = createSheet(workbook);
            int rowIndex = 1;
            int offset = 0;
            while (offset < total) {
                if (Thread.currentThread().isInterrupted()) { throw new IOException("Export cancelled"); }
                List<Object[]> rows = em.createNativeQuery("SELECT r.response_id, r.submitted_on,"
                        + " r.institution_name, r.contact_email, r.contact_phone, r.response_locale,"
                        + " a.section_name, a.question_label, a.answer_value" + FROM
                        + " ORDER BY r.response_id, a.display_order, a.answer_id")
                        .setParameter(1, upperId).setFirstResult(offset).setMaxResults(500).getResultList();
                if (rows.isEmpty()) { break; }
                for (Object[] values : rows) {
                    if (rowIndex == 1048576) { sheet = createSheet(workbook); rowIndex = 1; }
                    writeRow(sheet.createRow(rowIndex++), values);
                }
                offset += rows.size();
                progress.accept(5 + (int) (90L * offset / total));
            }
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                workbook.write(output);
                return output.toByteArray();
            }
        } finally {
            try { workbook.close(); }
            finally { workbook.dispose(); em.close(); }
        }
    }

    private Sheet createSheet(SXSSFWorkbook workbook) {
        Sheet sheet = workbook.createSheet("School responses " + (workbook.getNumberOfSheets() + 1));
        sheet.createFreezePane(0, 1);
        for (int i = 0; i < HEADERS.length; i++) { sheet.setColumnWidth(i, (i >= 6 ? 55 : 24) * 256); }
        writeRow(sheet.createRow(0), HEADERS);
        sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, HEADERS.length - 1));
        return sheet;
    }

    public static void writeRow(Row row, Object[] values) {
        for (int i = 0; i < values.length; i++) {
            // Explicit string cells preserve phone numbers, Arabic, and formula-like user input.
            row.createCell(i).setCellValue(values[i] == null ? "" : String.valueOf(values[i]));
        }
    }
}
