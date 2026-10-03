package JSF;

import ejb.*;
import java.io.*;
import java.math.BigDecimal;
import java.text.*;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.transaction.Status;
import javax.transaction.SystemException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.event.*;
import org.primefaces.model.file.UploadedFile;
import util.JsfUtil;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

@ManagedBean(name = "registrationMasterController")
@ViewScoped
public class RegistrationPageMasterController implements Serializable {

    public EventMaster createEvent;
    public RegistrationLimit eventRegLimit;
    UploadedFile fileUpload;
    EventMaster event;
    String ddlActionName = "";
    public EventRegData uploadEventFields;
    public String genratedUrl;
    public EventMaster eventForUrl;
    public EventRegData selected;
    public boolean showSubmit = true;
    public boolean showUpdate = false;
    public Action current;
    List<Action> ddlValue;
    List<Action> actionOptions = new ArrayList<Action>();
    public Action ddlSelected;
    Set<String> ddlValues = new HashSet<String>();
    private String oldEventUuid;
    private String newEventUuid;

    @EJB
    sb.EventMasterFacade ejbFacade;

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

    @EJB
    sb.RegistrationLimitFacade registrationLimitFacade;

    @PostConstruct
    public void init() {
        event = new EventMaster();
        createEvent = new EventMaster();
        current = new Action();
        eventRegLimit = new RegistrationLimit();
        ddlValue = actionFacade.getAllRecords();
    }

    public UploadedFile getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(UploadedFile fileUpload) {
        this.fileUpload = fileUpload;
    }

//    public SelectItem[] getItemsAvailableSelectOneEvent() {
//
//        List<EventMaster> values = ejbFacade.getAllRecords();
//        boolean selectOne = true;
//        int size = selectOne ? values.size() + 1 : values.size();
//        SelectItem[] items = new SelectItem[size];
//        int i = 0;
//        if (selectOne) {
//            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
//            if (locale == null || locale.getLanguage().equals("en") || locale == Locale.ENGLISH) {
//                items[0] = new SelectItem("", "Select..");
//            } else {
//                items[0] = new SelectItem("", "اختر..");
//            }
//            i++;
//        }
//
//        for (Object x : values) {
//            items[i++] = new SelectItem(x, x.toString());
//        }
//        return items;
//
//    }
    @FacesConverter(forClass = Action.class)
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

    @FacesConverter(forClass = EventMaster.class)
    public static class EventConverter implements Converter {

        @Override
        public EventMaster getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.trim().length() == 0) {
                return null;
            }
            RegistrationPageMasterController controller = (RegistrationPageMasterController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "registrationMasterController");
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

    public EventMaster getCreateEvent() {
        return createEvent;
    }

    public void setCreateEvent(EventMaster createEvent) {
        this.createEvent = createEvent;
    }

    public String eventUpdate() throws SystemException {

        EntityManager em = masterFacade.getEntityManager();
        try {

            masterFacade.getUt().begin();

            em.merge(createEvent);
            em.merge(eventRegLimit);

            masterFacade.getUt().commit();

            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Record updated successfully"));

        } catch (Exception e) {
            JsfUtil.addErrorMessageGeneral("General Error: Bed type not updated");
            return null;
        }
        createEvent = new EventMaster();
        eventRegLimit = new RegistrationLimit();
        showSubmit = true;
        showUpdate = false;
        return "createEvent.xhtml?faces-redirect=true";
    }

    public String eventCreation() throws SystemException {
        createEvent.setEvent_uuid(generateUUID());
        createEvent.setRegister_email("registerationEmail.ftl");
        createEvent.setSurvey_email("SurveyEmail.ftl");

        EntityManager em = masterFacade.getEntityManager();
        try {
            masterFacade.getUt().begin();

            //ejbFacade.create(current);
            em.persist(createEvent);
            eventRegLimit.setEvent_uuid(createEvent);
            em.persist(eventRegLimit);

            masterFacade.getUt().commit();
            //JsfUtil.addSuccessMessage("Record added successfully");
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Record added successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            if (masterFacade.getUt().getStatus() == Status.STATUS_ACTIVE) {
                masterFacade.getUt().rollback();
            }

            JsfUtil.addErrorMessageGeneral("Error creating project. Please contact administrator or try again later");
            return null;
        }
        createEvent = new EventMaster();
        eventRegLimit = new RegistrationLimit();
        return "createEvent.xhtml?faces-redirect=true";
    }

    public String generateUUID() {
        String uuid = UUID.randomUUID().toString();
        EventMaster ifPresent = ejbFacade.getRecordByuuid(uuid);
        if (ifPresent != null) {
            generateUUID();
        }
        return uuid;
    }

    public boolean checkError(String value) {
        return StringUtils.isNotBlank(value);
    }

    public RegistrationLimit getEventRegLimit() {
        return eventRegLimit;
    }

    public void setEventRegLimit(RegistrationLimit eventRegLimit) {
        this.eventRegLimit = eventRegLimit;
    }

    public List<EventMaster> getAllRecord() {
        List<EventMaster> items = ejbFacade.getAllRecords();
        return items;
    }

    public void edit(EventMaster selected) {
        createEvent = selected;
        eventRegLimit = registrationLimitFacade.getRecordByuuidNType(selected.getEvent_uuid());
        showSubmit = false;
        showUpdate = true;

    }

    public String editFields() {
        return "editField";

    }

    public boolean isShowSubmit() {
        return showSubmit;
    }

    public void setShowSubmit(boolean showSubmit) {
        this.showSubmit = showSubmit;
    }

    public boolean isShowUpdate() {
        return showUpdate;
    }

    public void setShowUpdate(boolean showUpdate) {
        this.showUpdate = showUpdate;
    }

    public String saveFile() throws FileNotFoundException, IOException, SystemException, InterruptedException, Exception {
        try {
            System.out.println("Uploaded File Name Is :: " + fileUpload.getFileName() + " :: Uploaded File Size :: " + fileUpload.getSize());
            EntityManager em = masterFacade.getEntityManager();
            uploadEventFields = new EventRegData();
            int countrow = 0;
            int notupdated = 0;
            String sheetName;
            Sheet sheet;
            int num;
//            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            DataFormatter formatter = new DataFormatter();
            try (InputStream input = fileUpload.getInputStream()) {

                String fileName = fileUpload.getFileName();
                //FileInputStream inputStream = new FileInputStream(new File());
                if (fileName.contains("xlsx") || fileName.contains("xls")) {
                    Workbook wb;
                    if (fileName.contains("xlsx")) {
                        wb = new XSSFWorkbook(fileUpload.getInputStream());
                        //Files.copy(input, new File("C:\\saveExcel", fileName).toPath(), REPLACE_EXISTING);
                    } else {
                        wb = new HSSFWorkbook(fileUpload.getInputStream());

                    }
                    if (ddlActionName.equalsIgnoreCase("Registration Page Parameters")) {
                        sheetName = wb.getSheetName(0);
                        sheet = wb.getSheet(sheetName);
                        num = sheet.getLastRowNum();
                        for (int j = 0; j < 1; j++) {
                            Row row1 = sheet.getRow(j);
                            if (row1 != null) {
                                Iterator<Cell> celIterator = row1.cellIterator();
                                countrow++;
                                while (celIterator.hasNext()) {
                                    Cell nextCell = celIterator.next();
                                    int columnIndex = nextCell.getColumnIndex();

                                    switch (columnIndex) {
                                        case 0:
                                            String fieldname = formatter.formatCellValue(row1.getCell(columnIndex));
                                            if (fieldname.equalsIgnoreCase("FIELDNAME")) {
                                                break;
                                            } else {
                                                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                return "createFields?faces?faces-redirect=true";
                                            }
                                        case 1:
                                            String FieldName = formatter.formatCellValue(row1.getCell(columnIndex));
                                            if (FieldName.equalsIgnoreCase("FIELD_TYPE")) {
                                                break;
                                            } else {
                                                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                return "createFields?faces?faces-redirect=true";
                                            }

                                        case 2:
                                            String LastName = formatter.formatCellValue(row1.getCell(columnIndex));
                                            if (LastName.equalsIgnoreCase("FIELD_LENGTH")) {
                                                break;
                                            } else {
                                                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                return "createFields?faces?faces-redirect=true";
                                            }
                                        case 3:
                                            String venue = formatter.formatCellValue(row1.getCell(columnIndex));
                                            //current.setLocation(venue);
                                            if (venue.equalsIgnoreCase("IS_MANDATORY")) {
                                                break;
                                            } else {
                                                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                return "createFields?faces?faces-redirect=true";
                                            }

                                        case 4:
                                            String activityCode = formatter.formatCellValue(row1.getCell(columnIndex));
                                            //current.setActivityCode(activityCode);
                                            if (activityCode.equalsIgnoreCase("ACTION")) {
                                                break;
                                            } else {
                                                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                return "createFields?faces?faces-redirect=true";
                                            }

                                        case 5:
                                            String hours = formatter.formatCellValue(row1.getCell(columnIndex));
                                            //St total_hoursAttended = Double.parseDouble(hours);
                                            //current.setTotalHours(total_hoursAttended);
                                            if (hours.equalsIgnoreCase("MANDATORY_MESSAGE")) {
                                                break;
                                            } else {
                                                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                return "createFields?faces?faces-redirect=true";
                                            }

                                        //break;
                                        case 6:

                                            String attend = formatter.formatCellValue(row1.getCell(columnIndex));

                                            if (attend.equalsIgnoreCase("REGEX_PATTERN")) {
                                                break;
                                            } else {
                                                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                return "createFields?faces?faces-redirect=true";
                                            }

                                        case 7:

                                            String To = formatter.formatCellValue(row1.getCell(columnIndex));

                                            if (To.equalsIgnoreCase("VALIDATIONMSG")) {
                                                break;
                                            } else {
                                                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                return "createFields?faces?faces-redirect=true";
                                            }

                                        case 8:

                                            String From = formatter.formatCellValue(row1.getCell(columnIndex));

                                            if (From.equalsIgnoreCase("FIELDNAME_AR")) {
                                                break;
                                            } else {
                                                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                return "createFields?faces?faces-redirect=true";
                                            }
                                        case 9:
                                            String issueing = formatter.formatCellValue(row1.getCell(columnIndex));
                                            if (issueing.equalsIgnoreCase("MANDATORY_MESSAGE_AR")) {
                                                break;
                                            } else {
                                                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                return "createFields?faces?faces-redirect=true";
                                            }

                                        case 10:
                                            String email = formatter.formatCellValue(row1.getCell(columnIndex));
                                            if (email.equalsIgnoreCase("VALIDATION_MESSAGE_AR")) {
                                                break;
                                            } else {
                                                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                return "createFields?faces?faces-redirect=true";
                                            }

                                    }

                                }
                            }

                        }
                        if (num != 0) {
                            for (int i = 1; i <= num; i++) {
                                uploadEventFields = new EventRegData();
                                uploadEventFields.setEvent_Id(event);
                                uploadEventFields.setEventUuid(event);
                                Row row = sheet.getRow(i);
                                if (row != null) {
                                    Iterator<Cell> cellIterator = row.cellIterator();

                                    while (cellIterator.hasNext()) {
                                        Cell nextCell = cellIterator.next();
                                        int columnIndex = nextCell.getColumnIndex();

                                        switch (columnIndex) {
                                            case 0:
                                                String fieldName = formatter.formatCellValue(row.getCell(columnIndex));
                                                if (fieldName == null) {
                                                    break;
                                                }
                                                uploadEventFields.setFieldName(fieldName);
                                                break;
                                            case 1:
                                                String fieldType = formatter.formatCellValue(row.getCell(columnIndex));
                                                uploadEventFields.setFieldType(fieldType.toLowerCase());

                                                break;
                                            case 2:
                                                String fieldLength = formatter.formatCellValue(row.getCell(columnIndex));

                                                uploadEventFields.setFieldLength(Integer.parseInt(fieldLength));
                                                break;
                                            case 3:
                                                String isMandatory = formatter.formatCellValue(row.getCell(columnIndex));

                                                uploadEventFields.setMandatory(Boolean.parseBoolean(isMandatory));

                                                break;
                                            case 4:
                                                String action = formatter.formatCellValue(row.getCell(columnIndex));
                                                if (!action.isEmpty()) {
                                                    uploadEventFields.setActionType(action);
                                                }
                                                break;
                                            case 5:
                                                String mandatoryMsg = formatter.formatCellValue(row.getCell(columnIndex));
                                                if (!mandatoryMsg.isEmpty()) {
                                                    uploadEventFields.setMandatoryMessage(mandatoryMsg);
                                                }

                                                break;
                                            case 6:
                                                String regexPattern = formatter.formatCellValue(row.getCell(columnIndex));
                                                if (!regexPattern.isEmpty()) {
                                                    uploadEventFields.setPattern(regexPattern);
                                                }
                                                break;

                                            case 7:
                                                String validationMsg = formatter.formatCellValue(row.getCell(columnIndex));
                                                if (!validationMsg.isEmpty()) {
                                                    uploadEventFields.setValidationmsg(validationMsg);
                                                }
                                                break;

                                            case 8:
                                                String fieldNameAr = formatter.formatCellValue(row.getCell(columnIndex));
                                                if (!fieldNameAr.isEmpty()) {
                                                    uploadEventFields.setFieldname_ar(fieldNameAr);
                                                }
                                                break;
                                            case 9:
                                                String mandatoryMsgAr = formatter.formatCellValue(row.getCell(columnIndex));
                                                if (!mandatoryMsgAr.isEmpty()) {
                                                    uploadEventFields.setMandatory_message_ar(mandatoryMsgAr);
                                                }
                                                break;
                                            case 10:
                                                String validationMsgAr = formatter.formatCellValue(row.getCell(columnIndex));
                                                if (!validationMsgAr.isEmpty()) {
                                                    uploadEventFields.setValidationMsg_ar(validationMsgAr);
                                                }
                                                break;

                                        }

                                    }

                                }
                                try {
                                    masterFacade.getUt().begin();

                                    em.persist(uploadEventFields);

                                    masterFacade.getUt().commit();
                                    FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Sheet for event fields" + event.getEventTitle() + " named " + fileUpload.getFileName() + " is uploaded."));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (masterFacade.getUt().getStatus() == Status.STATUS_ACTIVE) {
                                        masterFacade.getUt().rollback();
                                    }
                                    uploadEventFields = new EventRegData();
                                    event = null;
                                    ddlActionName = "";
                                    FacesMessage message = new FacesMessage("error", e.getMessage());
                                    FacesContext.getCurrentInstance().addMessage(null, message);
                                    throw e;

                                }
                            }
                        }
                        uploadEventFields = new EventRegData();
                        event = null;
                        ddlActionName = "";
                    } else if (ddlActionName.equalsIgnoreCase("Dropdown Values")) {
                        sheetName = wb.getSheetName(1);
                        sheet = wb.getSheet(sheetName);
                        num = sheet.getLastRowNum();
                        for (int j = 0; j < 1; j++) {
                            Row row1 = sheet.getRow(j);
                            if (row1 != null) {
                                Iterator<Cell> celIterator = row1.cellIterator();
                                countrow++;
                                while (celIterator.hasNext()) {
                                    Cell nextCell = celIterator.next();
                                    int columnIndex = nextCell.getColumnIndex();
                                    switch (columnIndex) {
                                        case 0:
                                            String fieldname = formatter.formatCellValue(row1.getCell(columnIndex));
                                            if (fieldname.equalsIgnoreCase("Action Type")) {
                                                break;
                                            } else {
                                                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                return "createFields?faces?faces-redirect=true";
                                            }
                                        case 1:
                                            String FieldName = formatter.formatCellValue(row1.getCell(columnIndex));
                                            if (FieldName.equalsIgnoreCase("Action Value English")) {
                                                break;
                                            } else {
                                                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                return "createFields?faces?faces-redirect=true";
                                            }
                                        case 2:
                                            String actionValue_ar = formatter.formatCellValue(row1.getCell(columnIndex));
                                            if (actionValue_ar.equalsIgnoreCase("Action Value Arabic")) {
                                                break;
                                            } else {
                                                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                return "createFields?faces?faces-redirect=true";
                                            }
                                    }
                                }
                            }
                        }
                        if (num != 0) {
                            for (int i = 1; i <= num; i++) {
                                current = new Action();

                                Row row = sheet.getRow(i);
                                if (row != null) {
                                    Iterator<Cell> cellIterator = row.cellIterator();

                                    while (cellIterator.hasNext()) {
                                        Cell nextCell = cellIterator.next();
                                        int columnIndex = nextCell.getColumnIndex();

                                        switch (columnIndex) {
                                            case 0:
                                                String fieldName = formatter.formatCellValue(row.getCell(columnIndex));
                                                if (fieldName == null) {

                                                    FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
                                                    return "createFields?faces?faces-redirect=true";
                                                }
                                                //EventRegData action_type = eventRegDataFacade.getrecordBYUUIDNActiontype(event, fieldName);
//                                                if (action_type == null) {
//                                                    FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
//                                                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", fileUpload.getFileName() + " is invalid."));
//                                                    return "createFields?faces?faces-redirect=true";
//                                                }
                                                current.setAction_type(fieldName);
                                                break;
                                            case 1:
                                                String fieldType = formatter.formatCellValue(row.getCell(columnIndex));
                                                current.setAction_values(fieldType);

                                                break;
                                            case 2:
                                                String fieldValue_ar = formatter.formatCellValue(row.getCell(columnIndex));

                                                current.setAction_value_ar(fieldValue_ar);
                                                break;
                                        }
                                    }
                                }
                                try {
                                    masterFacade.getUt().begin();

                                    em.persist(current);

                                    masterFacade.getUt().commit();
                                    FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Sheet for event fields" + event.getEventTitle() + " named " + fileUpload.getFileName() + " is uploaded."));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (masterFacade.getUt().getStatus() == Status.STATUS_ACTIVE) {
                                        masterFacade.getUt().rollback();
                                    }
                                    current = new Action();
                                    event = null;
                                    ddlActionName = "";
                                    FacesMessage message = new FacesMessage("error", e.getMessage());
                                    FacesContext.getCurrentInstance().addMessage(null, message);
                                    throw e;

                                }
                            }
                        }
                        current = new Action();
                        event = null;
                        ddlActionName = "";

                    }

                }

            } catch (IOException e) {
                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", fileUpload.getFileName() + " is empty."));
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return "createFields?faces?faces-redirect=true";
    }

    public EventMaster getEvent() {
        return event;
    }

    public void setEvent(EventMaster event) {
        this.event = event;
    }

    public EventRegData getUploadEventFields() {
        return uploadEventFields;
    }

    public void setUploadEventFields(EventRegData uploadEventFields) {
        this.uploadEventFields = uploadEventFields;
    }

    public void generateUrlForEvent() {
        String baseUrl = getBaseUrl();
        genratedUrl = baseUrl + "/faces/registeration.xhtml?lang=en&uuid=" + eventForUrl.getEvent_uuid();

    }
    
    public String getBaseUrl() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String baseUrl = request.getScheme() + ":/" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        return baseUrl;
    }

    public String getGenratedUrl() {
        return genratedUrl;
    }

    public void setGenratedUrl(String genratedUrl) {
        this.genratedUrl = genratedUrl;
    }

    public EventMaster getEventForUrl() {
        return eventForUrl;
    }

    public void setEventForUrl(EventMaster eventForUrl) {
        this.eventForUrl = eventForUrl;
    }

    public List<EventRegData> getAllFieldsRecord() {
        List<EventRegData> items = eventRegDataFacade.getAllRecords();
        return items;
    }

    public List<Action> getDdlValue() {
        return ddlValue;
    }

    public void setDdlValue(List<Action> ddlValue) {
        this.ddlValue = ddlValue;
    }

    public EventRegData getSelected() {
        return selected;
    }

    public void setSelected(EventRegData selected) {
        this.selected = selected;
    }

    public String updateFields() {
        EntityManager em = masterFacade.getEntityManager();
        try {

            masterFacade.getUt().begin();

            em.merge(selected);
            masterFacade.getUt().commit();

            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Record updated successfully"));

        } catch (Exception e) {
            JsfUtil.addErrorMessageGeneral("General Error: Field not updated");
            return null;
        }
        return "createFields?faces-redirect=true";
    }

    public String getDdlActionName() {
        return ddlActionName;
    }

    public void setDdlActionName(String ddlActionName) {
        this.ddlActionName = ddlActionName;
    }

    public void onddlRowEdit(RowEditEvent<Action> event) {
        if (!StringUtils.isBlank(event.getObject().getAction_type())) {
            EntityManager em = masterFacade.getEntityManager();
            try {

                masterFacade.getUt().begin();

                em.merge(event.getObject());

                masterFacade.getUt().commit();
                JsfUtil.addSuccessMessage("Record updated successfully");
                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Record updated successfully"));

            } catch (Exception e) {
                JsfUtil.addErrorMessageGeneral("General Error: ddl value not updated");

            }

            System.out.println(event.getObject().getAction_values());
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Action Type is mandatory"));
        }
    }

    public String deleteDDL() throws IllegalStateException, SecurityException, SystemException {
        //System.out.println(ddlSelected.getAction_type().getActionType());
        EntityManager em = masterFacade.getEntityManager();
        try {
            masterFacade.getUt().begin();
            Action bed = em.merge(ddlSelected);
            em.remove(bed);
            masterFacade.getUt().commit();
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Record deleted successfully"));
        } catch (Exception e) {

            masterFacade.getUt().rollback();
            JsfUtil.addErrorMessageGeneral("Error deleting the dropdown value. Please contact administrator or try again later");
            return null;
        }
        return "createFields?faces-redirect=true";
    }

    public Action getDdlSelected() {
        return ddlSelected;
    }

    public void setDdlSelected(Action ddlSelected) {
        this.ddlSelected = ddlSelected;
    }

    public String opencreateEventPage() {
        createEvent = new EventMaster();
        eventRegLimit = new RegistrationLimit();
        showSubmit = true;
        showUpdate = false;
        return "/jsf/createEvent.xhtml?faces-redirect=true";
    }

    public void updateValues() {
        event.setEventRegDetails(eventRegDataFacade.getAllRecordsByUUID(event.getEvent_uuid()));
        for (EventRegData reg : event.getEventRegDetails()) {
            if (reg.getFieldLength() == 8) {
                reg.setFieldType("textboxMobile");
            }
            if (reg.getPattern() != null) {
                switch (reg.getPattern()) {
                    case "^[a-zA-Z_ \\u0621-\\u064A ]*$":
                        reg.setFieldTypePattern("String");
                        break;
                    case "[\\u0621-\\u064A\\u0660-\\u0669a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[\\u0621-\\u064A\\u0660-\\u0669a-zA-Z0-9][\\u0621-\\u064A\\u0660-\\u0669a-zA-Z0-9\\-]{0,64}(\\.[\\u0621-\\u064A\\u0660-\\u0669a-zA-Z0-9][\\u0621-\\u064A\\u0660-\\u0669a-zA-Z0-9\\-]{0,25})+":
                        reg.setFieldTypePattern("Email");
                        break;
                    case "^[0-9\\u0660-\\u0669]*$":
                        reg.setFieldTypePattern("Number");
                        break;
                    case "^\\s*[\\da-zA-Z0-9][\\da-zA-Z0-9\\s]*$":
                        reg.setFieldTypePattern("AlphaNumeric");
                        break;

                }
            }

        }
        event.getEventRegDetails().add(new EventRegData(event, event));
    }

    public void addToEventFields() {
        event.getEventRegDetails().add(new EventRegData(event, event));
    }

    public void removeFromEventFieldDetails(EventRegData regData) {
        event.getEventRegDetails().remove(regData);
    }

    public String saveEventFieldData() throws SystemException {
        if (event == null || event.getEventRegDetails() == null || event.getEventRegDetails().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "No fields", "Select an event and add registration fields."));
            return null;
        }
        if (!validateFieldHierarchyConfiguration()) return null;
        try {
            System.out.println(event.getEventRegDetails().get(0).getFieldName());
            EntityManager em = masterFacade.getEntityManager();
            masterFacade.getUt().begin();
            for (EventRegData fields : event.getEventRegDetails()) {
                switch (fields.getFieldType()) {
                    case "textbox":
                        fields.setFieldLength(600);
                        break;
                    case "textboxMobile":
                        fields.setFieldLength(8);
                        break;
                }
                if (fields.getFieldTypePattern() != null) {
                    switch (fields.getFieldTypePattern()) {
                        case "String":
                            fields.setPattern("^[a-zA-Z_ \\u0621-\\u064A\\-]*$");
                            break;
                        case "Email":
                            fields.setPattern("[\\u0621-\\u064A\\u0660-\\u0669a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[\\u0621-\\u064A\\u0660-\\u0669a-zA-Z0-9][\\u0621-\\u064A\\u0660-\\u0669a-zA-Z0-9\\-]{0,64}(\\.[\\u0621-\\u064A\\u0660-\\u0669a-zA-Z0-9][\\u0621-\\u064A\\u0660-\\u0669a-zA-Z0-9\\-]{0,25})+");
                            break;
                        case "Number":
                            fields.setPattern("^[0-9\\u0660-\\u0669]*$");
                            break;
                        case "AlphaNumeric":
                            fields.setPattern("^\\s*[\\da-zA-Z0-9][\\da-zA-Z0-9\\s]*$");
                            break;
                    }
                }
                if (fields.getFieldType().equalsIgnoreCase("textboxMobile")) {
                    fields.setFieldType("textbox");
                }
                if (fields.getId() == null) {
                    em.persist(fields);
                } else {
                    em.merge(fields);
                }

            }
            masterFacade.getUt().commit();
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Records updated successfully"));
        } catch (Exception ex) {
            ex.printStackTrace();

            if (masterFacade.getUt().getStatus() == Status.STATUS_ACTIVE) {
                masterFacade.getUt().rollback();
            }
        }

        event = new EventMaster();

        return "createFields?faces?faces-redirect=true";
    }

    public String opencreateFieldCreatePage() {
        event = new EventMaster();
        actionOptions = new ArrayList<>();
        ddlActionName = null;
        return "/jsf/createFields.xhtml?faces-redirect=true";
    }

    public void onSelectOption() {
        event.setEventRegDetails(null);

    }

    public void onSelectValues() {
        actionOptions = new ArrayList<>();
        ddlActionName = null;
    }

    public SelectItem[] itemsAvailableSelectActionType() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        ddlValues = eventRegDataFacade.getRecordByFieldType("dropdown");
        SelectItem[] items = null;
        int i = 0;
        int size = ddlValues.size();
        items = new SelectItem[size];

        if (locale == null || locale.getLanguage().equals("en") || locale == Locale.ENGLISH) {
            items[0] = new SelectItem("", "Select..");
        } else {
            items[0] = new SelectItem("", "اختر..");
        }
        i++;

        for (String x : ddlValues) {
            if (!x.equalsIgnoreCase("Residence2")) {
                items[i++] = new SelectItem(x, x);
            }

        }
        return items;

    }

    public void updateOptions() {
        actionOptions = actionFacade.getRecordByType(ddlActionName);
        actionOptions.add(new Action(ddlActionName));
    }

    public List<Action> getActionOptions() {
        return actionOptions;
    }

    public void setActionOptions(List<Action> actionOptions) {
        this.actionOptions = actionOptions;
    }

    public void addFieldstoAction() {
        actionOptions.add(new Action(ddlActionName));
    }

    public void removeFieldFromAction(Action action) {
        actionOptions.remove(action);
    }

    public String saveFieldOptions() throws SystemException {
        try {
            EntityManager em = masterFacade.getEntityManager();
            masterFacade.getUt().begin();
            for (Action fields : actionOptions) {
                if (fields.getId() == null) {
                    em.persist(fields);
                } else {
                    em.merge(fields);
                }
            }

            masterFacade.getUt().commit();
            actionOptions = new ArrayList<>();
            ddlActionName = null;
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Records updated successfully"));

        } catch (Exception e) {
            e.printStackTrace();
            if (masterFacade.getUt().getStatus() == Status.STATUS_ACTIVE) {
                masterFacade.getUt().rollback();
            }
        }
        return "createFields?faces?faces-redirect=true";
    }
    
    public String copyEventFields() {
        try {
            eventRegDataFacade.copyEventRegistrationPage(oldEventUuid, newEventUuid);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Fields copied successfully", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error while copying fields: " + e.getMessage(), null));
        }
        return null; 
    }
    public void removeFromEventFieldDetails() {
    if (selected == null) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "No field selected."));
        return;
    }

    System.out.println("Deleting field: " + selected.getFieldName());

    EntityManager em = masterFacade.getEntityManager();
    try {
        masterFacade.getUt().begin();

        // Make sure entity is managed before removal
        EventRegData managed = em.find(EventRegData.class, selected.getId());
        if (managed != null) {
            em.remove(managed);
            System.out.println("Removed from DB: " + managed.getFieldName());
        }

        masterFacade.getUt().commit();

       

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Deleted", "Field \"" + selected.getFieldName() + "\" removed successfully."));

        selected = null; // clear selection after delete

    } catch (Exception e) {
        e.printStackTrace();
        try {
            if (masterFacade.getUt().getStatus() == Status.STATUS_ACTIVE) {
                masterFacade.getUt().rollback();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete field."));
    }
}

    public String getOldEventUuid() {
        return oldEventUuid;
    }

    public void setOldEventUuid(String oldEventUuid) {
        this.oldEventUuid = oldEventUuid;
    }

    public String getNewEventUuid() {
        return newEventUuid;
    }

    public void setNewEventUuid(String newEventUuid) {
        this.newEventUuid = newEventUuid;
    }
    

@SuppressWarnings("unchecked")
public java.util.List<ejb.EventRegData> getConfigurableFieldRows() {
    java.util.List<ejb.EventRegData> rows = javax.faces.context.FacesContext.getCurrentInstance()
        .getApplication().evaluateExpressionGet(javax.faces.context.FacesContext.getCurrentInstance(),
            "#{registrationMasterController.event.eventRegDetails}", java.util.List.class);
    return rows == null ? java.util.Collections.emptyList() : rows;
}

private boolean configSameField(ejb.EventRegData a, ejb.EventRegData b) {
    return a == b || (a != null && b != null && a.getId() != null && b.getId() != null
        && a.getId().compareTo(b.getId()) == 0);
}

private ejb.EventRegData configCanonicalField(ejb.EventRegData field) {
    for (ejb.EventRegData candidate : getConfigurableFieldRows())
        if (configSameField(candidate, field)) return candidate;
    return null;
}

private boolean configWouldCycle(ejb.EventRegData child, ejb.EventRegData parent) {
    java.util.Set<java.math.BigDecimal> seen = new java.util.HashSet<>();
    while (parent != null) {
        if (configSameField(child, parent) || parent.getId() == null || !seen.add(parent.getId())) return true;
        ejb.EventRegData loaded = configCanonicalField(parent);
        if (loaded == null) return true;
        parent = loaded.getParentField();
    }
    return false;
}

public java.util.List<javax.faces.model.SelectItem> getConfigParentItems(ejb.EventRegData child) {
    java.util.List<javax.faces.model.SelectItem> items = new java.util.ArrayList<>();
    for (ejb.EventRegData candidate : getConfigurableFieldRows()) {
        String type = candidate.getFieldType();
        boolean optionField = "radio".equalsIgnoreCase(type) || "dropdown".equalsIgnoreCase(type)
            || "dropdownOther".equalsIgnoreCase(type) || "dropdownSite".equalsIgnoreCase(type)
            || "listbox".equalsIgnoreCase(type);
        if (candidate.getId() != null && optionField && !configWouldCycle(child, candidate))
            items.add(new javax.faces.model.SelectItem(candidate, candidate.getFieldName()));
    }
    return items;
}

public java.util.List<ejb.Action> getConfigTriggerActions(ejb.EventRegData child) {
    java.util.List<ejb.Action> actions = new java.util.ArrayList<>();
    ejb.EventRegData parent = configCanonicalField(child.getParentField());
    if (parent == null || parent.getActionType() == null || parent.getActionType().trim().isEmpty()) return actions;
    ejb.EventMaster selectedEvent = javax.faces.context.FacesContext.getCurrentInstance()
        .getApplication().evaluateExpressionGet(javax.faces.context.FacesContext.getCurrentInstance(),
            "#{registrationMasterController.event}", ejb.EventMaster.class);
    if (selectedEvent == null) return actions;
    for (ejb.Action action : actionFacade.getRecordByType(parent.getActionType()))
        if (action.getEvent_uuid() == null || (selectedEvent.getEvent_uuid() != null
                && selectedEvent.getEvent_uuid().equals(action.getEvent_uuid().getEvent_uuid()))) actions.add(action);
    actions.sort(java.util.Comparator.comparing(ejb.Action::getId));
    return actions;
}

public void onConfigParentChanged(ejb.EventRegData child) {
    child.setShowWhenAction(null);
}

public void onConfigOptionGroupChanged() {
    for (ejb.EventRegData child : getConfigurableFieldRows()) {
        if (child.getShowWhenAction() == null) continue;
        boolean valid = false;
        for (ejb.Action action : getConfigTriggerActions(child))
            if (action.getId().equals(child.getShowWhenAction().getId())) valid = true;
        if (!valid) child.setShowWhenAction(null);
    }
}

// Call at the START of saveEventFieldData(), before any database write:
// if (!validateFieldHierarchyConfiguration()) return null;
public boolean validateFieldHierarchyConfiguration() {
    boolean valid = true;
    for (ejb.EventRegData child : getConfigurableFieldRows()) {
        if (child.getParentField() == null) { child.setShowWhenAction(null); continue; }
        boolean parentAllowed = false;
        for (javax.faces.model.SelectItem item : getConfigParentItems(child))
            if (configSameField(child.getParentField(), (ejb.EventRegData) item.getValue())) parentAllowed = true;
        boolean triggerAllowed = false;
        for (ejb.Action action : getConfigTriggerActions(child))
            if (child.getShowWhenAction() != null && action.getId().equals(child.getShowWhenAction().getId())) triggerAllowed = true;
        if (!parentAllowed || !triggerAllowed) {
            valid = false;
            javax.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new javax.faces.application.FacesMessage(javax.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Invalid field dependency: " + child.getFieldName(),
                    "Choose a valid parent and one of its Action options. Parent cycles are not allowed."));
        }
    }
    if (!valid) javax.faces.context.FacesContext.getCurrentInstance().validationFailed();
    return valid;
}

public javax.faces.convert.Converter getConfigParentConverter() {
    return new RegistrationConfigParentConverter();
}

public javax.faces.convert.Converter getConfigTriggerConverter() {
    return new RegistrationConfigTriggerConverter();
}

public static class RegistrationConfigTriggerConverter implements javax.faces.convert.Converter {
    @Override public Object getAsObject(javax.faces.context.FacesContext context,
            javax.faces.component.UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) return null;
        Integer id;
        try { id = Integer.valueOf(value); }
        catch (NumberFormatException error) {
            throw new javax.faces.convert.ConverterException("Invalid trigger Action ID.");
        }
        Object bean = context.getApplication().evaluateExpressionGet(context,
            "#{registrationMasterController}", Object.class);
        RegistrationPageMasterController controller = (RegistrationPageMasterController) bean;
        ejb.EventRegData child = (ejb.EventRegData) component.getAttributes().get("configuredField");
        if (child != null) for (ejb.Action action : controller.getConfigTriggerActions(child))
            if (id.equals(action.getId())) return action;
        throw new javax.faces.convert.ConverterException("Select an Action option belonging to this parent.");
    }
    @Override public String getAsString(javax.faces.context.FacesContext context,
            javax.faces.component.UIComponent component, Object value) {
        if (value == null) return "";
        ejb.Action action = (ejb.Action) value;
        return action.getId() == null ? "" : action.getId().toString();
    }
}

public static class RegistrationConfigParentConverter implements javax.faces.convert.Converter {
    @Override public Object getAsObject(javax.faces.context.FacesContext context,
            javax.faces.component.UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) return null;
        java.math.BigDecimal id;
        try { id = new java.math.BigDecimal(value); }
        catch (NumberFormatException error) { throw new javax.faces.convert.ConverterException("Invalid parent ID."); }
        java.util.List<?> rows = context.getApplication().evaluateExpressionGet(context,
            "#{registrationMasterController.event.eventRegDetails}", java.util.List.class);
        if (rows != null) for (Object row : rows) {
            ejb.EventRegData field = (ejb.EventRegData) row;
            if (field.getId() != null && field.getId().compareTo(id) == 0) return field;
        }
        throw new javax.faces.convert.ConverterException("Parent field is not in the selected event.");
    }
    @Override public String getAsString(javax.faces.context.FacesContext context,
            javax.faces.component.UIComponent component, Object value) {
        if (value == null) return "";
        ejb.EventRegData field = (ejb.EventRegData) value;
        return field.getId() == null ? "" : field.getId().toPlainString();
    }
}

}