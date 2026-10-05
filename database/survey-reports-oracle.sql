-- Run in the application's Oracle schema. Read-only reports.
-- One row per answer; response/contact details repeat for each question.
-- ANSWER_VALUE is returned intact, including CLOB values.

-- School Health: completed submissions only (drafts excluded).
-- Requires the current draft-support columns STATUS and RESPONSE_LOCALE.
SELECT r.response_id,
       r.submitted_on,
       r.institution_name,
       r.contact_email,
       r.contact_phone,
       r.response_locale,
       a.section_name,
       a.question_key,
       a.question_label,
       a.answer_value
FROM school_health_response r
LEFT JOIN school_health_answer a ON a.response_id = r.response_id
WHERE r.status = 'SUBMITTED'
ORDER BY r.response_id, a.display_order, a.answer_id;

-- Workplace Wellness: all saved submissions (no draft status column).
SELECT r.response_id,
       r.submitted_on,
       r.company_name,
       r.email,
       r.phone,
       a.section_name,
       a.question_key,
       a.question_label,
       a.answer_value
FROM workplace_wellness_response r
LEFT JOIN workplace_wellness_answer a ON a.response_id = r.response_id
ORDER BY r.response_id, a.display_order, a.answer_id;
