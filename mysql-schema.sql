CREATE DATABASE IF NOT EXISTS survey;
USE survey;

CREATE TABLE IF NOT EXISTS users (
  uname VARCHAR(100) PRIMARY KEY,
  password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS topics (
  topicid BIGINT AUTO_INCREMENT PRIMARY KEY,
  topictitle VARCHAR(255) NOT NULL,
  addedon DATETIME NOT NULL,
  uname VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS questions (
  questionid BIGINT AUTO_INCREMENT PRIMARY KEY,
  questiontext VARCHAR(1000) NOT NULL,
  opt1 VARCHAR(255) NOT NULL,
  opt2 VARCHAR(255) NOT NULL,
  opt3 VARCHAR(255) NOT NULL,
  topicid BIGINT NOT NULL,
  CONSTRAINT fk_questions_topic FOREIGN KEY (topicid) REFERENCES topics(topicid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS answers_master (
  surveyid BIGINT AUTO_INCREMENT PRIMARY KEY,
  topicid BIGINT NOT NULL,
  answered_on DATETIME NOT NULL,
  CONSTRAINT fk_answers_master_topic FOREIGN KEY (topicid) REFERENCES topics(topicid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS answers_details (
  surveyid BIGINT NOT NULL,
  questionid BIGINT NOT NULL,
  answer VARCHAR(10) NOT NULL,
  CONSTRAINT fk_answers_details_master FOREIGN KEY (surveyid) REFERENCES answers_master(surveyid) ON DELETE CASCADE,
  CONSTRAINT fk_answers_details_question FOREIGN KEY (questionid) REFERENCES questions(questionid) ON DELETE CASCADE
);

INSERT INTO users (uname, password)
VALUES ('admin', 'admin')
ON DUPLICATE KEY UPDATE password = VALUES(password);

-- Reset and seed sample survey content
DELETE FROM answers_details;
DELETE FROM answers_master;
DELETE FROM questions;
DELETE FROM topics WHERE topictitle IN ('Developer Experience Survey', 'Remote Work Survey');

INSERT INTO topics (topictitle, addedon, uname) VALUES
('Developer Experience Survey', NOW(), 'admin'),
('Remote Work Survey', NOW(), 'admin');

SET @dev_topic_id = (SELECT topicid FROM topics WHERE topictitle = 'Developer Experience Survey' ORDER BY topicid DESC LIMIT 1);
SET @remote_topic_id = (SELECT topicid FROM topics WHERE topictitle = 'Remote Work Survey' ORDER BY topicid DESC LIMIT 1);

INSERT INTO questions (questiontext, opt1, opt2, opt3, topicid) VALUES
('How satisfied are you with the current build process?', 'Very satisfied', 'Neutral', 'Needs improvement', @dev_topic_id),
('How clear are project requirements?', 'Very clear', 'Somewhat clear', 'Unclear', @dev_topic_id),
('How often do you work remotely?', 'Always', 'Sometimes', 'Never', @remote_topic_id),
('Do you have the tools needed for remote work?', 'Yes', 'Partly', 'No', @remote_topic_id);

INSERT INTO answers_master (topicid, answered_on) VALUES
(@dev_topic_id, NOW()),
(@remote_topic_id, NOW());

SET @dev_survey_id = (
  SELECT surveyid
  FROM answers_master
  WHERE topicid = @dev_topic_id
  ORDER BY surveyid DESC
  LIMIT 1
);

SET @remote_survey_id = (
  SELECT surveyid
  FROM answers_master
  WHERE topicid = @remote_topic_id
  ORDER BY surveyid DESC
  LIMIT 1
);

INSERT INTO answers_details (surveyid, questionid, answer)
SELECT @dev_survey_id, questionid,
  CASE
    WHEN questiontext LIKE 'How satisfied%' THEN '1'
    WHEN questiontext LIKE 'How clear%' THEN '2'
    ELSE '3'
  END
FROM questions
WHERE topicid = @dev_topic_id;

INSERT INTO answers_details (surveyid, questionid, answer)
SELECT @remote_survey_id, questionid,
  CASE
    WHEN questiontext LIKE 'How often%' THEN '2'
    WHEN questiontext LIKE 'Do you have%' THEN '1'
    ELSE '3'
  END
FROM questions
WHERE topicid = @remote_topic_id;

CREATE TABLE IF NOT EXISTS workplace_wellness_response (
  response_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  company_name VARCHAR(255),
  email VARCHAR(255),
  phone VARCHAR(100),
  submitted_on DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS workplace_wellness_answer (
  answer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  response_id BIGINT NOT NULL,
  section_name VARCHAR(150) NOT NULL,
  question_key VARCHAR(100) NOT NULL,
  question_label VARCHAR(500) NOT NULL,
  answer_value VARCHAR(2000) NOT NULL,
  display_order INT NOT NULL,
  CONSTRAINT fk_workplace_wellness_answer_response
    FOREIGN KEY (response_id) REFERENCES workplace_wellness_response(response_id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS school_health_response (
  response_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  institution_name VARCHAR(255),
  contact_email VARCHAR(255),
  contact_phone VARCHAR(100),
  submitted_on DATETIME NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
  resume_token VARCHAR(64) UNIQUE,
  response_locale VARCHAR(2) NOT NULL DEFAULT 'en',
  version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS school_health_answer (
  answer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  response_id BIGINT NOT NULL,
  section_name VARCHAR(150) NOT NULL,
  question_key VARCHAR(100) NOT NULL,
  question_label VARCHAR(500) NOT NULL,
  answer_value VARCHAR(4000) NOT NULL,
  display_order INT NOT NULL,
  CONSTRAINT fk_school_health_answer_response
    FOREIGN KEY (response_id) REFERENCES school_health_response(response_id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS school_health_question_text (
  question_key VARCHAR(100) NOT NULL,
  locale_code VARCHAR(10) NOT NULL,
  section_name VARCHAR(150) NOT NULL,
  question_label VARCHAR(500) NOT NULL,
  PRIMARY KEY (question_key, locale_code)
);

CREATE TABLE IF NOT EXISTS school_health_option_text (
  option_group VARCHAR(100) NOT NULL,
  option_value VARCHAR(100) NOT NULL,
  locale_code VARCHAR(10) NOT NULL,
  option_label VARCHAR(500) NOT NULL,
  display_order INT NOT NULL,
  PRIMARY KEY (option_group, option_value, locale_code)
);

CREATE TABLE IF NOT EXISTS SURVEY_DETAILS (
  ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  EVENT_UUID VARCHAR(100),
  SURVEY_HEADING_ID BIGINT,
  SURVEY_HEADING VARCHAR(255),
  SURVEY_QNS VARCHAR(1000),
  FIELD_TYPE VARCHAR(50),
  IS_MANDATORY BOOLEAN NOT NULL DEFAULT FALSE,
  FIELD_OPTION VARCHAR(100),
  REQUIRED_MESSAGE VARCHAR(500),
  PRE_SURVEY BOOLEAN NOT NULL DEFAULT FALSE,
  POST_SURVEY BOOLEAN NOT NULL DEFAULT FALSE,
  CORRECT_ANSWER_ID BIGINT,
  SURVEY_HEADING_AR VARCHAR(255),
  SURVEY_QNS_AR VARCHAR(1000),
  REQUIRED_MESSAGE_AR VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS OPTIONS (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  DESCRIPTION VARCHAR(500),
  DESCRIPTION_AR VARCHAR(500),
  EVENT_UUID VARCHAR(100),
  OPTION_TYPE VARCHAR(100)
);
