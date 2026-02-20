USE survey;

INSERT INTO topics (topictitle, addedon, uname) VALUES
('Product Feedback Survey', NOW(), 'admin'),
('Training Needs Survey', NOW(), 'admin');

SET @product_topic_id = (SELECT topicid FROM topics WHERE topictitle = 'Product Feedback Survey' ORDER BY topicid DESC LIMIT 1);
SET @training_topic_id = (SELECT topicid FROM topics WHERE topictitle = 'Training Needs Survey' ORDER BY topicid DESC LIMIT 1);

INSERT INTO questions (questiontext, opt1, opt2, opt3, topicid) VALUES
('How would you rate overall product quality?', 'Excellent', 'Average', 'Poor', @product_topic_id),
('How likely are you to recommend the product?', 'Very likely', 'Maybe', 'Unlikely', @product_topic_id),
('Which training format do you prefer?', 'Live session', 'Recorded videos', 'Self-paced docs', @training_topic_id),
('How often do you need technical training?', 'Monthly', 'Quarterly', 'Yearly', @training_topic_id);

INSERT INTO answers_master (topicid, answered_on) VALUES
(@product_topic_id, NOW()),
(@training_topic_id, NOW());

SET @product_survey_id = (SELECT surveyid FROM answers_master WHERE topicid = @product_topic_id ORDER BY surveyid DESC LIMIT 1);
SET @training_survey_id = (SELECT surveyid FROM answers_master WHERE topicid = @training_topic_id ORDER BY surveyid DESC LIMIT 1);

INSERT INTO answers_details (surveyid, questionid, answer)
SELECT @product_survey_id, questionid,
  CASE
    WHEN questiontext LIKE 'How would you rate%' THEN '1'
    WHEN questiontext LIKE 'How likely are you%' THEN '2'
    ELSE '3'
  END
FROM questions
WHERE topicid = @product_topic_id;

INSERT INTO answers_details (surveyid, questionid, answer)
SELECT @training_survey_id, questionid,
  CASE
    WHEN questiontext LIKE 'Which training format%' THEN '3'
    WHEN questiontext LIKE 'How often do you need%' THEN '2'
    ELSE '1'
  END
FROM questions
WHERE topicid = @training_topic_id;
