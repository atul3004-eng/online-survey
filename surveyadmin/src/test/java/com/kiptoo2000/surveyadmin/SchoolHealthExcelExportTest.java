package com.kiptoo2000.surveyadmin;

import com.kiptoo2000.surveyadmin.service.SchoolHealthExcelExport;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import org.apache.poi.ss.usermodel.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SchoolHealthExcelExportTest {
    @Test public void exportsSubmittedResponsesAcrossBatchesAndPreservesText() throws Exception {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("exportTest");
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("CREATE TABLE school_health_response (response_id BIGINT PRIMARY KEY,"
                    + " status VARCHAR(20), submitted_on TIMESTAMP, institution_name VARCHAR(100),"
                    + " contact_email VARCHAR(100), contact_phone VARCHAR(100), response_locale VARCHAR(2))").executeUpdate();
            em.createNativeQuery("CREATE TABLE school_health_answer (answer_id BIGINT PRIMARY KEY,"
                    + " response_id BIGINT, display_order INT, section_name VARCHAR(100),"
                    + " question_label VARCHAR(100), answer_value VARCHAR(100))").executeUpdate();
            em.createNativeQuery("INSERT INTO school_health_response VALUES"
                    + " (1, 'SUBMITTED', CURRENT_TIMESTAMP, 'مدرسة', 'a@example.com', '00123', 'ar'),"
                    + " (2, 'DRAFT', NULL, 'Private draft', NULL, NULL, 'en'),"
                    + " (3, 'SUBMITTED', CURRENT_TIMESTAMP, 'Empty response', NULL, NULL, 'en')").executeUpdate();
            for (int i = 1; i <= 501; i++) {
                em.createNativeQuery("INSERT INTO school_health_answer VALUES (?1, 1, ?1, 'Section', 'Question', ?2)")
                        .setParameter(1, i).setParameter(2, i == 1 ? "=1+1" : "إجابة").executeUpdate();
            }
            em.getTransaction().commit();
            List<Integer> progress = new ArrayList<Integer>();
            SchoolHealthExcelExport export = new SchoolHealthExcelExport(factory::createEntityManager);
            try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(export.generate(progress::add)))) {
                Sheet sheet = workbook.getSheetAt(0);
                assertEquals(502, sheet.getLastRowNum());
                assertEquals("مدرسة", sheet.getRow(1).getCell(2).getStringCellValue());
                assertEquals("00123", sheet.getRow(1).getCell(4).getStringCellValue());
                assertEquals(CellType.STRING, sheet.getRow(1).getCell(8).getCellType());
                assertEquals("=1+1", sheet.getRow(1).getCell(8).getStringCellValue());
                assertEquals("إجابة", sheet.getRow(501).getCell(8).getStringCellValue());
                assertEquals("3", sheet.getRow(502).getCell(0).getStringCellValue());
                assertEquals(Integer.valueOf(95), progress.get(progress.size() - 1));
                assertTrue(progress.size() >= 3);
            }
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM school_health_answer").executeUpdate();
            em.createNativeQuery("DELETE FROM school_health_response").executeUpdate();
            em.getTransaction().commit();
            try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(export.generate(value -> {})))) {
                assertEquals(0, workbook.getSheetAt(0).getLastRowNum());
                assertEquals("Response ID", workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
            }
        } finally { em.close(); factory.close(); }
    }
}
