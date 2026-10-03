-- Oracle: replace both placeholder table names before running.
-- Run once. DDL commits automatically; existing rows keep NULL trigger values.
ALTER TABLE YOUR_EVENTREG_TABLE ADD (SHOW_WHEN_ACTION_ID NUMBER(10));
ALTER TABLE YOUR_EVENTREG_TABLE ADD CONSTRAINT FK_REG_SHOW_WHEN_ACTION
    FOREIGN KEY (SHOW_WHEN_ACTION_ID) REFERENCES YOUR_ACTION_TABLE (ID);
