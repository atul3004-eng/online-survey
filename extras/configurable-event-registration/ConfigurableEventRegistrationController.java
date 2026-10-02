/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JSF;

import ejb.Action;
import ejb.Applicants;
import ejb.EventMaster;
import ejb.EventRegData;
import ejb.RegisterationMaster;
import ejb.RegisterationMasterValues;
import ejb.RegistrationLimit;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;

import javax.faces.context.FacesContext;

import javax.persistence.EntityManager;

import javax.transaction.SystemException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormat.Field;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.faces.validator.RegexValidator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import util.ActiveDirectoryService;
import util.EventDateUtil;
import util.JsfUtil;


/**
 *
 * @author rchoudhary
 */
@ManagedBean(name = "configurableEventRegistrationController")
@ViewScoped
public class ConfigurableEventRegistrationController implements Serializable {

    private static final long serialVersionUID = 1L;

    public EventMaster current;
    public EventMaster selected;
    List<EventRegData> fieldsDetails = new ArrayList<>();
    EventMaster item;
    List<Action> ddlValues = new ArrayList<>();
    List<Action> radioValues = new ArrayList<>();
    RegisterationMasterValues regValues;
    private RegisterationMaster registrationMaster;
    private RegisterationMaster currentRegistration;
    public boolean showOnLoad = true;
    public boolean regClosed = false;
    public boolean showOnreg = false;
    public boolean invalidLink = false;
    public boolean showSubmit = true;
    public boolean showUpdate = false;
    public EventMaster eventid;
    List<Map<String, String>> applicantDetails = new ArrayList<Map<String, String>>();
    Map<String, String> regdetails = new LinkedHashMap<>();
    private String email;
    private String mobile;
    public String fullName;
    public String organization;
    private String license ;
    private Configuration cfg = null;
    RegistrationLimit daterange;
    private DataModel<Field> model;
    private RegexValidator regexValidator;
    private String eventDate = "";
    public EventMaster createEvent;
    public RegistrationLimit eventRegLimit;
    UploadedFile fileUpload;
    EventMaster event;
    public EventRegData uploadEventFields;
    public String genratedUrl;
    public EventMaster eventForUrl;
    public EventRegData updateFields;
    public Boolean emailNotPresnt = false;
    private String attaendanceDateValue;
    private Map<String, String> applicantDetailsSingle;
    private String eventUuid;
    private BigDecimal applicantId;
    private String attaendanceDate ;
    
    private String adUser;

    @EJB
    sb.EventMasterFacade ejbFacade;
    // cfg.setClassForTemplateLoading(SurveyServlet.class, "");
    @EJB
    sb.EventRegDataFacade eventRegDataFacade;

    @EJB
    sb.RegisterationMasterFacade registerationMasterFacade;

    @EJB
    sb.RegisterationMasterValuesFacade registerationMasterValuesFacade;

    @EJB
    sb.ActionFacade actionFacade;

    @EJB
    sb.MasterFacade masterFacade;

    @ManagedProperty(value = "#{language}")
    private LanguageBean language;

    @EJB
    sb.RegistrationLimitFacade registrationLimitFacade;

    private Integer parentValue = null;
    private String cvFolder;
    private String radioValue ;

    @PostConstruct
    public void init() {
        current = new EventMaster();
        createEvent = new EventMaster();
        updateFields = new EventRegData();
        eventRegLimit = new RegistrationLimit();
        regValues = new RegisterationMasterValues();
        registrationMaster = new RegisterationMaster();
        eventid = new EventMaster();
        cfg = new Configuration();

        //    cfg.setClassForTemplateLoading(FileUploadController.class, "/");
        cfg.setClassLoaderForTemplateLoading(ConfigurableEventRegistrationController.class.getClassLoader(), "/templates");
        regexValidator = new RegexValidator();

        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        String lang = params.get("lang");
        if (lang != null) {
            FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(lang));

            if ("en".equals(lang)) {
                language.setLocale(Locale.ENGLISH);
            } else if ("ar".equals(lang)) {
                language.setLocale(new Locale("ar"));
            }
        }

        if (params.get("applicantId") != null) {
            applicantId = new BigDecimal(params.get("applicantId"));
            loadApplicantDetails();
        }

        if (params.get("eventId") != null) {
            BigDecimal eventId = new BigDecimal(params.get("eventId"));
            current = ejbFacade.getRecordByEventId(eventId);  
        }   
    }

    public List<EventRegData> eventFieldDetails() {

        fieldsDetails = eventRegDataFacade.getAllRecordsByUUID(current.getEvent_uuid());

        return fieldsDetails;

    }
    public String generateUrlForApplicant(BigDecimal applicantId) {
         String baseUrl = JsfUtil.getBaseUrl();
        String lang = language.isLangEn() ? "en" : "ar";
        return baseUrl + "/faces/viewRegistration.xhtml?applicantId=" + applicantId
                + "&eventId=" + item.getEventId() + "&lang=" + lang;
    }
    
    public boolean isCurrentUserAuthorized() {
        try {
            String loggedUser = (String) FacesContext.getCurrentInstance()
                                     .getExternalContext()
                                     .getSessionMap().get("adUser");

            if (loggedUser == null || current.getAdUser() == null) {
                return false;
            }

            return Arrays.stream(current.getAdUser().split(","))
                         .map(String::trim)
                         .anyMatch(u -> u.equalsIgnoreCase(loggedUser));

        } catch (Exception e) {
            return false;
        }
    }

    
    
    public void loadApplicantDetails() {
        applicantDetailsSingle = new LinkedHashMap<>();
        currentRegistration = registerationMasterFacade.find(applicantId);
        RegisterationMaster regMaster = registerationMasterFacade.getRecordbyRegId(applicantId);
        List<RegisterationMasterValues> regValues = registerationMasterValuesFacade.getrecordByRegId(regMaster);

        Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        SimpleDateFormat timeDate = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        for (RegisterationMasterValues values : regValues) {
            String fieldName = currentLocale.getLanguage().equals("ar")
                ? values.getFieldId().getFieldname_ar()
                : values.getFieldId().getFieldName();

            String fieldValue = values.isIsaction()
                    ? actionFacade.getRecordValueById(Integer.parseInt(values.getFieldValues()))
                    : values.getFieldValues();

            applicantDetailsSingle.put(fieldName, fieldValue);
        }

        applicantDetailsSingle.put("Registration Date", timeDate.format(regMaster.getRegisterationValues().get(0).getReg_date()));
    }
    
    
    public void eventApplicantDetails() {
        List<RegisterationMasterValues> registrationDetails = new ArrayList<>();
        applicantDetails = new ArrayList<Map<String, String>>();
        String uuids = current.getEvent_uuid();

        // Get the current locale
        Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();

        if (eventid != null) {
            List<RegisterationMaster> regId = registerationMasterFacade.getRecordByEventId(eventid);
            for (int i = 0; i < regId.size(); i++) {
                int j = 0;
                String regDay = "";
                SimpleDateFormat timeDate = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss");
                regdetails = new LinkedHashMap<>();
                regdetails.put("ApplicantId", regId.get(i).getRegisterationId().toString());

                registrationDetails = registerationMasterValuesFacade.getrecordByRegId(regId.get(i));

                for (RegisterationMasterValues values : registrationDetails) {
                    if (values != null) {
                        // Check if the current locale is Arabic, otherwise use the English field name
                        String fieldName;
                        if (currentLocale.getLanguage().equals("ar")) {
                            fieldName = values.getFieldId().getFieldname_ar() != null && !values.getFieldId().getFieldname_ar().isEmpty()
                                    ? values.getFieldId().getFieldname_ar()
                                    : values.getFieldId().getFieldName();
                        } else {
                            fieldName = values.getFieldId().getFieldName();
                        }

                        String fieldValue = values.isIsaction()
                                ? (values.getFieldValues() == null || values.getFieldValues().isEmpty()
                                ? ""
                                : actionFacade.getRecordValueById(Integer.parseInt(values.getFieldValues())))
                                : (values.getFieldValues() == null || values.getFieldValues().isEmpty()
                                ? ""
                                : values.getFieldValues());

                        regdetails.put(fieldName, fieldValue);
                    }

                    if (j == 0) {
                        regDay = timeDate.format(values.getReg_date());
                        j++;
                    }
                }

                regdetails.put("Date to Registration", regDay);
                regdetails.put("Attended", regId.get(i).getAttended() == null ? "No" : (regId.get(i).getAttended() ? "Yes" : "No"));
                regdetails.put("Survey Sent", regId.get(i).getSurvey_sent() == null ? "No" : (regId.get(i).getSurvey_sent() ? "Yes" : "No"));

                 System.out.println(regdetails);
                applicantDetails.add(regdetails);
            }
        }
    }

    public void sendPosttestSurveyLink() {

        EventMaster item = ejbFacade.getRecordByEventId(eventid.getEventId());

        if (item == null) {
            System.out.println("Event not found for UUID: " + item.getEvent_uuid());
            return;
        }

        if (applicantDetails != null && !applicantDetails.isEmpty()) {
            for (Map<String, String> details : applicantDetails) {
                String applicantId = details.get("ApplicantId");
                String email = details.get("Email Address"); // Assuming "Email Address" key exists in the map
                String testLink = "<a href=\"https://eventsreg.moph.gov.qa/NCGP/faces/test.xhtml?eventUUId=" + item.getEvent_uuid() + "$$" + applicantId + "\">Click to attend post-course survey</a>";

                try {
                    // Set up email properties
                    // Create a mail session
                    Session session = Session.getDefaultInstance(mailProperties());

                    // Create the email message
                    MimeMessage msg = new MimeMessage(session);
                    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
                    msg.setFrom(new InternetAddress(item.getEmail())); // Use EventEmail from EventMaster
                    msg.setSubject("Post Survey Notification");

                    // Load the template
                    Template template = cfg.getTemplate("postsurveyTest.ftl");

                    // Prepare template data
                    Map<String, Object> map = new HashMap<>();
                    map.put("fullName", details.get("Full Name")); // Assuming "Full Name" key exists in the map
                    map.put("organization", details.get("Organization")); // Assuming "Organization" key exists in the map
                    map.put("registrationId", applicantId);
                    map.put("testLink", testLink);
                    map.put("EventSalutation", item.getEmailSalutation()); // Add EventSalutation
                    map.put("EventEmail", item.getEmail()); // Add EventEmail
                    map.put("EventName", item.getEventTitle());

                    // Process the template into a string
                    StringWriter out = new StringWriter();
                    template.process(map, out);

                    // Set the email content
                    msg.setContent(out.toString(), "text/html");

                    // Send the email
                    Transport transport = session.getTransport();
                    transport.connect();
                    Transport.send(msg);

                    System.out.println("Post-survey email sent to " + email + " with Registration ID: " + applicantId);

                } catch (MessagingException | TemplateException | IOException e) {
                    System.out.println("Failed to send email: " + e.getMessage());
                }
            }
        }

    }

    public SelectItem[] getItemsAvailableSelectOne(String type) throws ParseException {

        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        ddlValues.clear();
        ddlValues = actionFacade.getRecordByType(type);

        List<SelectItem> countryItems = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        if ("Month".equalsIgnoreCase(type)) {
            Date today = new Date();
            DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

            for (Object x : ddlValues) {
                String monthStr = x.toString();
                Date date = format.parse(monthStr + " 30, 2021");
                if (date.after(today) && seen.add(monthStr.toUpperCase())) {
                    countryItems.add(new SelectItem(x, monthStr));
                }
            }

            countryItems.sort(Comparator.comparing(si -> si.getLabel().trim().toUpperCase()));

        } else if ("Residence2".equalsIgnoreCase(type)) {

            for (Action x : ddlValues) {
                String countryEn = x.getCountry_Name_En() != null ? x.getCountry_Name_En().trim() : "";
                String key = countryEn.toUpperCase();

                if (seen.add(key)) {
                    String label = (locale == null || locale.getLanguage().equals("en") || locale == Locale.ENGLISH)
                            ? countryEn
                            : (x.getCountry_Name_Ar() != null ? x.getCountry_Name_Ar().trim() : countryEn);
                    countryItems.add(new SelectItem(x, label));
                }
            }

            countryItems.sort((a, b) -> {
                String aLabel = a.getLabel().trim().toUpperCase();
                String bLabel = b.getLabel().trim().toUpperCase();

                if (aLabel.equals("QATAR")) {
                    return -1;
                }
                if (bLabel.equals("QATAR")) {
                    return 1;
                }
                return aLabel.compareTo(bLabel);
            });

        }else {
    // âœ… Generic case: Sort by ID only
    for (Object x : ddlValues) {
        Action action = (Action) x;

        if (seen.add(String.valueOf(action.getId()))) {
            // âœ… Use proper label instead of toString()
            String label;

            if (locale == null || locale.getLanguage().equals("en") || locale == Locale.ENGLISH) {
                label = action.getAction_values(); // English
            } else {
                label = action.getAction_value_ar(); // Arabic
            }

            if (label == null) {
                label = ""; // avoid null
            }

            countryItems.add(new SelectItem(action, label.trim()));
        }
    }

    // âœ… Sort by ID only
    countryItems.sort(Comparator.comparing(si -> ((Action) si.getValue()).getId()));
}

        List<SelectItem> itemList = new ArrayList<>();
        String selectLabel = (locale == null || locale.getLanguage().equals("en") || locale == Locale.ENGLISH)
                ? "Select.."
                : "\u0627\u062E\u062A\u0631..";
        itemList.add(new SelectItem(null, selectLabel));
        itemList.addAll(countryItems);

        return itemList.toArray(new SelectItem[0]);
    }





    public UploadedFile getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(UploadedFile fileUpload) {
        this.fileUpload = fileUpload;
    }

    public SelectItem[] getItemsAvailableSelectOneEvent() {

        List<EventMaster> values = ejbFacade.getAllRecords();
        boolean selectOne = true;
        int size = selectOne ? values.size() + 1 : values.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            if (locale == null || locale.getLanguage().equals("en") || locale == Locale.ENGLISH) {
                items[0] = new SelectItem("", "Select..");
            } else {
                items[0] = new SelectItem("", "Ã˜Â§Ã˜Â®Ã˜ÂªÃ˜Â±..");
            }
            i++;
        }

        for (Object x : values) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;

    }
   public void assignUserToEvent() {
       ActiveDirectoryService adService =new ActiveDirectoryService();
    if (adUser == null  || eventid == null) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "User or Event is missing.", null));
        return;
    }
    
    if (!adService.checkUserExists(adUser)) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "User does not exist in Active Directory.", null));
        return;
    }

    EventMaster event = eventid; 
    if (event != null) {
        String currentAdUsers = event.getAdUser();
        String newUser = adUser;

        if (currentAdUsers == null || currentAdUsers.isEmpty()) {
            event.setAdUser(newUser);
        } else {
            List<String> userList = new ArrayList<>(Arrays.asList(currentAdUsers.split(",")));
            if (!userList.contains(newUser)) {
                userList.add(newUser);
                event.setAdUser(String.join(",", userList));
            }
        }

        ejbFacade.edit(event); // persist changes

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "User assigned to event successfully.", null));
    } else {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Event not found.", null));
    }
}
   
    public void removeUserFromEvent() {
        if (adUser == null || eventid == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "User or Event is missing.", null));
            return;
        }

        EventMaster event = eventid;
        if (event == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Event not found.", null));
            return;
        }

        String currentAdUsers = event.getAdUser();
        if (currentAdUsers != null && !currentAdUsers.isEmpty()) {
            List<String> userList = new ArrayList<>(Arrays.asList(currentAdUsers.split(",")));

            if (userList.contains(adUser)) {
                userList.remove(adUser);
                event.setAdUser(userList.isEmpty() ? null : String.join(",", userList));
                ejbFacade.edit(event);

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "User removed from event successfully.", null));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "User not assigned to this event.", null));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "No users assigned to this event.", null));
        }
    }

    public SelectItem[] getUserItemsAvailableSelectOneEvent() {
        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        System.out.println("Current View ID: " + viewId);

        List<EventMaster> values = ejbFacade.getRecordByAdUser((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("adUser"));
        boolean selectOne = true;
        int size = selectOne ? values.size() + 1 : values.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;

        if (selectOne) {
            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            if (locale == null || locale.getLanguage().equals("en") || locale == Locale.ENGLISH) {
                items[0] = new SelectItem("", "Select..");
            } else {
                items[0] = new SelectItem("", "Ã˜Â§Ã˜Â®Ã˜ÂªÃ˜Â±..");
            }
            i++;
        }

        if (values.isEmpty()) {
            return new SelectItem[]{new SelectItem("", "No records available")};
        }

        Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();

        for (EventMaster event : values) {
            String displayTitle;

            // Use Arabic title if locale is Arabic, otherwise use the default title
            if ("ar".equals(currentLocale.getLanguage()) && event.getEventTitleAr() != null && !event.getEventTitleAr().isEmpty()) {
                FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale("ar"));
                displayTitle = event.getEventTitleAr();
            } else { FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale("en"));
                displayTitle = event.getEventTitle(); // Fallback to the default title if Arabic title is not available
            }

            items[i++] = new SelectItem(event, displayTitle);
        }

        return items;
    }

    public SelectItem[] getItemsCheckboxEvent() {
        List<EventMaster> values = ejbFacade.getAllRecords();
        SelectItem[] items = new SelectItem[values.size()];
        int i = 0;
        for (Object x : values) {
            items[i] = new SelectItem(x, x.toString());
            System.out.println(items[i].getLabel());
            i++;
        }
        return items;
    }

    public List<Action> getradioValue(String type) {
        radioValues = actionFacade.getRecordByuuidNType(type);
        return radioValues;
    }

    public EventMaster getItem() {
        item = ejbFacade.getRecordByuuid(current.getEvent_uuid());
        return item;
    }

    public void setItem(EventMaster item) {
        this.item = item;
    }

    public EventMaster getCurrent() {
        return current;
    }

    public void setCurrent(EventMaster current) {
        this.current = current;
    }

    public List<EventRegData> getFieldsDetails() {
        eventFieldDetails();
        return fieldsDetails;
    }

    public void setFieldsDetails(List<EventRegData> fieldsDetails) {
        this.fieldsDetails = fieldsDetails;
    }

    public List<Action> getDdlValues() {
        return ddlValues;
    }

    public void setDdlValues(List<Action> ddlValues) {
        this.ddlValues = ddlValues;
    }

    public List<Action> getRadioValues() {
        return radioValues;
    }

    public void setRadioValues(List<Action> radioValues) {
        this.radioValues = radioValues;
    }

    public LanguageBean getLanguage() {
        return language;
    }

    public void setLanguage(LanguageBean language) {
        this.language = language;
    }

    @FacesConverter("configuredRegistrationActionConverter")
    public static class ActionConverter implements Converter {

        @Override
        public Action getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.trim().length() == 0) {
                return null;
            }
            ActionController controller = (ActionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "actionController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = new java.lang.Integer(value.trim());
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Action) {
                Action o = (Action) object;
                return getStringKey(Integer.toString(o.getId()));
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Action.class.getName());
            }
        }
    }

    @FacesConverter("configuredRegistrationEventConverter")
    public static class EventConverter implements Converter {

        @Override
        public EventMaster getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.trim().length() == 0) {
                return null;
            }
            ConfigurableEventRegistrationController controller = (ConfigurableEventRegistrationController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "configurableEventRegistrationController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.math.BigDecimal getKey(String value) {
            java.math.BigDecimal key;
            key = new java.math.BigDecimal(value.trim());
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof EventMaster) {
                EventMaster o = (EventMaster) object;
                return getStringKey(o.getEventId().toString());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + EventMaster.class.getName());
            }
        }
    }

    public RegisterationMasterValues getRegValues() {
        return regValues;
    }

    public void setRegValues(RegisterationMasterValues regValues) {
        this.regValues = regValues;
    }

    public void saveValues() throws SystemException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String uuids = current.getEvent_uuid();
        if (showOnreg) return;
        if (!validateConfiguredRegistration()) return;
        String visitedHealthcare = "";
        Integer selectedLimitDynamicId = null;
        {
            registrationMaster.setEvent_Id(item);
            EntityManager em = masterFacade.getEntityManager();
            int count = 0;
            try {
                masterFacade.getUt().begin();
                registrationMaster.setRegisterationId(registerationMasterFacade.getNextRegistrationId());
                em.persist(registrationMaster);

                for (EventRegData data : fieldsDetails) {
                    if (!isFieldVisible(data) || "display".equalsIgnoreCase(data.getFieldType())) continue;
                    regValues = new RegisterationMasterValues();
                    System.out.println("Value: " + data.getFieldValue());

                    if ("radio".equalsIgnoreCase(data.getFieldType()) && "YesNo".equalsIgnoreCase(data.getActionType())) {
                        visitedHealthcare = data.getFieldValue();
                    }

                    regValues.setReg_Id(registrationMaster);
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    regValues.setReg_date(timestamp);
                    Date d = new Date();
                    regValues.setYearEvent(d.getYear() + 1900);
                    regValues.setFieldId(data);

                    // ================= EMAIL =================
                    if (StringUtils.containsIgnoreCase(data.getFieldName(), "Email")) {
                        email = data.getFieldValue();
                        RegisterationMasterValues email_exist = registerationMasterValuesFacade.findemailByFieldNEvent(data, email);
                        count++;
                        if (email_exist != null) {
                            masterFacade.getUt().rollback();
                            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", JsfUtil.getMsgFromBundle("Duplicate.Email")));
                            return;
                        }
                    }

                    // ================= LICENSE =================
                    if (StringUtils.containsIgnoreCase(data.getFieldName(), "licence") || StringUtils.containsIgnoreCase(data.getFieldName(), "license")) {
                        license = data.getFieldValue();
                    }

                    // ================= DAY 1 =================
                    if (StringUtils.containsIgnoreCase(data.getFieldName(), "Day 1")) {
                        if (data.getFielddynamicValue() != null) {
                            attaendanceDateValue = language.isLangEn() ? data.getFielddynamicValue().getAction_values() : data.getFielddynamicValue().getAction_value_ar();
                        }
                    }

                    // ================= ATTENDANCE =================
                    if (StringUtils.containsIgnoreCase(data.getFieldName(), "Attendance")) {
                        if ("radio".equalsIgnoreCase(data.getFieldType())) {
                            attaendanceDate = data.getFieldValue();
                            if (data.getFieldValue() != null && !data.getFieldValue().isEmpty()) {
                                attaendanceDateValue = language.isLangEn() ? actionFacade.getRecordById(Integer.parseInt(data.getFieldValue())).getAction_values() : actionFacade.getRecordById(Integer.parseInt(data.getFieldValue())).getAction_value_ar();
                            }
                        }
                    }

                    // ================= MOBILE =================
                    if (StringUtils.containsIgnoreCase(data.getFieldName(), "Mobile") || StringUtils.containsIgnoreCase(data.getFieldName(), "Phone") || StringUtils.containsIgnoreCase(data.getFieldName(), "contact")) {
                        mobile = data.getFieldValue();
                        RegisterationMasterValues mobile_exist = registerationMasterValuesFacade.findemailByFieldNEvent(data, data.getFieldValue());
                        if (mobile_exist != null) {
                            masterFacade.getUt().rollback();
                            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", JsfUtil.getMsgFromBundle("Duplicate.Mobile")));
                            return;
                        }
                    }

                    // ================= FULL NAME =================
                    String normalizedField = data.getFieldName().replaceAll("\\s+", " ").trim();
                    List<String> allowedNames = Arrays.asList("Name (First & Last)", "FirstName", "First Name", "Full Name");
                    if (allowedNames.stream().anyMatch(n -> StringUtils.equalsIgnoreCase(normalizedField, n))) {
                        fullName = data.getFieldValue();
                    }
                    if (StringUtils.containsIgnoreCase(data.getFieldName(), "LastName") || StringUtils.containsIgnoreCase(data.getFieldName(), "Last Name")) {
                        fullName = fullName + " " + data.getFieldValue();
                    }

                    // ================= ORGANIZATION =================
                    if (StringUtils.containsIgnoreCase(data.getFieldName(), "Organization")) {
                        organization = data.getFielddynamicValue() == null ? data.getFieldValue() : data.getFielddynamicValue().getAction_values();
                    }

                    // ================= EVENT DATE =================
                    if (data.getFieldName().equalsIgnoreCase("Select Event date")) {
                        if (data.getFielddynamicValue() != null) {
                            eventDate = data.getFielddynamicValue().getAction_values();
                        }
                    }

                    // ================= FIELD VALUE HANDLING =================
                    String fieldType = data.getFieldType();
                    if (fieldType != null && isDropdownType(fieldType)) {
                        regValues.setIsaction(true);
                        Action dynamicValue = data.getFielddynamicValue();
                        if (dynamicValue != null && dynamicValue.getId() != null) {
                            regValues.setFieldValues(dynamicValue.getId().toString());
                            Action action = actionFacade.getLimitedActionByEventData(data);
                            if (action != null && action.getMax_limit() > 0) {
                                selectedLimitDynamicId = dynamicValue.getId();
                            }
                        } else {
                            regValues.setFieldValues("");
                        }
                    } else if ("calendar".equalsIgnoreCase(fieldType)) {
                        regValues.setIsaction(false);
                        regValues.setFieldValues(data.getFieldDateValue() != null ? data.getFieldDateValue().toString() : "");
                    } else {
                        regValues.setIsaction("radio".equalsIgnoreCase(fieldType));
                        regValues.setFieldValues(data.getFieldValue() != null ? data.getFieldValue() : "");
                    }

                    System.out.println("Saving Field: " + data.getFieldName() + " | Type: " + fieldType + " | Value: " + regValues.getFieldValues());
                    em.persist(regValues);

                    if (selectedLimitDynamicId != null) {
                        if (!actionFacade.incrementCurrentApplicants(selectedLimitDynamicId)) {
                            String message = MessageFormat.format(JsfUtil.getMsgFromBundle("registration.full"), data.getFielddynamicValue().getAction_values());
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "The selected option has reached the maximum allowed registrations. Please choose another option."));
                            masterFacade.getUt().rollback();
                            return;
                        }
                    }
                    selectedLimitDynamicId = null;
                }

                // ================= SURVEY =================
                if (Boolean.TRUE.equals(item.getIsSurvey())) {
                    Applicants currentApplicant = new Applicants();
                    currentApplicant.setRegId(registrationMaster);
                    currentApplicant.setEventUUId(item.getEvent_uuid());
                    currentApplicant.setEmail(email);
                    currentApplicant.setFullName(fullName);
                    em.persist(currentApplicant);
                    em.flush();
                    masterFacade.getUt().commit();
                    String surveyLink = JsfUtil.getBaseUrl() + "/faces/survey.xhtml?eventUUId=" + item.getEvent_uuid() + "$$" + currentApplicant.getID() + "&visited=" + visitedHealthcare;
                    FacesContext.getCurrentInstance().getExternalContext().redirect(surveyLink);
                    return;
                }

                // ================= FILE UPLOAD =================
                if (fileUpload != null) {
                    System.out.println("Uploaded File Name Is :: " + fileUpload.getFileName() + " :: Uploaded File Size :: " + fileUpload.getSize());
                    sendEmailWithAttachment(fileUpload, fullName, email);
                }
                fileUpload = null;

                // ================= COMMIT =================
                masterFacade.getUt().commit();
                showOnLoad = false;
                showOnreg = true;
                if (count > 0) {
                    sendEmail();
                    registrationEmailSent = true;
                }
                System.out.print("Entered method");
                showOnLoad = false;
                showOnreg = true;
                if (count == 0) {
                    emailNotPresnt = true;
                }

            } catch (Exception e) {
                if (showOnreg) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_WARN, "Registration saved",
                        "Email delivery failed. Please contact the event organizer for joining instructions."));
                    return;
                }
                e.printStackTrace();
                try {
                    masterFacade.getUt().rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", JsfUtil.getMsgFromBundle("Mail.Notsent")));
                return;
            }
        }
    }

   public void handleFileUpload(FileUploadEvent event) throws IOException {
    this.fileUpload = event.getFile();

    // Max file size (2MB)
    long MAX_SIZE = 2 * 1024 * 1024;

    // Validate file size
    if (fileUpload.getSize() > MAX_SIZE) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "File is too large", "Maximum file size is 2 MB."));
        return;  // Stop further processing if the file size exceeds the limit
    }

    java.util.Date date = new java.util.Date();

    // Generate unique folder name if not provided
    if (StringUtils.isBlank(cvFolder)) {
        UUID uniqueKey = UUID.randomUUID();
        cvFolder = StringUtils.substring(uniqueKey.toString(), 0, 6);
    }

    String strFullPath = ("/cibDoc/" + cvFolder);
    Path path = Paths.get(strFullPath);

    // Create directories if they do not exist
    if (!Files.exists(path)) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace(); // Handle directory creation failure
            return;
        }
    }

    if (fileUpload != null) {
        byte[] bytes = fileUpload.getContent();
        String filename = FilenameUtils.getName(fileUpload.getFileName());
        String filePath = strFullPath + "/" + filename;

        // Check the file signature for content-based validation
        if (isValidFile(bytes)) {
            // Proceed with saving the file if it's a valid type
            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)))) {
                stream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace(); // Handle file writing error
            }

            // Set the fileName for the UI to display only when the file is valid
            this.fileUpload = event.getFile(); // Only assign if the file is valid
        } else {  fileUpload = null;
            // Invalid file type, notify user
            FacesContext.getCurrentInstance().addMessage("repeatId:9:uploadFile_label",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid file type", 
                "Please upload a valid PDF, JPG, JPEG, or PNG file."));
        }
    }
}

// Method to validate the file's magic bytes (signature)
private boolean isValidFile(byte[] bytes) {
    if (bytes == null || bytes.length < 4) {
        return false;
    }

    // Check for PDF (starts with %PDF)
    if (bytes[0] == 0x25 && bytes[1] == 0x50 && bytes[2] == 0x44 && bytes[3] == 0x46) {
        return true;  // It's a PDF
    }

    // Check for JPEG (starts with FF D8 FF)
    if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8 && bytes[2] == (byte) 0xFF) {
        return true;  // It's a JPEG
    }

    // Check for PNG (starts with 89 50 4E 47)
    if (bytes[0] == (byte) 0x89 && bytes[1] == (byte) 0x50 && bytes[2] == (byte) 0x4E && bytes[3] == (byte) 0x47) {
        return true;  // It's a PNG
    }

    // If it doesn't match any of the known signatures
    return false;
}
    public boolean isShowOnLoad() {
        daterange = new RegistrationLimit();
        if (current.getEvent_uuid() != null) {
            daterange = registrationLimitFacade.getRecordByuuidNType(current.getEvent_uuid());
        } else {
            showOnLoad = false;
            invalidLink = true;
        }

        Date today = new Date();
        if (daterange != null) {
            if (daterange.getStartDate() != null || daterange.getEndDate() != null) {
                if (today.after(daterange.getEndDate())) {
                    showOnLoad = false;
                    regClosed = true;
                }
            }
            if (daterange.getLimit() != null) {
                EventMaster event = ejbFacade.getRecordByuuid(current.getEvent_uuid());
                List<RegisterationMaster> list = registerationMasterFacade.getRecordByEventId(event);
                if (list.size() > daterange.getLimit()) {
                    showOnLoad = false;
                    regClosed = true;
                }
                
                Integer webinarLimit
                        = event.getWebinar() != null && event.getWebinar().matches("\\d+")
                        ? Integer.valueOf(event.getWebinar())
                        : null;

                if (webinarLimit != null && list.size() > webinarLimit) {

                    event.setEventDate(
                            EventDateUtil.shiftEventDate(event.getEventDate())
                    );

                    daterange.setEventstartDate(
                            EventDateUtil.shiftEn(daterange.getEventstartDate())
                    );

                    daterange.setEventStartDayAr(
                            EventDateUtil.shiftAr(daterange.getEventStartDayAr())
                    );

                    ejbFacade.edit(event);
                    registrationLimitFacade.edit(daterange);
                }
            }
            
            
        }
        return showOnLoad;
    }

    public void changeField(EventRegData regData) {
        parentValue = regData.getFielddynamicValue().getId();

    }
    
    public void changeFieldRadio(EventRegData field) {
        parentValue = Integer.parseInt(field.getFieldValue());
//        FacesContext context = FacesContext.getCurrentInstance();
//        UIViewRoot viewRoot = context.getViewRoot();
//        List<String> updateIds = new ArrayList<>();
//        for (int i = 0; i < fieldsDetails.size(); i++) {
//            String clientId = "repeatId:" + i + ":childField";
//            UIComponent component = viewRoot.findComponent(clientId);
//            if (component != null) {
//                updateIds.add(clientId);
//            }
//        }
//        PrimeFaces.current().ajax().update(updateIds);
    }

    public void setShowOnLoad(boolean showOnLoad) {
        this.showOnLoad = showOnLoad;
    }

    public boolean isShowOnreg() {
        return showOnreg;
    }

    public void setShowOnreg(boolean showOnreg) {
        this.showOnreg = showOnreg;
    }

    public EventMaster getEventid() {
        System.out.println(eventid);
        return eventid;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventid(EventMaster eventid) {
        this.eventid = eventid;
    }

    public List<Map<String, String>> getApplicantDetails() {
        return applicantDetails;
    }

    public void setApplicantDetails(List<Map<String, String>> applicantDetails) {
        this.applicantDetails = applicantDetails;
    }

    public Map<String, String> getRegdetails() {
        return regdetails;
    }

    public void setRegdetails(Map<String, String> regdetails) {
        this.regdetails = regdetails;
    }

    public Integer getParentValue() {
        return parentValue;
    }

    public void setParentValue(Integer parentValue) {
        this.parentValue = parentValue;
    }

    public String openRegisterApplicantPage() {
        eventid = new EventMaster();
        applicantDetails = new ArrayList<>();
        regdetails = new LinkedHashMap<>();
        return "/jsf/applicantDetails.xhtml";
    }

    public String changeLocale(String lang) {
        try {
            language.changeLocale(lang);
            if (current.getEvent_uuid() == null) {
                invalidLink = true;
                showOnLoad = false;
                return "/faces/registeration.xhtml?faces-redirect=true";
            }
            return "/faces/registeration.xhtml?faces-redirect=true&uuid=" + current.getEvent_uuid() + "&lang=" + lang;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isRegClosed() {
        return regClosed;
    }

    public void setRegClosed(boolean regClosed) {
        this.regClosed = regClosed;
    }

    public void checkValue(EventRegData field) {
        if (field.getFielddynamicValue().getId() == 232) {
            FacesContext context = FacesContext.getCurrentInstance();
            UIViewRoot root = context.getViewRoot();
            UIComponent tile = JsfUtil.findComponent("rfFields");

            Iterator<UIComponent> it = tile.getChildren().iterator();

            while (it.hasNext()) {
                UIComponent comp = it.next();
                if (comp != null) {
                    Iterator<UIComponent> itComp = comp.getChildren().iterator();
                    while (itComp.hasNext()) {
                        UIComponent compChild = itComp.next();
                        if (compChild.getAttributes().containsKey("styleClass")) {
                            System.out.println(comp.getAttributes().get("styleClass"));
                            if (comp.getAttributes().get("styleClass").equals("childField")) {
                                PrimeFaces.current().ajax().update(comp.getClientId());
                                break;
                            }
                        }
                    }

                }
                if (comp.getAttributes().containsKey("styleClass")) {
                    System.out.println(comp.getAttributes().get("styleClass"));
                    if (comp.getAttributes().get("styleClass").equals("childField")) {
                        PrimeFaces.current().ajax().update(comp.getClientId());
                        break;
                    }
                }
            }
        }
    }

    private void sendEmailWithAttachment(UploadedFile uploadedFile, String fullName, String email1) throws Exception {
        // Create Session
        Session session = Session.getDefaultInstance(mailProperties());

        // Prepare message with attachment
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(item.getEmail()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("eventresearch@moph.gov.qa"));
        message.setSubject("New Document Uploaded");

        BodyPart messageBodyPart = new MimeBodyPart();
        String messageText = String.format(
                "A new Document has been uploaded by %s (%s). Please find attached.",
                fullName, email1
        );
        messageBodyPart.setText(messageText);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        messageBodyPart = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(uploadedFile.getInputStream(), uploadedFile.getContentType());
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(uploadedFile.getFileName());
        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);

        // Send message
        Transport.send(message);
    }

    private void sendEmail() throws Exception {
        String emailTemplate = language.isLangEn() ? item.getRegister_email() : item.getRegisteremailAr();
        if (emailTemplate == null || emailTemplate.trim().isEmpty()) {
            throw new IllegalStateException("Configure a registration email template for this language.");
        }
        String joinUrl = getRegistrationPage().get("teamsJoinUrl");
        java.net.URI teamsUri = joinUrl == null ? null : java.net.URI.create(joinUrl);
        if (teamsUri == null || !"https".equalsIgnoreCase(teamsUri.getScheme())
                || teamsUri.getHost() == null) {
            throw new IllegalStateException("Configure a valid HTTPS Teams joining URL.");
        }
        String qrUrl = generateUrlForApplicant(registrationMaster.getRegisterationId());
        String qrBase64 = JsfUtil.generateQRCodeBase64(qrUrl);
        byte[] qrBytes = Base64.getDecoder().decode(qrBase64);
        // Create a Session object to represent a mail session with the specified properties. 
        Session session = Session.getInstance(mailProperties());
        // Send the message.
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setRecipient(Message.RecipientType.BCC, new InternetAddress(item.getEmail()));

            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            msg.setFrom(new InternetAddress(item.getEmail()));
            msg.setSubject(MimeUtility.encodeText(item.getRegisterEmailSubject(), "UTF-8", "B"));
            cfg.setDefaultEncoding("UTF-8");
            Map<String, Object> map = new HashMap<>();
            map.put("qrCodeImage", qrBase64);
            map.put("fullName", fullName);
            map.put("applicantId", registrationMaster.getRegisterationId());
            map.put("EventSalutation", item.getEmailSalutation());
            map.put("webinar", item.getWebinar());
            map.put("instructor", item.getInstructor());
            map.put("organization", organization);
            map.put("email", email);
            map.put("mobileNumber", mobile);
            map.put("license", license);
            map.put("attendanceDate", attaendanceDate);
            map.put("attaendanceDateValue", attaendanceDateValue);
            map.put("EventEmail", item.getEmail());
            map.put("registerEmail", email);
            map.put("registrationPage", getRegistrationPage());
            Writer out = new StringWriter();

            if (language.isLangEn()) {
                if (item.getRegister_email() != null) {
                    Template template = cfg.getTemplate(item.getRegister_email());  // English template
                    map.put("EventName", item.getEventTitle());
                    map.put("startDayRange",daterange.getEventstartDate());

                    if (eventDate != null && !eventDate.isEmpty()) {
                        map.put("startDay", eventDate);
                    } else if (daterange.getEventstartDate() != null && daterange.getEventEndDate() != null
                            && !daterange.getEventstartDate().isEmpty() && !daterange.getEventEndDate().isEmpty()) {
                        map.put("startDay", daterange.getEventstartDate() + "-" + daterange.getEventEndDate());
                    } else if (daterange.getEventstartDate() != null && !daterange.getEventstartDate().isEmpty()) {
                        map.put("startDay", daterange.getEventstartDate());
                    }

                    if (item.getRegister_email().equalsIgnoreCase("presurveyTest.ftl")) {
                        String testLink = "<a href=" + "https://eventsreg.moph.gov.qa/NCGP/faces/test.xhtml?eventUUId="
                                + item.getEvent_uuid() + "$$" + registrationMaster.getRegisterationId() + ">Click to attend pre-course test</a>";
                        map.put("testLink", testLink);
                    } else {
                        if (daterange.getEventDay1() != null && !daterange.getEventDay1().isEmpty()) {
                            map.put("day1", "Day 1: <a href =" + daterange.getEventDay1() + ">Click here to join the meeting</a>");
                        }
                        if (daterange.getEventDay2() != null && !daterange.getEventDay2().isEmpty()) {
                            map.put("day2", "Day 2: <a href =" + daterange.getEventDay2() + ">Click here to join the meeting</a>");
                        }
                        if (daterange.getEventDay3() != null && !daterange.getEventDay3().isEmpty()) {
                            map.put("day3", "Day 3: <a href =" + daterange.getEventDay3() + ">Click here to join the meeting</a>");
                        }
                    }

                    template.process(map, out);
                }
            } else {
                if (item.getRegisteremailAr() != null) {
                    Template templateAr = cfg.getTemplate(item.getRegisteremailAr());  // Arabic template
                    map.put("EventName", item.getEventTitleAr());
                    map.put("startDayRange",daterange.getEventStartDayAr());

                    if (daterange.getEventDay1() != null && !daterange.getEventDay1().isEmpty()) {
                        map.put("day1", "Ø§Ù„ÙŠÙˆÙ… Ø§Ù„Ø£ÙˆÙ„: <a href =" + daterange.getEventDay1() + ">Ø§Ø¶ØºØ· Ù‡Ù†Ø§ Ù„Ø­Ø¶ÙˆØ± Ø§Ù„ÙˆØ±Ø´Ø©</a>");
                    }
                    if (daterange.getEventDay2() != null && !daterange.getEventDay2().isEmpty()) {
                        map.put("day2", "Ø§Ù„ÙŠÙˆÙ… Ø§Ù„Ø«Ø§Ù†ÙŠ: <a href =" + daterange.getEventDay2() + ">Ø§Ø¶ØºØ· Ù‡Ù†Ø§ Ù„Ø­Ø¶ÙˆØ± Ø§Ù„ÙˆØ±Ø´Ø©</a>");
                    }
                    if (daterange.getEventDay3() != null && !daterange.getEventDay3().isEmpty()) {
                        map.put("day3", "Ø§Ù„ÙŠÙˆÙ… Ø§Ù„Ø«Ø§Ù„Ø«: <a href =" + daterange.getEventDay3() + ">Ø§Ø¶ØºØ· Ù‡Ù†Ø§ Ù„Ø­Ø¶ÙˆØ± Ø§Ù„ÙˆØ±Ø´Ø©</a>");
                    }

                    templateAr.process(map, out);
                }
            }
            // creates message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(out.toString(), "text/html; charset=UTF-8");

            FacesContext context = FacesContext.getCurrentInstance();

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.setHeader("Content-ID", "<qrCodeImage>");
            imagePart.setDisposition(MimeBodyPart.INLINE);
            imagePart.setFileName("qrcode.png");
            imagePart.setDataHandler(new DataHandler(new ByteArrayDataSource(qrBytes, "image/png")));
            multipart.addBodyPart(imagePart);
            addAgendaAttachment(multipart, daterange.getEvent_uuid().getEmailAttachEn());
            addAgendaAttachment(multipart, daterange.getEvent_uuid().getEmailAttachAr());

            msg.setContent(multipart);

            // Create a transport.        
            Transport transport = session.getTransport();
            System.out.println("sending email to " + email);

            //Connect to the smtp host
            transport.connect();
            Transport.send(msg);

            System.out.println("Email sent!");
            System.out.println("message sent successfully....");

            //current = new Applicants();
        } catch (MessagingException | TemplateException | IOException me) {
            System.out.println(me.getMessage());

            throw me;
        }
    }

    private Properties mailProperties() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "10.25.255.222");
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.auth", "false");
        return properties;
    }

    private void addAgendaAttachment(Multipart multipart, String actualFileName) throws IOException, MessagingException {
        if (StringUtils.isBlank(actualFileName)) {
            return;
        }
        String resourcePath = "docs/agenda/pdf/" + actualFileName;
        InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (resource == null) {
            throw new FileNotFoundException("Attachment not found: " + resourcePath);
        }
        MimeBodyPart attachment = new MimeBodyPart();
        attachment.setDataHandler(new DataHandler(new ByteArrayDataSource(resource, "application/pdf")));
        attachment.setFileName(actualFileName);
        multipart.addBodyPart(attachment);
    }
    
   public void downloadExcel() {
    try {
        eventApplicantDetails();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Applicants");

        if (!applicantDetails.isEmpty()) {
            // Step 1: Collect all unique keys from all maps to define headers
            Set<String> allKeys = new LinkedHashSet<>();
            for (Map<String, String> map : applicantDetails) {
                allKeys.addAll(map.keySet());
            }

            // Step 2: Create header style (bold + brown)
            CellStyle headerStyle = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.BROWN.getIndex());
            headerStyle.setFont(font);

            // Step 3: Create header row
            Row header = sheet.createRow(0);
            int col = 0;
            for (String key : allKeys) {
                Cell cell = header.createCell(col++);
                cell.setCellValue(key);
                cell.setCellStyle(headerStyle);
            }

            // Step 4: Create data rows
            int rowNum = 1;
            for (Map<String, String> dataRow : applicantDetails) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                for (String key : allKeys) {
                    String value = dataRow.getOrDefault(key, ""); // Use empty string if key is missing
                    row.createCell(colNum++).setCellValue(value);
                }
            }

            // Optional: Auto-size columns
            for (int i = 0; i < allKeys.size(); i++) {
                sheet.autoSizeColumn(i);
            }
        }

        // Response writing
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

        response.reset();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"Applicants.xlsx\"");

        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.flush();
        outputStream.close();

        facesContext.responseComplete();
    } catch (Exception e) {
        e.printStackTrace();
    }
}



    
    public void removeApplicant(BigDecimal regId) {
    try {
        registerationMasterFacade.deleteByRegId(regId);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Registration and related values deleted successfully"));
    } catch (Exception e) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to delete registration"));
    }
}
    public void markAsAttended() {
        try {
            if (currentRegistration != null) {
                currentRegistration.setAttended(Boolean.TRUE);
                registerationMasterFacade.edit(currentRegistration);
                JsfUtil.addSuccessMessage("Marked as attended successfully.");
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Error marking attendance: " + e.getMessage());
        }
    }

   public void markAsUnattended() {
    try {
        if (currentRegistration != null) {
            currentRegistration.setAttended(Boolean.FALSE);
            registerationMasterFacade.edit(currentRegistration);
            JsfUtil.addSuccessMessage("Marked as unattended successfully.");
        }
    } catch (Exception e) {
        JsfUtil.addErrorMessage("Error updating attendance: " + e.getMessage());
    }
}

    public boolean isInvalidLink() {
        return invalidLink;
    }

    public void setInvalidLink(boolean invalidLink) {
        this.invalidLink = invalidLink;
    }

    /* open create event page **/
    public String opencreateSurveyFieldCreatePage() {
        return "/jsf/createSurveyField.xhtml?faces-redirect=true";
    }

    public boolean checkError(String value) {
        return StringUtils.isNotBlank(value);
    }

    public Boolean getEmailNotPresnt() {
        return emailNotPresnt;
    }

    public void setEmailNotPresnt(Boolean emailNotPresnt) {
        this.emailNotPresnt = emailNotPresnt;
    }

    public RegistrationLimit getDaterange() {
        return daterange;
    }

    public void setDaterange(RegistrationLimit daterange) {
        this.daterange = daterange;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getEventUuid() {
        return eventUuid;
    }

    public void setEventUuid(String eventUuid) {
        this.eventUuid = eventUuid;
    }

    public String getAdUser() {
        return adUser;
    }

    public void setAdUser(String adUser) {
        this.adUser = adUser;
    }

    public Map<String, String> getApplicantDetailsSingle() {
        return applicantDetailsSingle;
    }

    public void setApplicantDetailsSingle(Map<String, String> applicantDetailsSingle) {
        this.applicantDetailsSingle = applicantDetailsSingle;
    }

    public RegisterationMaster getCurrentRegistration() {
        return currentRegistration;
    }

    public void setCurrentRegistration(RegisterationMaster currentRegistration) {
        this.currentRegistration = currentRegistration;
    }
    
    public void sendReminderEmail() {
    if (eventid == null) {
        JsfUtil.addErrorMessage("No event selected.");
        return;
    }

    try {
        // Stop if event is already over
        if (new Date().after(eventid.getEventDate())) {
            JsfUtil.addErrorMessage("Event expired — no reminder");
            return;
        }

       
        List<RegisterationMaster> regList =
                registerationMasterFacade.getRecordByEventId(eventid);

       
        String date = "23/11/2025";
        String time = "8:00 am to 1:00 pm";

        cfg.setDefaultEncoding("UTF-8");
 
        Template template = cfg.getTemplate("Reminder.ftl", "UTF-8");

        for (RegisterationMaster reg : regList) {

            List<RegisterationMasterValues> details =
                    registerationMasterValuesFacade.getrecordByRegId(reg);

            String email = null;
            String fullName = null;

            // Extract applicant name + email
            if (details != null) {
                for (RegisterationMasterValues v : details) {
                    if (v == null || v.getFieldId() == null) continue;

                    String field = v.getFieldId().getFieldName().toLowerCase();

                    if (field.contains("email")) {
                        email = v.getFieldValues();
                    } else if (field.contains("full") && field.contains("name")) {
                        fullName = v.getFieldValues();
                    } else if (fullName == null && field.contains("name")) {
                        fullName = v.getFieldValues();
                    }
                }
            }

            if (email == null || email.trim().isEmpty()) {
                System.out.println("Skipping: no email for Reg ID " + reg.getRegisterationId());
                continue;
            }

            if (fullName == null) {
                fullName = "المشارك/ة";
            }

            // Build data model for FTL
            Map<String, Object> map = new HashMap<>();
            map.put("fullName", fullName);
            map.put("email", email);
            map.put("date", date);
            map.put("time", time);
            map.put("EventName", eventid.getEventTitle());

            // Process FTL → HTML
            StringWriter out = new StringWriter();
            template.process(map, out);
            String htmlBody = out.toString();

            // Send as HTML email
            JsfUtil.sendHTMLEmail(email, 
                    "تذكير بموعد الورشة: " + eventid.getEventTitle(),
                    htmlBody);

            System.out.println("Reminder sent to: " + email);
        }

        JsfUtil.addSuccessMessage("Reminder emails sent successfully.");

    } catch (Exception e) {
        e.printStackTrace();
        JsfUtil.addErrorMessage("Error sending reminder: " + e.getMessage());
    }
}




    
    public void initLocale() {
    Map<String, String> params = FacesContext.getCurrentInstance()
                              .getExternalContext().getRequestParameterMap();
    String lang = params.get("lang");

    if (lang != null) {
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(lang));

        if ("en".equals(lang)) {
            language.setLocale(Locale.ENGLISH);
        } else if ("ar".equals(lang)) {
            language.setLocale(new Locale("ar"));
        }
    }
}

    public String getRadioValue() {
        return radioValue;
    }

    public void setRadioValue(String radioValue) {
        this.radioValue = radioValue;
    }
    
     private boolean isDropdownType(String fieldType) {
    return "dropdown".equalsIgnoreCase(fieldType)
        || "listbox".equalsIgnoreCase(fieldType)
        || "dropdownOther".equalsIgnoreCase(fieldType);
}
     

private void addCalendarInvitation(Multipart multipart,
                                   Date startDateTime,
                                   Date endDateTime,
                                   String eventTitle,
                                   String teamsLink) throws MessagingException {

    if (startDateTime == null || endDateTime == null) {
        return;
    }

    SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    String dtStart = utcFormat.format(startDateTime);
    String dtEnd = utcFormat.format(endDateTime);
    String dtStamp = utcFormat.format(new Date());
    String uid = UUID.randomUUID() + "@moph.gov.qa";
    String link = (teamsLink != null) ? teamsLink.trim() : "";

    StringBuilder ics = new StringBuilder();
    ics.append("BEGIN:VCALENDAR\r\n")
       .append("VERSION:2.0\r\n")
       .append("PRODID:-//Ministry of Public Health Qatar//Event Registration//EN\r\n")
       .append("CALSCALE:GREGORIAN\r\n")
       .append("METHOD:PUBLISH\r\n")
       .append("BEGIN:VEVENT\r\n")
       .append("UID:").append(uid).append("\r\n")
       .append("DTSTAMP:").append(dtStamp).append("\r\n")
       .append("DTSTART:").append(dtStart).append("\r\n")
       .append("DTEND:").append(dtEnd).append("\r\n")
       .append("SUMMARY:").append(escapeIcsText(eventTitle)).append("\r\n")
       .append("DESCRIPTION:").append(escapeIcsText(
               "National Mental Health Educational Rounds\n"
               + "Microsoft Teams\n"
               + "Join Link: " + link
       )).append("\r\n");

    if (!link.isEmpty()) {
        ics.append("URL:").append(link).append("\r\n");
    }

    // 10-minute reminder alarm
    ics.append("BEGIN:VALARM\r\n")
       .append("TRIGGER:-PT10M\r\n")
       .append("ACTION:DISPLAY\r\n")
       .append("DESCRIPTION:Event starts in 10 minutes\r\n")
       .append("END:VALARM\r\n")
       .append("END:VEVENT\r\n")
       .append("END:VCALENDAR\r\n");

    MimeBodyPart calendarPart = new MimeBodyPart();
    calendarPart.setContent(
        ics.toString(), 
        "text/calendar; charset=UTF-8; method=PUBLISH"
    );
    calendarPart.setFileName("event-calendar.ics");
    calendarPart.setDisposition(Part.ATTACHMENT);

    multipart.addBodyPart(calendarPart);
}

/**
 * Escapes characters that are special in iCalendar (RFC 5545) text fields.
 */
private static String escapeIcsText(String input) {
    if (input == null) return "";
    return input.replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\r\n", "\\n")
                .replace("\n", "\\n")
                .replace("\r", "\\n");
}

    // Additional methods for the configurable registration page.
    private Map<String, String> registrationPage;
    private boolean registrationEmailSent;

    public boolean isRegistrationEmailSent() { return registrationEmailSent; }

    public Map<String, String> getRegistrationPage() {
        if (registrationPage == null) {
            registrationPage = new HashMap<>();
            String uuid = current.getEvent_uuid();
            if (uuid == null || !uuid.matches("[A-Za-z0-9_-]+")) {
                throw new IllegalStateException("A valid event UUID is required.");
            }
            String resource = "/event-registration/" + uuid + ".properties";
            try (InputStream input = getClass().getResourceAsStream(resource)) {
                if (input == null) throw new IllegalStateException("Missing " + resource);
                Properties properties = new Properties();
                properties.load(new java.io.InputStreamReader(input, java.nio.charset.StandardCharsets.UTF_8));
                for (String key : properties.stringPropertyNames()) {
                    registrationPage.put(key, properties.getProperty(key));
                }
            } catch (IOException e) {
                throw new IllegalStateException("Cannot load registration configuration", e);
            }
        }
        return registrationPage;
    }

    public String getFieldInstruction(EventRegData field) {
        String key = "field." + field.getId().toPlainString() + ".instruction";
        return getRegistrationPage().getOrDefault(key, "");
    }

    public void loadConfiguredRegistration() {
        if (FacesContext.getCurrentInstance().isPostback()) return;
        getItem();
        eventFieldDetails();
        fieldsDetails.sort(Comparator.comparing(EventRegData::getPositions,
                Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(EventRegData::getId));
        getRegistrationPage();
    }

    public List<Action> getConfiguredActions(EventRegData field) {
        List<Action> result = new ArrayList<>();
        if (field.getActionType() == null) return result;
        for (Action action : actionFacade.getRecordByType(field.getActionType())) {
            if (action.getEvent_uuid() != null && current.getEvent_uuid().equals(
                    action.getEvent_uuid().getEvent_uuid())) {
                result.add(action);
            }
        }
        result.sort(Comparator.comparing(Action::getId));
        return result;
    }

    public boolean isFieldVisible(EventRegData field) {
        return isFieldVisible(field, new HashSet<BigDecimal>());
    }

    private boolean isFieldVisible(EventRegData field, Set<BigDecimal> seen) {
        if (field == null || field.getId() == null || !seen.add(field.getId())) return false;
        if (field.getParentField() == null) return true;
        EventRegData parent = null;
        for (EventRegData candidate : fieldsDetails) {
            if (candidate.getId().compareTo(field.getParentField().getId()) == 0) {
                parent = candidate;
                break;
            }
        }
        Action trigger = field.getShowWhenAction();
        if (parent == null || trigger == null || trigger.getId() == null
                || !isFieldVisible(parent, seen)) return false;
        String selected = "radio".equalsIgnoreCase(parent.getFieldType())
            ? parent.getFieldValue()
            : parent.getFielddynamicValue() == null ? null
                : parent.getFielddynamicValue().getId().toString();
        return trigger.getId().toString().equals(selected);
    }

    public void onConfiguredFieldChange() {
        for (EventRegData field : fieldsDetails) {
            if (!isFieldVisible(field)) {
                field.setFieldValue(null);
                field.setFieldDateValue(null);
                field.setFielddynamicValue(null);
            }
        }
    }

    public String getDependentUpdateTargets(EventRegData field) {
        return "@(.dependsOn" + field.getId().toPlainString() + ")";
    }

    public String getDependencyClasses(EventRegData field) {
        StringBuilder classes = new StringBuilder("configuredField");
        Set<BigDecimal> seen = new HashSet<>();
        EventRegData parent = field.getParentField();
        while (parent != null && seen.add(parent.getId())) {
            classes.append(" dependsOn").append(parent.getId().toPlainString());
            EventRegData loaded = null;
            for (EventRegData candidate : fieldsDetails)
                if (candidate.getId().compareTo(parent.getId()) == 0) loaded = candidate;
            parent = loaded == null ? null : loaded.getParentField();
        }
        return classes.toString();
    }

    public void validateConfiguredText(FacesContext context, UIComponent component, Object value) {
        EventRegData field = (EventRegData) component.getAttributes().get("configuredField");
        String text = value == null ? "" : value.toString();
        String error = configuredTextError(field, text);
        if (error != null) throw new javax.faces.validator.ValidatorException(
            new FacesMessage(FacesMessage.SEVERITY_ERROR, error, error));
    }

    private String configuredTextError(EventRegData field, String text) {
        if (field.isMandatory() && text.trim().isEmpty()) return field.getMandatoryMessage();
        if (text.isEmpty()) return null;
        if (field.getFieldLength() > 0 && text.length() > field.getFieldLength())
            return "The value exceeds the configured field length.";
        if (field.getPattern() != null && !field.getPattern().isEmpty()
                && !text.matches(field.getPattern())) return field.getValidationmsg();
        return null;
    }

    public boolean getAcknowledgementValue(EventRegData field) {
        return Boolean.parseBoolean(field.getFieldValue());
    }

    public void setAcknowledgementValue(EventRegData field, boolean value) {
        field.setFieldValue(Boolean.toString(value));
    }

    // Map bridge provides a writable Boolean binding for String-backed fieldValue.
    public Map<String, Boolean> getAcknowledgements() {
        return new java.util.AbstractMap<String, Boolean>() {
            private EventRegData find(Object id) {
                for (EventRegData field : fieldsDetails)
                    if (field.getId().toPlainString().equals(String.valueOf(id))) return field;
                throw new IllegalArgumentException("Unknown acknowledgement field");
            }
            @Override public Boolean get(Object id) { return getAcknowledgementValue(find(id)); }
            @Override public Boolean put(String id, Boolean value) {
                EventRegData field = find(id);
                Boolean previous = getAcknowledgementValue(field);
                setAcknowledgementValue(field, Boolean.TRUE.equals(value));
                return previous;
            }
            @Override public Set<Map.Entry<String, Boolean>> entrySet() {
                Set<Map.Entry<String, Boolean>> entries = new HashSet<>();
                for (EventRegData field : fieldsDetails)
                    entries.add(new java.util.AbstractMap.SimpleEntry<>(
                        field.getId().toPlainString(), getAcknowledgementValue(field)));
                return entries;
            }
        };
    }

    public void validateAcknowledgement(FacesContext context, UIComponent component, Object value) {
        EventRegData field = (EventRegData) component.getAttributes().get("configuredField");
        if (field.isMandatory() && !Boolean.TRUE.equals(value))
            throw new javax.faces.validator.ValidatorException(new FacesMessage(
                FacesMessage.SEVERITY_ERROR, field.getMandatoryMessage(), field.getMandatoryMessage()));
    }

    private boolean validateConfiguredRegistration() {
        onConfiguredFieldChange();
        boolean valid = true;
        for (EventRegData field : fieldsDetails) {
            if (!isFieldVisible(field)) continue;
            String type = field.getFieldType();
            if ("display".equalsIgnoreCase(type)) continue;
            String error = null;
            if ("booleancheckbox".equalsIgnoreCase(type)) {
                if (field.isMandatory() && !getAcknowledgementValue(field)) error = field.getMandatoryMessage();
            } else if (isDropdownType(type) || "radio".equalsIgnoreCase(type)) {
                String selected = "radio".equalsIgnoreCase(type) ? field.getFieldValue()
                    : field.getFielddynamicValue() == null ? null : field.getFielddynamicValue().getId().toString();
                if (selected == null || selected.isEmpty()) {
                    if (field.isMandatory()) error = field.getMandatoryMessage();
                } else {
                    boolean allowed = false;
                    for (Action action : getConfiguredActions(field))
                        if (action.getId().toString().equals(selected)) allowed = true;
                    if (!allowed) error = "Please select a valid option.";
                }
            } else if ("calendar".equalsIgnoreCase(type)) {
                if (field.isMandatory() && field.getFieldDateValue() == null) error = field.getMandatoryMessage();
            } else {
                error = configuredTextError(field, field.getFieldValue() == null ? "" : field.getFieldValue());
            }
            if (error != null) {
                valid = false;
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, field.getFieldName(), error));
            }
        }
        if (!valid) FacesContext.getCurrentInstance().validationFailed();
        return valid;
    }

}

