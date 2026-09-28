package JSF.reports;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.*;
import javax.persistence.*;

@ManagedBean(name = "surveyReportController")
@RequestScoped
public class SurveyReportController {
    // Uses the host application's default persistence unit. Specify unitName if it has several.
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;
    private static final Properties GRANTS = loadGrants();

    private static Properties loadGrants() {
        Properties grants = new Properties();
        try (InputStream in = SurveyReportController.class.getResourceAsStream("/survey-report-access.properties")) {
            if (in == null) { throw new IllegalStateException("Missing survey-report-access.properties"); }
            grants.load(in);
            return grants;
        } catch (IOException ex) { throw new IllegalStateException("Cannot load report access", ex); }
    }

    /** Integration point 1: your existing authenticated session user ID. */
    public String currentUserId() {
        Object user = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("adUser");
        return user == null ? null : user.toString();
    }

    /** Integration point 2: replace these sample grants with your user/survey database lookup. */
    public boolean allowed(String survey) {
        String userId = currentUserId();
        if (userId == null || survey == null || survey.isEmpty()) { return false; }
        return Arrays.stream(GRANTS.getProperty(userId, "").split(","))
                .map(String::trim).anyMatch(survey::equals);
    }

    public boolean isSchoolAllowed() { return allowed("school"); }
    public boolean isWellnessAllowed() { return allowed("wellness"); }
    public void downloadSchool() throws IOException { download("school"); }
    public void downloadWellness() throws IOException { download("wellness"); }

    private void download(String survey) throws IOException {
        FacesContext faces = FacesContext.getCurrentInstance();
        ExternalContext context = faces.getExternalContext();
        // Recheck on every download; hiding a button alone is not authorization.
        if (!allowed(survey)) {
            context.responseSendError(403, "You do not have access to this survey report.");
            faces.responseComplete();
            return;
        }
        byte[] bytes;
        try {
            if (entityManagerFactory == null) { throw new IllegalStateException("Persistence unit was not injected"); }
            bytes = "school".equals(survey)
                    ? new SchoolHealthExcelExport(entityManagerFactory::createEntityManager).generate(progress -> { })
                    : new WellnessExcelExport(entityManagerFactory::createEntityManager).generate(progress -> { });
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Survey report generation failed", ex);
            faces.addMessage("surveyReports", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Download failed", "The report could not be generated. Please try again."));
            return;
        }
        context.responseReset();
        context.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        context.setResponseHeader("Content-Disposition", "attachment; filename=\"" + survey + "-survey-responses.xlsx\"");
        context.setResponseHeader("Cache-Control", "no-store");
        context.setResponseContentLength(bytes.length);
        context.getResponseOutputStream().write(bytes);
        faces.responseComplete();
    }
}
