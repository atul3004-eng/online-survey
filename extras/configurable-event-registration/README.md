# Configurable event registration integration

This folder contains a separate JSF.ConfigurableEventRegistrationController,
not a controller installed in the unrelated survey project. It needs the original
application's ejb entities, facades, utilities and dependencies to compile.
The page is event-registration.xhtml. There are no event ID or
answer-label comparisons in its branching rules.

## Install

1. Add ConfigurableEventRegistrationController.java to the original application under package JSF. Leave EventMasterController.java unchanged. The new bean name is configurableEventRegistrationController.
   controller-methods.inc is a reference extract, not another compiled class.
2. Add the entity relationship below and its database column/foreign key using
   your actual table names. Generate the entity getter and setter.
3. Copy the XHTML into your original application's web folder. Open
   /faces/event-registration.xhtml?uuid=<actual-event-uuid>&lang=en.
4. Optionally save event-page.properties.example under the application's classpath as
   /event-registration/<actual-event-uuid>.properties. Replace the instruction
   field ID placeholders. This file now contains only optional field instructions.
5. Install mental-health-registration.ftl under /templates on the classpath.
   Set EventMaster.register_email to mental-health-registration.ftl and configure
   its email sender and subject. This sample is English; configure Arabic text and
   a separate registeremailAr template if Arabic registration is required.
6. Configure the following EventRegData and Action records for the same event.
   Check the actual Action event getter: this copy assumes getEvent_uuid() returns
   EventMaster, as in the pasted entity. The new page explicitly uses configuredRegistrationActionConverter; its named registration does not replace the existing Action converter.

```java
@ManyToOne
@JoinColumn(name = "SHOW_WHEN_ACTION_ID", referencedColumnName = "ID")
private Action showWhenAction;

public Action getShowWhenAction() { return showWhenAction; }
public void setShowWhenAction(Action action) { showWhenAction = action; }
```

PARENT_FIELD identifies the parent question. SHOW_WHEN_ACTION_ID identifies the
option that reveals this field. Several children may share a parent and trigger.
Reject cycles, cross-event parents and actions not in the parent's option group
when saving configuration. Keep all field IDs and positions stable on postback.
The EventMaster-to-event UUID mappings in the provided entities also require proper
JPA relationship annotations if not already present in the original source.

## EventRegData records

All input fields below are mandatory. Provide MANDATORY_MESSAGE and its Arabic
counterpart for each; provide VALIDATIONMSG when a regex is configured.
Use your actual IDs in the relationships. Display-only notice is not mandatory.

| POSITION | FIELDNAME | FIELD_TYPE | ACTION group | Parent / trigger | FIELD_LENGTH | REGEX_PATTERN |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | Full Name | textbox | none | none | 200 | `(?s).*\S.*` |
| 2 | Email Address | textbox | none | none | 254 | `^[A-Za-z0-9.!#$%&'*+/=?^_\x60{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?(?:\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?)+$` |
| 3 | QID | textbox | none | none | 11 | `^[0-9]{11}$` |
| 4 | Institution/Organization | dropdown | institution group | none | not used | none |
| 5 | Please specify other organization | textbox | none | Institution field / Other action | 200 | `(?s).*\S.*` |
| 6 | Are you a licensed healthcare practitioner in Qatar? | radio | practitioner group | none | not used | none |
| 7 | Scope of Practice | dropdown | scope group | Practitioner field / Yes action | not used | none |
| 8 | License Number | textbox | none | Practitioner field / Yes action | 30 | `(?i)^(?:PH|P|N|D|A)[0-9]+$` |
| 9 | Profession / Role | textbox | none | Practitioner field / No action | 200 | `(?s).*\S.*` |
| 10 | To ensure your CPD credits are correctly allocated, please review your information carefully. If you notice any error in your License Number, QID, or Email Address, please correct it before submitting. | display | none | none | not used | none |
| 11 | I have verified that my License Number, QID, and Email Address are accurate. | booleancheckbox | none | none | not used | none |
| 12 | I understand that for my attendance to be verified, I must sign in to the Teams live session using the same email provided in this form. | booleancheckbox | none | none | not used | none |

Store regexes exactly as shown when entering them directly into the database;
escaping varies if inserting them through SQL or Java string literals. Suggested
validation messages: "Enter a valid email address", "QID must contain exactly 11
digits", and "Enter a full license number such as P1234, without spaces".
The QID is stored as text to preserve leading zeros. The email expression checks
common email syntax; it does not prove ownership or that Teams uses that account.

## Action records

Create all options with the current EventMaster in EVENT_UUID and use the matching
ACTION_TYPE group from the field's ACTION setting:

- Practitioner group: Yes, No.
- Scope group: Physician, Pharmacist, Nurse, Dentist, Allied Health Professional.
- Institution group: your approved system organization list, plus Other.

Populate ACTION_VALUES and ACTION_VALUES_AR. Values shown in the XHTML come from
these records. No Action IDs are assumed. If an institution list is unavailable,
change Institution/Organization to textbox and remove the Other child record.

The full-license fallback is implemented: users enter P1234, PH1234, N1234, D1234,
or A1234. There is no automatic scope-specific prefix or prefix-to-scope check.

## Controller changes

- Loads event/page settings by UUID and fields by existing facade, ordered by
  POSITION then ID. Do not reload fields during postback.
- Filters Action choices by event UUID and action_type.
- Replaces the fixed Profession/270/Other-role validation in saveValues with
  per-field validation before opening a transaction.
- Resolves dependencies from the current form's transient answer objects.
- Updates only descendants of the changed field so unrelated typed answers survive.
- Clears hidden answers and skips hidden/display fields during persistence.
- Validates configured text patterns server-side. Requires mandatory checkboxes to
  be true, using a Boolean map bridge to the existing String fieldValue. Persists
  each acknowledgement as true/false through the existing values table.
- Recognizes License and Licence for the existing confirmation email data.
- Uses selected organization labels for the existing email data.
- Tracks successful email transmission. Shows the requested confirmation only
  after saving and successful sendEmail(). A post-commit mail failure preserves
  the saved state and prevents a second insert from the same view.

This copy retains the original registration limits and survey logic. Apply it to
this registration workflow and review any additional field types used elsewhere;
this page supports text, textarea, radio, dropdown/listbox, calendar, checkbox and
display. Upload fields and their original workflow are not included in this page.
Existing duplicate-email checks remain. Database-level uniqueness is needed if
simultaneous registrations must never create duplicates.

## Verify in the original application

- Correct event header and Action options are loaded for two different event UUIDs.
- Missing Full Name, invalid email, and QIDs with 10/12 digits or letters are rejected.
- Institution Other reveals its required textbox; switching away clears its answer.
- Yes reveals Scope and License; No reveals only Profession / Role.
- Switching the practitioner answer does not erase an unrelated organization answer.
- Spaces or missing prefix in a license are rejected; PH1234 is accepted.
- Each unchecked mandatory acknowledgement prevents saving, including crafted requests.
- Hidden answers are not saved; accepted acknowledgement values are saved.
- Successful save sends an email with the actual Teams link and shows confirmation.
- Email failure after commit reports saved registration without claiming mail was sent.

Only XML syntax and source transformation checks were possible here. Compilation,
JSF rendering and transaction/email tests require the original application.

## Isolation from the existing form

The new Java class and bean name are separate. The new converter names are unique
and are explicitly bound in the new page. No existing Java class or deployed XHTML
was changed here. The copy includes the supplied controller's existing supporting
methods so it does not depend on inheritance from EventMasterController. The new mail method requires a configured email template before claiming email success. Joining details use the original configured meeting-link data.
The EventRegData relationship/database migration is shared and additive: existing
rows may keep SHOW_WHEN_ACTION_ID null and the old form continues using its old logic.
The new event's records and classpath properties must be configured separately.

## Oracle SQL scripts

Run 01-add-trigger-column-oracle.sql once, then
02-seed-mental-health-event-oracle.sql using SQL Developer Run Script (F5) or
SQL*Plus. Confirm the DEFINE table names (EVENTREGDATA, ACTION, EVENTMASTER) against
your actual @Table mappings. The script prompts for the existing event UUID and
looks up EVENT_ID. Sequences ACTIONSEQ and EVENTREGPAGESEQ must already exist and
be ahead of existing IDs. IS_MANDATORY is assumed NUMBER(1) with 0/1 values.

The seed inserts 8 Action options and 12 fields, linking both Yes children, the No
profession field, and Other organization using generated IDs. It refuses an event
that already has fields or these Action groups. No existing field records are
updated or deleted. Add approved organizations in the marked section; the supplied
organization group contains only Other until you add those options.

Ensure FIELDNAME and FIELDNAME_AR can hold the notice and acknowledgement text
(use a sufficient length, such as 1000 characters); adjust column sizes if required. Arabic
Action labels are included; field labels/messages currently mirror English and
can be localized. The script prints the generated Email/License instruction keys
for the event properties file. Review inserts and manually COMMIT or ROLLBACK.
Oracle ALTER TABLE commits independently of the insert transaction.

SQL was generated and its option/field counts checked; it has not been executed
against your database. Event header, Teams link and email template remain in the
properties/template configuration described above.

## Existing event metadata (updated)

No separate event-header properties file is required. The page title/heading uses
EventMaster.eventTitle/eventTitleAr. The combined Date & Time uses the same
RegistrationLimit.eventstartDate/eventEndDate (and Arabic equivalents) already
used in the supplied page and existing FTL map. It is displayed intact: no date/time
splitting and no new columns. The loader fetches RegistrationLimit by event UUID.

The original FTL map keys EventName, EventSalutation, startDay, startDayRange,
webinar and day1/day2/day3 remain available. The example template additionally
uses dateTime and joiningUrl (the existing RegistrationLimit.eventDay1 URL).
The separate properties file is optional and contains field instructions only.

Platform uses the existing day1 meeting URL (RegistrationLimit.eventDay1) as
confirmed. The page and sample email show a Platform joining link from that value.
No platform name, meeting URL, event date/time or event ID is hard-coded. The
original HTML-valued day1 FTL parameter remains available for existing templates;
the sample uses joiningUrl so the URL is escaped by FreeMarker. Additional welcome
and session-title fields need the actual entity getters if separate from eventTitle.