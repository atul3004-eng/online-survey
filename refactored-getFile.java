// Refactored replacement for the pasted getFile(...) method.
// Paste this method and the helper methods into the same bean/class that owns
// ejbFacade, fileTypeFacade, extStatusFacade, intStatusFacade, selectedColumns,
// language, fileType, and file.

private static final String REQUEST_PHARMACEUTICAL = "Pharmaceutical";
private static final String REQUEST_HERBAL = "Herbal";

private static final String COL_SERIAL = "Serial";
private static final String COL_REG_NO = "Reg - Number";
private static final String COL_CATEGORY = "Category";
private static final String COL_GENERIC = "Generic name & Strength";
private static final String COL_API_MANUFACTURERS = "API Manufacturers";
private static final String COL_TRADE_NAME = "Trade name";
private static final String COL_APPLICATION_TYPE = "Type of Application";
private static final String COL_DOSAGE_FORM = "Dosage Form";
private static final String COL_PACKAGE_TYPE = "Package type";
private static final String COL_PACKAGE_SIZE = "Package size";
private static final String COL_MARKETING_COMPANY = "Marketing Company";
private static final String COL_NATIONALITY = "Nationality";
private static final String COL_BULK_MANUFACTURER = "Bulk Manufacturer";
private static final String COL_PRIMARY_PACKAGER = "Primary Packager";
private static final String COL_SECONDARY_PACKAGER = "Secondary Packager";
private static final String COL_BATCH_RELEASER = "Batch Releaser";
private static final String COL_COUNTRY_OF_MANUFACTURER = "Country of Manufacturer";
private static final String COL_STORAGE_CONDITIONS = "Storage conditions";
private static final String COL_SHELF_LIFE = "Shelf-life";
private static final String COL_AGENT_NAME = "Agent name";
private static final String COL_REG_DATE = "Reg Date";
private static final String COL_NOTES = "Notes";
private static final String COL_CIF_PRICE = "CIF Price (USD)";
private static final String COL_WHOLESALE = "Whole Sale (QAR)";
private static final String COL_RETAIL_PRICE = "Retail Price (QAR)";
private static final String COL_ACTIVE = "Active";
private static final String COL_PHARMACOLOGICAL_GROUP = "Pharmacological Group";

private static final Map<String, String> ARABIC_HEADERS = new HashMap<String, String>();

static {
    ARABIC_HEADERS.put(COL_SERIAL, "Ù…Ø³Ù„Ø³Ù„");
    ARABIC_HEADERS.put(COL_REG_NO, "Ø±Ù‚Ù… Ø§Ù„ØªØ³Ø¬ÙŠÙ„");
    ARABIC_HEADERS.put(COL_CATEGORY, COL_CATEGORY);
    ARABIC_HEADERS.put(COL_GENERIC, "Ø§Ù„Ø§Ø³Ù… Ø§Ù„Ø¹Ù„Ù…ÙŠ ÙˆØ§Ù„ØªØ±ÙƒÙŠØ²");
    ARABIC_HEADERS.put(COL_API_MANUFACTURERS, COL_API_MANUFACTURERS);
    ARABIC_HEADERS.put(COL_TRADE_NAME, "Ø§Ù„Ø§Ø³Ù… Ø§Ù„ØªØ¬Ø§Ø±ÙŠ");
    ARABIC_HEADERS.put(COL_APPLICATION_TYPE, "Ù†ÙˆØ¹ Ø§Ù„Ø·Ù„Ø¨");
    ARABIC_HEADERS.put(COL_DOSAGE_FORM, "Ø§Ù„Ø´ÙƒÙ„ Ø§Ù„ØµÙŠØ¯Ù„Ø§Ù†ÙŠ");
    ARABIC_HEADERS.put(COL_PACKAGE_TYPE, "Ù†ÙˆØ¹ Ø§Ù„Ø¹Ø¨ÙˆØ©");
    ARABIC_HEADERS.put(COL_PACKAGE_SIZE, "Ø­Ø¬Ù… Ø§Ù„Ø¹Ø¨ÙˆØ©");
    ARABIC_HEADERS.put(COL_MARKETING_COMPANY, "Ø§Ø³Ù… Ø§Ù„Ø´Ø±ÙƒØ© Ø§Ù„Ù…Ø³ÙˆÙ‚Ø©");
    ARABIC_HEADERS.put(COL_NATIONALITY, "Ø¨Ù„Ø¯ Ø§Ù„Ø´Ø±ÙƒØ© Ø§Ù„Ù…Ø³ÙˆÙ‚Ø©");
    ARABIC_HEADERS.put(COL_BULK_MANUFACTURER, "Ø§Ø³Ù… Ø§Ù„Ù…ØµÙ†Ø¹");
    ARABIC_HEADERS.put(COL_PRIMARY_PACKAGER, "Ø§Ø³Ù… Ø§Ù„Ù…ØµÙ†Ø¹");
    ARABIC_HEADERS.put(COL_SECONDARY_PACKAGER, "Ø§Ø³Ù… Ø§Ù„Ù…ØµÙ†Ø¹");
    ARABIC_HEADERS.put(COL_BATCH_RELEASER, "Ø§Ø³Ù… Ø§Ù„Ù…ØµÙ†Ø¹");
    ARABIC_HEADERS.put(COL_COUNTRY_OF_MANUFACTURER, "Ø¨Ù„Ø¯ Ø§Ù„Ù…ØµÙ†Ø¹");
    ARABIC_HEADERS.put(COL_STORAGE_CONDITIONS, "Ø¸Ø±ÙˆÙ Ø§Ù„ØªØ®Ø²ÙŠÙ†");
    ARABIC_HEADERS.put(COL_SHELF_LIFE, "Ù…Ø¯Ø© Ø§Ù„ØµÙ„Ø§Ø­ÙŠØ©");
    ARABIC_HEADERS.put(COL_AGENT_NAME, "Ø§Ù„ÙˆÙƒÙŠÙ„");
    ARABIC_HEADERS.put(COL_REG_DATE, "ØªØ§Ø±ÙŠØ® Ø§Ù„ØªØ³Ø¬ÙŠÙ„");
    ARABIC_HEADERS.put(COL_NOTES, "Ù…Ù€Ù€Ù„Ø§Ø­Ù€Ù€Ù€Ø¸Ù€Ù€Ù€Ø§Øª");
    ARABIC_HEADERS.put(COL_CIF_PRICE, "Ø³Ø¹Ø± Ø§Ù„Ø§Ø³ØªÙŠØ±Ø§Ø¯");
    ARABIC_HEADERS.put(COL_WHOLESALE, "Ø³Ù€Ù€Ø± Ø§Ù„Ø¬Ù…Ù„Ø©");
    ARABIC_HEADERS.put(COL_RETAIL_PRICE, "Ø³Ù€Ù€Ø± Ø§Ù„Ø¬Ù€Ù€Ù…Ù€Ù‡ÙˆØ±");
    ARABIC_HEADERS.put(COL_ACTIVE, COL_ACTIVE);
    ARABIC_HEADERS.put(COL_PHARMACOLOGICAL_GROUP, COL_PHARMACOLOGICAL_GROUP);
}

public StreamedContent getFile(boolean isNewRegistration, boolean isReregistration) {
    try {
        boolean pharmaceuticalReport = isPharmaceuticalReport();

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = createReportSheet(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle bodyStyle = createBodyStyle(workbook);

        List<String> selectedColumnList = Arrays.asList(selectedColumns);
        List<String> reportColumns = buildReportColumns();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Row englishHeaderRow = sheet.createRow(0);
        writeHeaderRow(englishHeaderRow, reportColumns, headerStyle);

        Row arabicHeaderRow = sheet.createRow(1);
        writeArabicHeaderRow(arabicHeaderRow, reportColumns, headerStyle, pharmaceuticalReport);

        ExternalStatus approved = extStatusFacade.getRecordById(3);
        InternalStatus internalApproved = intStatusFacade.getRecordById(12);
        FileType selectedFileType = getSelectedFileType();
        List<RegisterProductMaster> products = getReportProducts(
                selectedFileType,
                approved,
                internalApproved,
                isNewRegistration,
                isReregistration,
                pharmaceuticalReport);

        int rowNumber = 2;
        for (RegisterProductMaster product : products) {
            Row row = sheet.createRow(rowNumber);
            writeSerialCell(row, rowNumber - 1, bodyStyle);
            writeProductRow(row, product, selectedColumnList, bodyStyle, dateFormat, isNewRegistration, pharmaceuticalReport);
            rowNumber++;
        }

        sheet.setAutoFilter(new CellRangeAddress(0, rowNumber - 1, 0, reportColumns.size() - 1));
        autoSizeColumns(sheet, reportColumns.size());

        file = buildDownload(workbook);
        System.out.println("Excel written successfully..");
    } catch (Exception e) {
        e.printStackTrace();
    }
    return file;
}

private HSSFSheet createReportSheet(HSSFWorkbook workbook) {
    HSSFSheet sheet = workbook.createSheet("Registration Report_" + fileType);
    sheet.createFreezePane(0, 2);

    Header topHeader = sheet.getHeader();
    topHeader.setLeft("*** Registration Report ***");
    topHeader.setCenter(HSSFHeader.font("Arial", "Bold")
            + HSSFHeader.fontSize((short) 14) + "SAMPLE ORDER");

    return sheet;
}

private CellStyle createHeaderStyle(HSSFWorkbook workbook) {
    Font boldFont = workbook.createFont();
    boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    boldFont.setFontHeightInPoints((short) 12);
    boldFont.setFontName("Cambria");

    CellStyle style = workbook.createCellStyle();
    style.setFont(boldFont);
    style.setWrapText(true);
    style.setAlignment(CellStyle.ALIGN_CENTER);

    HSSFPalette palette = workbook.getCustomPalette();
    HSSFColor color = palette.findSimilarColor((byte) 242, (byte) 220, (byte) 219);
    style.setFillForegroundColor(color.getIndex());
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);

    return style;
}

private CellStyle createBodyStyle(HSSFWorkbook workbook) {
    HSSFFont font = workbook.createFont();
    font.setFontHeightInPoints((short) 12);
    font.setFontName("Cambria");

    CellStyle style = workbook.createCellStyle();
    style.setWrapText(true);
    style.setAlignment(CellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    style.setFont(font);

    return style;
}

private List<String> buildReportColumns() {
    List<String> columns = new ArrayList<String>();
    columns.add(COL_SERIAL);
    columns.addAll(Arrays.asList(selectedColumns));
    return columns;
}

private void writeHeaderRow(Row row, List<String> columns, CellStyle style) {
    for (int index = 0; index < columns.size(); index++) {
        writeCell(row, index, columns.get(index), style);
    }
}

private void writeArabicHeaderRow(Row row, List<String> columns, CellStyle style, boolean pharmaceuticalReport) {
    writeCell(row, 0, ARABIC_HEADERS.get(COL_SERIAL), style);

    for (int index = 1; index < columns.size(); index++) {
        String column = columns.get(index);
        if (isArabicHeaderVisible(column, pharmaceuticalReport)) {
            writeCell(row, index, ARABIC_HEADERS.get(column), style);
        }
    }
}

private boolean isArabicHeaderVisible(String column, boolean pharmaceuticalReport) {
    if (!ARABIC_HEADERS.containsKey(column)) {
        return false;
    }
    if (COL_CATEGORY.equals(column)) {
        return !pharmaceuticalReport;
    }
    if (COL_API_MANUFACTURERS.equals(column)
            || COL_APPLICATION_TYPE.equals(column)
            || COL_BULK_MANUFACTURER.equals(column)
            || COL_PRIMARY_PACKAGER.equals(column)
            || COL_SECONDARY_PACKAGER.equals(column)
            || COL_PHARMACOLOGICAL_GROUP.equals(column)) {
        return pharmaceuticalReport;
    }
    return true;
}

private FileType getSelectedFileType() {
    if (isPharmaceuticalReport()) {
        return fileTypeFacade.getRecordById(1);
    }
    if (REQUEST_HERBAL.equalsIgnoreCase(language.getRequestType())) {
        return fileTypeFacade.getRecordById(2);
    }
    return null;
}

private List<RegisterProductMaster> getReportProducts(
        FileType type,
        ExternalStatus approved,
        InternalStatus internalApproved,
        boolean isNewRegistration,
        boolean isReregistration,
        boolean pharmaceuticalReport) {

    if (type == null) {
        return ejbFacade.getAllRegisteredRecords(approved, isNewRegistration, isReregistration);
    }

    if (pharmaceuticalReport) {
        return ejbFacade.getAllRegisteredRecordsByExtIntClassification(
                type, approved, internalApproved, isNewRegistration, isReregistration);
    }

    return ejbFacade.getAllRegisteredRecordsByClassification(
            type, approved, isNewRegistration, isReregistration);
}

private void writeSerialCell(Row row, int serialNumber, CellStyle style) {
    writeCell(row, 0, serialNumber, style);
}

private void writeProductRow(
        Row row,
        RegisterProductMaster product,
        List<String> columns,
        CellStyle style,
        DateFormat dateFormat,
        boolean isNewRegistration,
        boolean pharmaceuticalReport) {

    writeSelectedCell(row, columns, COL_REG_NO, getRegistrationNumber(product, isNewRegistration), style);
    writeSelectedCell(row, columns, COL_CATEGORY, getHerbalCategory(product), style);
    writeSelectedCell(row, columns, COL_GENERIC, getGenericNameAndStrength(product, pharmaceuticalReport), style);
    writeSelectedCell(row, columns, COL_API_MANUFACTURERS, wrap(getApiManufacturers(product), 18), style);
    writeSelectedCell(row, columns, COL_TRADE_NAME, wrap(product.getTradeNameEn(), 18), style);
    writeSelectedCell(row, columns, COL_APPLICATION_TYPE, nullToBlank(product.getApplicationType()), style);
    writeSelectedCell(row, columns, COL_DOSAGE_FORM, wrap(getDosageForm(product), 8), style);
    writeSelectedCell(row, columns, COL_PACKAGE_TYPE, wrap(getPackageType(product), 8), style);
    writeSelectedCell(row, columns, COL_PACKAGE_SIZE, wrap(product.getPackageSize(), 10), style);
    writeSelectedCell(row, columns, COL_MARKETING_COMPANY, wrap(getMarketingCompany(product), 12), style);
    writeSelectedCell(row, columns, COL_NATIONALITY, getMarketingCompanyCountry(product), style);
    writeSelectedCell(row, columns, COL_BULK_MANUFACTURER, wrap(getBulkManufacturers(product), 31), style);
    writeSelectedCell(row, columns, COL_PRIMARY_PACKAGER, wrap(getPrimaryPackagers(product), 31), style);
    writeSelectedCell(row, columns, COL_SECONDARY_PACKAGER, wrap(getSecondaryPackagers(product), 31), style);
    writeSelectedCell(row, columns, COL_BATCH_RELEASER, wrap(getBatchReleasers(product), 31), style);
    writeSelectedCell(row, columns, COL_COUNTRY_OF_MANUFACTURER, getManufacturerCountries(product), style);
    writeSelectedCell(row, columns, COL_STORAGE_CONDITIONS, wrap(product.getStorageConditions(), 22), style);
    writeSelectedCell(row, columns, COL_SHELF_LIFE, wrap(product.getShelfLife(), 12), style);
    writeSelectedCell(row, columns, COL_AGENT_NAME, getAgentName(product), style);
    writeSelectedCell(row, columns, COL_REG_DATE, formatDate(product.getMeetingDate(), dateFormat), style);
    writeSelectedCell(row, columns, COL_NOTES, wrap(product.getRemarks(), 28), style);
    writePriceCells(row, product, columns, style, pharmaceuticalReport);
    writeSelectedCell(row, columns, COL_ACTIVE, product.isActive() ? "Yes" : "No", style);
    writeSelectedCell(row, columns, COL_PHARMACOLOGICAL_GROUP, product.getPharmacologicalGroup(), style);
}

private void writePriceCells(
        Row row,
        RegisterProductMaster product,
        List<String> columns,
        CellStyle style,
        boolean pharmaceuticalReport) {

    if (pharmaceuticalReport) {
        writeSelectedCell(row, columns, COL_CIF_PRICE, product.getCifPrice(), style);
        writeSelectedCell(row, columns, COL_WHOLESALE, product.getWholesalePrice(), style);
        writeSelectedCell(row, columns, COL_RETAIL_PRICE, product.getRetailPrice(), style);
        return;
    }

    writeSelectedCell(row, columns, COL_CIF_PRICE, joinHerbalCifPrices(product), style);
    writeSelectedCell(row, columns, COL_WHOLESALE, joinHerbalWholesalePrices(product), style);
    writeSelectedCell(row, columns, COL_RETAIL_PRICE, joinHerbalRetailPrices(product), style);
}

private void writeSelectedCell(Row row, List<String> columns, String columnName, Object value, CellStyle style) {
    int columnIndex = columns.indexOf(columnName);
    if (columnIndex >= 0) {
        writeCell(row, columnIndex + 1, value, style);
    }
}

private void writeCell(Row row, int columnIndex, Object value, CellStyle style) {
    Cell cell = row.createCell(columnIndex);
    setCellValue(cell, value);
    cell.setCellStyle(style);
}

private void setCellValue(Cell cell, Object value) {
    if (value == null) {
        cell.setCellValue("");
    } else if (value instanceof Number) {
        cell.setCellValue(((Number) value).doubleValue());
    } else if (value instanceof Boolean) {
        cell.setCellValue((Boolean) value);
    } else if (value instanceof Date) {
        cell.setCellValue((Date) value);
    } else {
        cell.setCellValue(String.valueOf(value));
    }
}

private String getRegistrationNumber(RegisterProductMaster product, boolean isNewRegistration) {
    if (isNewRegistration) {
        return product.getRegistrationNo();
    }
    RegisterProductMaster oldProduct = ejbFacade.getRecordById(product.getOldRegistrationData());
    return oldProduct != null ? oldProduct.getRegistrationNo() : "";
}

private String getHerbalCategory(RegisterProductMaster product) {
    return product.getHerbalSubSection() != null ? product.getHerbalSubSection().getDescEn() : "";
}

private String getGenericNameAndStrength(RegisterProductMaster product, boolean pharmaceuticalReport) {
    String value = pharmaceuticalReport ? getApiIngredients(product) : getProductIngredients(product);
    if (StringUtils.isBlank(value)) {
        value = getProductIngredients(product);
    }
    return wrap(value, 25);
}

private String getApiIngredients(RegisterProductMaster product) {
    List<String> values = new ArrayList<String>();
    for (APIMaster api : product.getApi()) {
        values.add(nullToBlank(api.getIngredients())
                + nullToBlank(api.getConcentration()) + " "
                + nullToBlank(api.getEquivalency()));
    }
    return join(values, " ");
}

private String getProductIngredients(RegisterProductMaster product) {
    List<String> values = new ArrayList<String>();
    for (ProductRegistrationIngredient ingredient : product.getIngredients()) {
        values.add(nullToBlank(ingredient.getIngredient())
                + nullToBlank(ingredient.getConcentration()));
    }
    return join(values, " ");
}

private String getApiManufacturers(RegisterProductMaster product) {
    List<String> values = new ArrayList<String>();
    for (APIMaster api : product.getApi()) {
        values.add(nullToBlank(api.getManufacturer()) + " " + nullToBlank(api.getManufacturer2()));
    }
    return join(values, " ");
}

private String getDosageForm(RegisterProductMaster product) {
    if (product.getDosageForm() == null) {
        return "";
    }
    return product.getDosageForm().getId() == 999
            ? product.getOthrDosageForm()
            : product.getDosageForm().getDescEn();
}

private String getPackageType(RegisterProductMaster product) {
    if (product.getPackageType() == null) {
        return "";
    }
    return product.getPackageType().getId() == 999
            ? product.getOthrPackageType()
            : product.getPackageType().getDescEn();
}

private String getMarketingCompany(RegisterProductMaster product) {
    return product.getMah() != null ? product.getMah().getNameEn() : "";
}

private String getMarketingCompanyCountry(RegisterProductMaster product) {
    return product.getMah() != null && product.getMah().getCountry() != null
            ? product.getMah().getCountry().getDescEn()
            : "";
}

private String getBulkManufacturers(RegisterProductMaster product) {
    List<String> values = new ArrayList<String>();
    if (product.getBulkManufacturers() == null) {
        return "";
    }

    for (BulkManufacturer bulkManufacturer : product.getBulkManufacturers()) {
        if (bulkManufacturer.getManufact() == null) {
            values.add(nullToBlank(bulkManufacturer.getManufacturer()) + " " + nullToBlank(bulkManufacturer.getAddress()));
        } else {
            values.add(nullToBlank(bulkManufacturer.getManufact().getNameEn()) + " " + nullToBlank(bulkManufacturer.getManufact().getAddress()));
        }
    }
    return join(values, " ");
}

private String getPrimaryPackagers(RegisterProductMaster product) {
    List<String> values = new ArrayList<String>();
    if (product.getPrimaryPackagers() == null) {
        return "";
    }

    for (PrimaryPackager packager : product.getPrimaryPackagers()) {
        if (packager.getManufacturer() == null) {
            values.add(nullToBlank(packager.getPackager()) + " " + nullToBlank(packager.getAddress()));
        } else {
            values.add(nullToBlank(packager.getManufacturer().getNameEn()) + " " + nullToBlank(packager.getManufacturer().getAddress()));
        }
    }
    return join(values, " ");
}

private String getSecondaryPackagers(RegisterProductMaster product) {
    List<String> values = new ArrayList<String>();
    if (product.getSecondaryPackagers() == null) {
        return "";
    }

    for (SecondaryPackager packager : product.getSecondaryPackagers()) {
        if (packager.getManufacturer() == null) {
            values.add(nullToBlank(packager.getPackager()) + " " + nullToBlank(packager.getAddress()));
        } else {
            values.add(nullToBlank(packager.getManufacturer().getNameEn()) + " " + nullToBlank(packager.getManufacturer().getAddress()));
        }
    }
    return join(values, " ");
}

private String getBatchReleasers(RegisterProductMaster product) {
    List<String> values = new ArrayList<String>();
    if (product.getBatchReleasers() != null && !product.getBatchReleasers().isEmpty()) {
        for (ProductBatchReleaser batchReleaser : product.getBatchReleasers()) {
            if (batchReleaser.getManufacturer() != null) {
                values.add(nullToBlank(batchReleaser.getManufacturer().getNameEn())
                        + " " + nullToBlank(batchReleaser.getManufacturer().getAddress()));
            }
        }
    } else if (product.getBatchReleaser() != null) {
        values.add(nullToBlank(product.getBatchReleaser().getNameEn())
                + " " + nullToBlank(product.getBatchReleaser().getAddress()));
    }
    return join(values, " ");
}

private String getManufacturerCountries(RegisterProductMaster product) {
    List<String> values = new ArrayList<String>();
    if (product.getBatchReleasers() != null && !product.getBatchReleasers().isEmpty()) {
        for (ProductBatchReleaser batchReleaser : product.getBatchReleasers()) {
            if (batchReleaser.getManufacturer() != null && batchReleaser.getManufacturer().getCountry() != null) {
                values.add(batchReleaser.getManufacturer().getCountry().getDescEn());
            }
        }
    } else if (product.getBatchReleaser() != null && product.getBatchReleaser().getCountry() != null) {
        values.add(product.getBatchReleaser().getCountry().getDescEn());
    }
    return join(values, ",");
}

private String getAgentName(RegisterProductMaster product) {
    return product.getAgent() != null && product.getAgent().getAgent() != null
            ? product.getAgent().getAgent().getCompanyNameEn()
            : "";
}

private String joinHerbalCifPrices(RegisterProductMaster product) {
    List<String> values = new ArrayList<String>();
    for (HerbalPriceMaster herbalPrice : product.getHerbalPriceMasters()) {
        values.add(nullToBlank(herbalPrice.getCifPrice()));
    }
    return join(values, ",");
}

private String joinHerbalWholesalePrices(RegisterProductMaster product) {
    List<String> values = new ArrayList<String>();
    for (HerbalPriceMaster herbalPrice : product.getHerbalPriceMasters()) {
        values.add(nullToBlank(herbalPrice.getWholeSalePrice()));
    }
    return join(values, ",");
}

private String joinHerbalRetailPrices(RegisterProductMaster product) {
    List<String> values = new ArrayList<String>();
    for (HerbalPriceMaster herbalPrice : product.getHerbalPriceMasters()) {
        values.add(nullToBlank(herbalPrice.getRetailPrice()));
    }
    return join(values, ",");
}

private String formatDate(Date value, DateFormat dateFormat) {
    return value != null ? dateFormat.format(value) : "";
}

private String wrap(String value, int maxLineLength) {
    String remaining = nullToBlank(value).trim();
    if (StringUtils.isBlank(remaining)) {
        return "";
    }

    StringBuilder wrapped = new StringBuilder();
    while (remaining.length() > maxLineLength) {
        int breakAt = findBreakPosition(remaining, maxLineLength);
        wrapped.append(remaining.substring(0, breakAt).trim()).append("\n");
        remaining = remaining.substring(breakAt).trim();
    }
    wrapped.append(remaining);
    return wrapped.toString();
}

private int findBreakPosition(String value, int preferredPosition) {
    int breakAt = preferredPosition;
    while (breakAt < value.length() && !StringUtils.isWhitespace(value.substring(breakAt, breakAt + 1))) {
        breakAt++;
    }
    return breakAt < value.length() ? breakAt : preferredPosition;
}

private String join(List<String> values, String separator) {
    StringBuilder joined = new StringBuilder();
    for (String value : values) {
        if (StringUtils.isBlank(value)) {
            continue;
        }
        if (joined.length() > 0) {
            joined.append(separator);
        }
        joined.append(value.trim());
    }
    return joined.toString();
}

private String nullToBlank(Object value) {
    return value != null ? String.valueOf(value) : "";
}

private boolean isPharmaceuticalReport() {
    return REQUEST_PHARMACEUTICAL.equalsIgnoreCase(language.getRequestType());
}

private void autoSizeColumns(HSSFSheet sheet, int columnCount) {
    for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
        sheet.autoSizeColumn(columnIndex);
    }
}

private StreamedContent buildDownload(HSSFWorkbook workbook) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);

    InputStream stream = new ByteArrayInputStream(out.toByteArray());
    String reportName = "Registration_report_"
            + language.getRequestType()
            + "_"
            + new SimpleDateFormat("dd-MM-yyyy").format(new Date())
            + ".xls";

    return new DefaultStreamedContent(
            stream,
            "application/vnd.ms-excel",
            reportName);
}
