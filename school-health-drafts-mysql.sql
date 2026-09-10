-- Run once before deploying the updated survey application.
ALTER TABLE school_health_response
  MODIFY submitted_on DATETIME NULL,
  ADD status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
  ADD resume_token VARCHAR(64) NULL,
  ADD response_locale VARCHAR(2) NOT NULL DEFAULT 'en',
  ADD version BIGINT NOT NULL DEFAULT 0,
  ADD CONSTRAINT uq_school_health_resume_token UNIQUE (resume_token);
