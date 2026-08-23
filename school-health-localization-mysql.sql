USE survey;

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

DELETE FROM SURVEY_DETAILS
WHERE FIELD_OPTION IN (
  'institutionName','institutionType','institutionTypeOther','department','contactName','contactTitle',
  'contactEmail','contactPhone','programTitle','programDescription','currentStatus','statusDetail',
  'targetedTopics','targetedTopicOther','targetPopulation','targetPopulationOther','primaryGrades',
  'preparatoryGrades','secondaryGrades','coverage','schoolsCovered','coverageInfo','performanceIndicators',
  'performanceIndicatorsDescription','partnersInvolved','partnersSpecification','partnerNames','partnerRoles',
  'coordinationMechanism','coordinationStructure','documentsDeveloped','documentTitlesYears','humanResources',
  'humanResourceOther','capacityBuilding','trainingFrequency','trainingFrequencyOther','infrastructureSupport',
  'infrastructureOther','programFeedback','nationalContribution'
);

INSERT INTO SURVEY_DETAILS
(SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR)
VALUES
('General Information','Institution name','text',TRUE,'institutionName','Institution name is required.',TRUE,FALSE,'المعلومات العامة','اسم المؤسسة','اسم المؤسسة مطلوب.'),
('General Information','Type of institution','radio',TRUE,'institutionType','Type of institution is required.',TRUE,FALSE,'المعلومات العامة','نوع المؤسسة','نوع المؤسسة مطلوب.'),
('General Information','Other institution type','text',FALSE,'institutionTypeOther','Other institution type is required.',TRUE,FALSE,'المعلومات العامة','خيارات إضافية','يرجى تحديد الخيار الإضافي.'),
('General Information','Department / section','text',TRUE,'department','Department / section is required.',TRUE,FALSE,'المعلومات العامة','الإدارة / القسم','الإدارة / القسم مطلوب.'),
('General Information','Contact person name','text',TRUE,'contactName','Contact person name is required.',TRUE,FALSE,'المعلومات العامة','اسم جهة الاتصال','اسم جهة الاتصال مطلوب.'),
('General Information','Contact person title','text',TRUE,'contactTitle','Contact person title is required.',TRUE,FALSE,'المعلومات العامة','المسمى الوظيفي','المسمى الوظيفي مطلوب.'),
('General Information','Email','text',TRUE,'contactEmail','Email is required.',TRUE,FALSE,'المعلومات العامة','البريد الإلكتروني','البريد الإلكتروني مطلوب.'),
('General Information','Phone number','text',TRUE,'contactPhone','Phone number is required.',TRUE,FALSE,'المعلومات العامة','رقم الهاتف','رقم الهاتف مطلوب.'),
('Program / Initiative / Activity','Program / initiative / activity title','text',TRUE,'programTitle','Program title is required.',FALSE,FALSE,'البرنامج / المبادرة / النشاط','اسم البرنامج / المبادرة / النشاط','اسم البرنامج / المبادرة / النشاط مطلوب.'),
('Program / Initiative / Activity','Brief description of the program / initiative / activity','textarea',TRUE,'programDescription','Brief description is required.',FALSE,FALSE,'البرنامج / المبادرة / النشاط','وصف مختصر للبرنامج / المبادرة / النشاط','الوصف المختصر مطلوب.'),
('Program / Initiative / Activity','Current status','radio',TRUE,'currentStatus','Current status is required.',FALSE,FALSE,'البرنامج / المبادرة / النشاط','الوضع الراهن','الوضع الراهن مطلوب.'),
('Program / Initiative / Activity','Start year, planned start date, or other status detail','text',TRUE,'statusDetail','Status detail is required.',FALSE,FALSE,'البرنامج / المبادرة / النشاط','سنة البدء أو تاريخ البدء المتوقع أو تفاصيل أخرى','تفاصيل الوضع الراهن مطلوبة.'),
('Program / Initiative / Activity','Targeted topics','checkbox',TRUE,'targetedTopics','Select at least one targeted topic.',FALSE,FALSE,'البرنامج / المبادرة / النشاط','المواضيع المستهدفة','يرجى اختيار موضوع واحد على الأقل.'),
('Program / Initiative / Activity','Other targeted topic','text',FALSE,'targetedTopicOther','Other targeted topic is required.',FALSE,FALSE,'البرنامج / المبادرة / النشاط','خيارات إضافية للمواضيع المستهدفة','يرجى تحديد الخيار الإضافي.'),
('Program / Initiative / Activity','Target population','checkbox',TRUE,'targetPopulation','Select at least one target population.',FALSE,FALSE,'البرنامج / المبادرة / النشاط','الفئة المستهدفة','يرجى اختيار فئة واحدة على الأقل.'),
('Program / Initiative / Activity','Other target population','text',FALSE,'targetPopulationOther','Other target population is required.',FALSE,FALSE,'البرنامج / المبادرة / النشاط','خيارات إضافية للفئة المستهدفة','يرجى تحديد الخيار الإضافي.'),
('Program / Initiative / Activity','Primary','checkbox',FALSE,'primaryGrades',NULL,FALSE,FALSE,'البرنامج / المبادرة / النشاط','ابتدائي',NULL),
('Program / Initiative / Activity','Preparatory','checkbox',FALSE,'preparatoryGrades',NULL,FALSE,FALSE,'البرنامج / المبادرة / النشاط','إعدادي',NULL),
('Program / Initiative / Activity','Secondary','checkbox',FALSE,'secondaryGrades',NULL,FALSE,FALSE,'البرنامج / المبادرة / النشاط','ثانوي',NULL),
('Coverage and Monitoring','Coverage','radio',TRUE,'coverage','Coverage is required.',FALSE,FALSE,'نطاق التغطية والمتابعة','نطاق التغطية','نطاق التغطية مطلوب.'),
('Coverage and Monitoring','Number of schools covered','text',TRUE,'schoolsCovered','Number of schools covered is required.',FALSE,FALSE,'نطاق التغطية والمتابعة','عدد المدارس المغطاة','عدد المدارس المغطاة مطلوب.'),
('Coverage and Monitoring','Any other information related to coverage scope','textarea',FALSE,'coverageInfo',NULL,FALSE,FALSE,'نطاق التغطية والمتابعة','أي إضافات أخرى متعلقة بنطاق التغطية',NULL),
('Coverage and Monitoring','Does your institution have performance indicators to monitor and evaluate this program, initiative, or activity?','radio',TRUE,'performanceIndicators','Performance indicators status is required.',FALSE,FALSE,'نطاق التغطية والمتابعة','هل لدى مؤسستكم مؤشرات أداء لمتابعة وتقييم هذه البرامج؟','يرجى اختيار إجابة.'),
('Coverage and Monitoring','If yes, please briefly describe','textarea',FALSE,'performanceIndicatorsDescription',NULL,FALSE,FALSE,'نطاق التغطية والمتابعة','إذا كانت الإجابة نعم، يرجى الوصف بإيجاز',NULL),
('Governance, Coordination, and Partnerships','Are there any partners involved in implementation?','radio',TRUE,'partnersInvolved','Partners involved is required.',FALSE,FALSE,'الحوكمة والتنسيق والشراكات','هل توجد جهات شريكة في التنفيذ؟','يرجى اختيار إجابة.'),
('Governance, Coordination, and Partnerships','If yes, please specify','textarea',FALSE,'partnersSpecification',NULL,FALSE,FALSE,'الحوكمة والتنسيق والشراكات','إذا كانت الإجابة نعم، يرجى التحديد',NULL),
('Governance, Coordination, and Partnerships','Partner(s) name','textarea',FALSE,'partnerNames',NULL,FALSE,FALSE,'الحوكمة والتنسيق والشراكات','اسم الشريك / الشركاء',NULL),
('Governance, Coordination, and Partnerships','Partner(s) role','textarea',FALSE,'partnerRoles',NULL,FALSE,FALSE,'الحوكمة والتنسيق والشراكات','دور الشريك / الشركاء',NULL),
('Governance, Coordination, and Partnerships','Is there an established coordination mechanism?','radio',TRUE,'coordinationMechanism','Coordination mechanism is required.',FALSE,FALSE,'الحوكمة والتنسيق والشراكات','هل توجد آلية تنسيق قائمة؟','يرجى اختيار إجابة.'),
('Governance, Coordination, and Partnerships','If yes, provide the name and structure','textarea',FALSE,'coordinationStructure',NULL,FALSE,FALSE,'الحوكمة والتنسيق والشراكات','يرجى ذكر اسم الجهة وهيكلها التنظيمي',NULL),
('Policies, Guidelines, and Documents','Has your institution developed or contributed to policies, strategies, guidelines, manuals, training materials, or other documents supporting this activity?','radio',TRUE,'documentsDeveloped','Document status is required.',FALSE,FALSE,'السياسات والإرشادات والوثائق','هل قامت مؤسستكم بإعداد أو المساهمة في تطوير وثائق داعمة لهذا النشاط؟','يرجى اختيار إجابة.'),
('Policies, Guidelines, and Documents','If yes, provide titles and publication year(s)','textarea',FALSE,'documentTitlesYears',NULL,FALSE,FALSE,'السياسات والإرشادات والوثائق','يرجى ذكر عناوين وسنوات إصدار الوثائق',NULL),
('Resources and Capacity','Dedicated human resources','checkbox',TRUE,'humanResources','Select at least one human resource.',FALSE,FALSE,'الموارد والقدرات','الموارد البشرية المخصصة','يرجى اختيار مورد واحد على الأقل.'),
('Resources and Capacity','Other human resource','text',FALSE,'humanResourceOther','Other human resource is required.',FALSE,FALSE,'الموارد والقدرات','خيارات إضافية للموارد البشرية','يرجى تحديد الخيار الإضافي.'),
('Resources and Capacity','Is capacity-building or training conducted for the personnel selected above?','radio',TRUE,'capacityBuilding','Capacity-building or training is required.',FALSE,FALSE,'الموارد والقدرات','هل يتم تنفيذ أنشطة لبناء القدرات أو تدريب للكوادر المحددة أعلاه؟','يرجى اختيار إجابة.'),
('Resources and Capacity','Training frequency','radio',TRUE,'trainingFrequency','Training frequency is required.',FALSE,FALSE,'الموارد والقدرات','عدد مرات التدريب','يرجى اختيار إجابة.'),
('Resources and Capacity','Other training frequency','text',FALSE,'trainingFrequencyOther','Other training frequency is required.',FALSE,FALSE,'الموارد والقدرات','خيارات إضافية لعدد مرات التدريب','يرجى تحديد الخيار الإضافي.'),
('Resources and Capacity','Infrastructure support available','checkbox',TRUE,'infrastructureSupport','Select at least one infrastructure support.',FALSE,FALSE,'الموارد والقدرات','البنية التحتية الداعمة المتوفرة','يرجى اختيار خيار واحد على الأقل.'),
('Resources and Capacity','Other infrastructure support','text',FALSE,'infrastructureOther','Other infrastructure support is required.',FALSE,FALSE,'الموارد والقدرات','خيارات إضافية للبنية التحتية','يرجى تحديد الخيار الإضافي.'),
('Feedback / Recommendations','Feedback or recommendations to enhance current school health promoting programs','textarea',TRUE,'programFeedback','Feedback or recommendations is required.',FALSE,TRUE,'الملاحظات / التوصيات','ملاحظات أو توصيات لتعزيز برامج تعزيز الصحة في المدارس الحالية','الملاحظات أو التوصيات مطلوبة.'),
('Feedback / Recommendations','How can your institution contribute to the national initiative of Health-Promoting Schools?','textarea',TRUE,'nationalContribution','Contribution is required.',FALSE,TRUE,'الملاحظات / التوصيات','كيف يمكن لمؤسستكم المساهمة في المبادرة الوطنية للمدارس المعززة للصحة؟','الإجابة مطلوبة.');

DELETE FROM OPTIONS
WHERE OPTION_TYPE IN (
  'institutionTypes','statusOptions','targetedTopics','targetPopulations','primaryGrades',
  'preparatoryGrades','secondaryGrades','coverageOptions','indicatorOptions','yesNoOptions',
  'documentStatusOptions','humanResources','trainingFrequencies','infrastructureSupports'
);

INSERT INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES
('Government','حكومية','institutionTypes'),('Semi-Governmental','شبه حكومية','institutionTypes'),('Private','خاصة','institutionTypes'),('NGO','منظمة غير حكومية','institutionTypes'),('Other','خيارات إضافية','institutionTypes'),
('Ongoing','جارٍ التنفيذ','statusOptions'),('Planned','مخطط له','statusOptions'),('One-time or repeated activity','مبادرة أو نشاط لمرة واحدة أو عدة مرات','statusOptions'),('Other','خيارات أخرى','statusOptions'),
('Tobacco and nicotine product control','مكافحة منتجات التبغ والنيكوتين','targetedTopics'),('Nutrition','التغذية','targetedTopics'),('Physical Activity','النشاط البدني','targetedTopics'),('Non-Communicable Diseases (chronic)','الأمراض غير الانتقالية (المزمنة)','targetedTopics'),('Mental Health','الصحة النفسية','targetedTopics'),('Oral Health','صحة الفم والأسنان','targetedTopics'),('Communicable Diseases','الأمراض الانتقالية','targetedTopics'),('Vaccination','التطعيمات','targetedTopics'),('Environmental health','صحة البيئة','targetedTopics'),('Safety','السلامة','targetedTopics'),('Other','خيارات إضافية','targetedTopics'),
('Students','الطلاب','targetPopulations'),('Teachers','المعلمون','targetPopulations'),('Parents','أولياء الأمور','targetPopulations'),('Other','خيارات إضافية','targetPopulations'),
('All primary grade levels','كل المرحلة الابتدائية','primaryGrades'),('Grade 1','الصف الأول','primaryGrades'),('Grade 2','الصف الثاني','primaryGrades'),('Grade 3','الصف الثالث','primaryGrades'),('Grade 4','الصف الرابع','primaryGrades'),('Grade 5','الصف الخامس','primaryGrades'),('Grade 6','الصف السادس','primaryGrades'),
('All preparatory grade levels','كل المرحلة الإعدادية','preparatoryGrades'),('Grade 7','الأول إعدادي (السابع)','preparatoryGrades'),('Grade 8','الثاني إعدادي (الثامن)','preparatoryGrades'),('Grade 9','الثالث إعدادي (التاسع)','preparatoryGrades'),
('All secondary grade levels','كل المرحلة الثانوية','secondaryGrades'),('Grade 10','الأول ثانوي (العاشر)','secondaryGrades'),('Grade 11','الثاني ثانوي (الحادي عشر)','secondaryGrades'),('Grade 12','الثالث ثانوي (الثاني عشر)','secondaryGrades'),('Grade 13','الثالث عشر','secondaryGrades'),
('National (all government and private schools)','وطني (جميع المدارس الحكومية والخاصة)','coverageOptions'),('All government schools only','جميع المدارس الحكومية فقط','coverageOptions'),('All private schools only','جميع المدارس الخاصة فقط','coverageOptions'),('Selected government schools only','بعض المدارس الحكومية فقط','coverageOptions'),('Selected private schools only','بعض المدارس الخاصة فقط','coverageOptions'),('Selected government and private schools','بعض المدارس الحكومية والخاصة','coverageOptions'),
('Yes','نعم','indicatorOptions'),('No','لا','indicatorOptions'),('Under Development','قيد التطوير','indicatorOptions'),
('Yes','نعم','yesNoOptions'),('No','لا','yesNoOptions'),
('Yes','نعم','documentStatusOptions'),('No','لا','documentStatusOptions'),('Planned','مخطط لها','documentStatusOptions'),
('Nurses','الممرضون','humanResources'),('Health Educators','المثقفون الصحيون','humanResources'),('Social workers','الأخصائيون الاجتماعيون','humanResources'),('Canteen supervisors','مشرفو التغذية','humanResources'),('Coordinators','المنسقون','humanResources'),('Other','خيارات إضافية','humanResources'),
('Monthly','شهري','trainingFrequencies'),('Quarterly','ربع سنوي','trainingFrequencies'),('Biannually','نصف سنوي','trainingFrequencies'),('Annually','سنوي','trainingFrequencies'),('Other','حسب الحاجة / خيارات أخرى','trainingFrequencies'),
('School Clinics','العيادات المدرسية','infrastructureSupports'),('Mobile Units','الوحدات المتنقلة','infrastructureSupports'),('Digital Platforms','المنصات الرقمية','infrastructureSupports'),('Training Centers','مراكز التدريب','infrastructureSupports'),('Other','خيارات إضافية','infrastructureSupports');
