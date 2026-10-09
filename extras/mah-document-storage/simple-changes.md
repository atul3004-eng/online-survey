In all five upload methods (handleFileUpload, handleFileResubmitUpload,
handleGlobalFileUpload, handleVariationFileUpload, handleReregistrationFileUpload):

1. Remove the temporary mahFolder generation block.
2. Replace strFullPath with:

```java
String strFullPath = "/dps/mah/allDocuments";
Files.createDirectories(Paths.get(strFullPath));
```

3. Keep the original filename for display, but use a unique storage filename:

```java
String filename = FilenameUtils.getName(uploadedPhoto.getFileName());
String filePath = strFullPath + "/" + UUID.randomUUID().toString() + "_" + filename;
try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(filePath))) {
    stream.write(uploadedPhoto.getContents());
}
file.setDocumentName(filename);
file.setDocumentPath(filePath);
```

Replace the existing byte-write block with this block; do not write twice.
Leave documentList.add(file), document types and UI updates in place.

On submit, replace:

```java
if (StringUtils.isNotBlank(mahFolder) && !documentList.isEmpty()) {
```

with:

```java
if (documentList != null && !documentList.isEmpty()) {
```

Remove calls to renameMahFolder, moveFolder, moveUpdatedFiles,
moveDocumentToMahDirectory and moveUpdatedDocumentsToMAHDirectory.
Remove their unused method bodies, including copy/move/delete operations.
Remove the empty mahFolder condition around moveFolder.

Return the saved path unchanged:

```java
public String getDocumentPath(String path) {
    return path;
}
```

Keep existing explicit Remove/delete actions and rename behavior unchanged.
Disable only submission relocation and its temporary-folder cleanup. The updated
copy-paste-methods.txt preserves the supplied explicit removal methods verbatim.
Those original methods contain hard-coded D: paths; their behavior is preserved.

The existing upload XHTML listeners can stay as supplied. The view/download
must read document.documentPath directly, without reconstructing mah/<id>/ paths.
The actual view/download implementation was not included in the supplied markup.
Keep older saved paths unchanged; this change applies to new uploads.

This is an integration guide for the supplied controller, not a compiled project
change. The supplied file omits imports and belongs to a separate DPS application.
