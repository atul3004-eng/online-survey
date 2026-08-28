-- Oracle seed data for School Health survey labels and options.
-- Run this in the Oracle schema used by the application datasource.

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

INSERT ALL
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('General Information','Institution name','text',1,'institutionName','Institution name is required.',1,0,'المعلومات العامة','اسم المؤسسة','اسم المؤسسة مطلوب.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('General Information','Type of institution','radio',1,'institutionType','Type of institution is required.',1,0,'المعلومات العامة','نوع المؤسسة','نوع المؤسسة مطلوب.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('General Information','Other institution type','text',0,'institutionTypeOther','Other institution type is required.',1,0,'المعلومات العامة','خيارات إضافية','يرجى تحديد الخيار الإضافي.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('General Information','Department / section','text',1,'department','Department / section is required.',1,0,'المعلومات العامة','الإدارة / القسم','الإدارة / القسم مطلوب.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('General Information','Contact person name','text',1,'contactName','Contact person name is required.',1,0,'المعلومات العامة','اسم جهة الاتصال','اسم جهة الاتصال مطلوب.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('General Information','Contact person title','text',1,'contactTitle','Contact person title is required.',1,0,'المعلومات العامة','المسمى الوظيفي','المسمى الوظيفي مطلوب.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('General Information','Email','text',1,'contactEmail','Email is required.',1,0,'المعلومات العامة','البريد الإلكتروني','البريد الإلكتروني مطلوب.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('General Information','Phone number','text',1,'contactPhone','Phone number is required.',1,0,'المعلومات العامة','رقم الهاتف','رقم الهاتف مطلوب.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Program / Initiative / Activity','Program / initiative / activity title','text',1,'programTitle','Program title is required.',0,0,'البرنامج / المبادرة / النشاط','اسم البرنامج / المبادرة / النشاط','اسم البرنامج / المبادرة / النشاط مطلوب.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Program / Initiative / Activity','Brief description of the program / initiative / activity','textarea',1,'programDescription','Brief description is required.',0,0,'البرنامج / المبادرة / النشاط','وصف مختصر للبرنامج / المبادرة / النشاط','الوصف المختصر مطلوب.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Program / Initiative / Activity','Current status','radio',1,'currentStatus','Current status is required.',0,0,'البرنامج / المبادرة / النشاط','الوضع الراهن','الوضع الراهن مطلوب.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Program / Initiative / Activity','Start year, planned start date, or other status detail','text',1,'statusDetail','Status detail is required.',0,0,'البرنامج / المبادرة / النشاط','سنة البدء أو تاريخ البدء المتوقع أو تفاصيل أخرى','تفاصيل الوضع الراهن مطلوبة.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Program / Initiative / Activity','Targeted topics','checkbox',1,'targetedTopics','Select at least one targeted topic.',0,0,'البرنامج / المبادرة / النشاط','المواضيع المستهدفة','يرجى اختيار موضوع واحد على الأقل.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Program / Initiative / Activity','Other targeted topic','text',0,'targetedTopicOther','Other targeted topic is required.',0,0,'البرنامج / المبادرة / النشاط','خيارات إضافية للمواضيع المستهدفة','يرجى تحديد الخيار الإضافي.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Program / Initiative / Activity','Target population','checkbox',1,'targetPopulation','Select at least one target population.',0,0,'البرنامج / المبادرة / النشاط','الفئة المستهدفة','يرجى اختيار فئة واحدة على الأقل.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Program / Initiative / Activity','Other target population','text',0,'targetPopulationOther','Other target population is required.',0,0,'البرنامج / المبادرة / النشاط','خيارات إضافية للفئة المستهدفة','يرجى تحديد الخيار الإضافي.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Program / Initiative / Activity','Primary','checkbox',0,'primaryGrades',NULL,0,0,'البرنامج / المبادرة / النشاط','ابتدائي',NULL)
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Program / Initiative / Activity','Preparatory','checkbox',0,'preparatoryGrades',NULL,0,0,'البرنامج / المبادرة / النشاط','إعدادي',NULL)
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Program / Initiative / Activity','Secondary','checkbox',0,'secondaryGrades',NULL,0,0,'البرنامج / المبادرة / النشاط','ثانوي',NULL)
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Coverage and Monitoring','Coverage','radio',1,'coverage','Coverage is required.',0,0,'نطاق التغطية والمتابعة','نطاق التغطية','نطاق التغطية مطلوب.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Coverage and Monitoring','Number of schools covered','text',1,'schoolsCovered','Number of schools covered is required.',0,0,'نطاق التغطية والمتابعة','عدد المدارس المغطاة','عدد المدارس المغطاة مطلوب.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Coverage and Monitoring','Any other information related to coverage scope','textarea',0,'coverageInfo',NULL,0,0,'نطاق التغطية والمتابعة','أي إضافات أخرى متعلقة بنطاق التغطية',NULL)
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Coverage and Monitoring','Does your institution have performance indicators to monitor and evaluate this program, initiative, or activity?','radio',1,'performanceIndicators','Performance indicators status is required.',0,0,'نطاق التغطية والمتابعة','هل لدى مؤسستكم مؤشرات أداء لمتابعة وتقييم هذه البرامج؟','يرجى اختيار إجابة.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Coverage and Monitoring','If yes, please briefly describe','textarea',0,'performanceIndicatorsDescription',NULL,0,0,'نطاق التغطية والمتابعة','إذا كانت الإجابة نعم، يرجى الوصف بإيجاز',NULL)
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Governance, Coordination, and Partnerships','Are there any partners involved in implementation?','radio',1,'partnersInvolved','Partners involved is required.',0,0,'الحوكمة والتنسيق والشراكات','هل توجد جهات شريكة في التنفيذ؟','يرجى اختيار إجابة.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Governance, Coordination, and Partnerships','If yes, please specify','textarea',0,'partnersSpecification',NULL,0,0,'الحوكمة والتنسيق والشراكات','إذا كانت الإجابة نعم، يرجى التحديد',NULL)
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Governance, Coordination, and Partnerships','Partner(s) name','textarea',0,'partnerNames',NULL,0,0,'الحوكمة والتنسيق والشراكات','اسم الشريك / الشركاء',NULL)
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Governance, Coordination, and Partnerships','Partner(s) role','textarea',0,'partnerRoles',NULL,0,0,'الحوكمة والتنسيق والشراكات','دور الشريك / الشركاء',NULL)
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Governance, Coordination, and Partnerships','Is there an established coordination mechanism?','radio',1,'coordinationMechanism','Coordination mechanism is required.',0,0,'الحوكمة والتنسيق والشراكات','هل توجد آلية تنسيق قائمة؟','يرجى اختيار إجابة.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Governance, Coordination, and Partnerships','If yes, provide the name and structure','textarea',0,'coordinationStructure',NULL,0,0,'الحوكمة والتنسيق والشراكات','يرجى ذكر اسم الجهة وهيكلها التنظيمي',NULL)
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Policies, Guidelines, and Documents','Has your institution developed or contributed to policies, strategies, guidelines, manuals, training materials, or other documents supporting this activity?','radio',1,'documentsDeveloped','Document status is required.',0,0,'السياسات والإرشادات والوثائق','هل قامت مؤسستكم بإعداد أو المساهمة في تطوير وثائق داعمة لهذا النشاط؟','يرجى اختيار إجابة.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Policies, Guidelines, and Documents','If yes, provide titles and publication year(s)','textarea',0,'documentTitlesYears',NULL,0,0,'السياسات والإرشادات والوثائق','يرجى ذكر عناوين وسنوات إصدار الوثائق',NULL)
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Resources and Capacity','Dedicated human resources','checkbox',1,'humanResources','Select at least one human resource.',0,0,'الموارد والقدرات','الموارد البشرية المخصصة','يرجى اختيار مورد واحد على الأقل.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Resources and Capacity','Other human resource','text',0,'humanResourceOther','Other human resource is required.',0,0,'الموارد والقدرات','خيارات إضافية للموارد البشرية','يرجى تحديد الخيار الإضافي.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Resources and Capacity','Is capacity-building or training conducted for the personnel selected above?','radio',1,'capacityBuilding','Capacity-building or training is required.',0,0,'الموارد والقدرات','هل يتم تنفيذ أنشطة لبناء القدرات أو تدريب للكوادر المحددة أعلاه؟','يرجى اختيار إجابة.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Resources and Capacity','Training frequency','radio',1,'trainingFrequency','Training frequency is required.',0,0,'الموارد والقدرات','عدد مرات التدريب','يرجى اختيار إجابة.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Resources and Capacity','Other training frequency','text',0,'trainingFrequencyOther','Other training frequency is required.',0,0,'الموارد والقدرات','خيارات إضافية لعدد مرات التدريب','يرجى تحديد الخيار الإضافي.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Resources and Capacity','Infrastructure support available','checkbox',1,'infrastructureSupport','Select at least one infrastructure support.',0,0,'الموارد والقدرات','البنية التحتية الداعمة المتوفرة','يرجى اختيار خيار واحد على الأقل.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Resources and Capacity','Other infrastructure support','text',0,'infrastructureOther','Other infrastructure support is required.',0,0,'الموارد والقدرات','خيارات إضافية للبنية التحتية','يرجى تحديد الخيار الإضافي.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Feedback / Recommendations','Feedback or recommendations to enhance current school health promoting programs','textarea',1,'programFeedback','Feedback or recommendations is required.',0,1,'الملاحظات / التوصيات','ملاحظات أو توصيات لتعزيز برامج تعزيز الصحة في المدارس الحالية','الملاحظات أو التوصيات مطلوبة.')
  INTO SURVEY_DETAILS (SURVEY_HEADING, SURVEY_QNS, FIELD_TYPE, IS_MANDATORY, FIELD_OPTION, REQUIRED_MESSAGE, PRE_SURVEY, POST_SURVEY, SURVEY_HEADING_AR, SURVEY_QNS_AR, REQUIRED_MESSAGE_AR) VALUES ('Feedback / Recommendations','How can your institution contribute to the national initiative of Health-Promoting Schools?','textarea',1,'nationalContribution','Contribution is required.',0,1,'الملاحظات / التوصيات','كيف يمكن لمؤسستكم المساهمة في المبادرة الوطنية للمدارس المعززة للصحة؟','الإجابة مطلوبة.')
SELECT 1 FROM DUAL;

DELETE FROM OPTIONS
WHERE OPTION_TYPE IN (
  'institutionTypes','statusOptions','targetedTopics','targetPopulations','primaryGrades',
  'preparatoryGrades','secondaryGrades','coverageOptions','indicatorOptions','yesNoOptions',
  'documentStatusOptions','humanResources','trainingFrequencies','infrastructureSupports'
);

INSERT ALL
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Government','حكومية','institutionTypes')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Semi-Governmental','شبه حكومية','institutionTypes')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Private','خاصة','institutionTypes')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('NGO','منظمة غير حكومية','institutionTypes')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Other','خيارات إضافية','institutionTypes')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Ongoing','جارٍ التنفيذ','statusOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Planned','مخطط له','statusOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('One-time or repeated activity','مبادرة أو نشاط لمرة واحدة أو عدة مرات','statusOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Other','خيارات أخرى','statusOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Tobacco and nicotine product control','مكافحة منتجات التبغ والنيكوتين','targetedTopics')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Nutrition','التغذية','targetedTopics')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Physical Activity','النشاط البدني','targetedTopics')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Non-Communicable Diseases (chronic)','الأمراض غير الانتقالية (المزمنة)','targetedTopics')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Mental Health','الصحة النفسية','targetedTopics')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Oral Health','صحة الفم والأسنان','targetedTopics')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Communicable Diseases','الأمراض الانتقالية','targetedTopics')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Vaccination','التطعيمات','targetedTopics')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Environmental health','صحة البيئة','targetedTopics')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Safety','السلامة','targetedTopics')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Other','خيارات إضافية','targetedTopics')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Students','الطلاب','targetPopulations')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Teachers','المعلمون','targetPopulations')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Parents','أولياء الأمور','targetPopulations')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Other','خيارات إضافية','targetPopulations')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('All primary grade levels','كل المرحلة الابتدائية','primaryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Grade 1','الصف الأول','primaryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Grade 2','الصف الثاني','primaryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Grade 3','الصف الثالث','primaryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Grade 4','الصف الرابع','primaryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Grade 5','الصف الخامس','primaryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Grade 6','الصف السادس','primaryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('All preparatory grade levels','كل المرحلة الإعدادية','preparatoryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Grade 7','الأول إعدادي (السابع)','preparatoryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Grade 8','الثاني إعدادي (الثامن)','preparatoryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Grade 9','الثالث إعدادي (التاسع)','preparatoryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('All secondary grade levels','كل المرحلة الثانوية','secondaryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Grade 10','الأول ثانوي (العاشر)','secondaryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Grade 11','الثاني ثانوي (الحادي عشر)','secondaryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Grade 12','الثالث ثانوي (الثاني عشر)','secondaryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Grade 13','الثالث عشر','secondaryGrades')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('National (all government and private schools)','وطني (جميع المدارس الحكومية والخاصة)','coverageOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('All government schools only','جميع المدارس الحكومية فقط','coverageOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('All private schools only','جميع المدارس الخاصة فقط','coverageOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Selected government schools only','بعض المدارس الحكومية فقط','coverageOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Selected private schools only','بعض المدارس الخاصة فقط','coverageOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Selected government and private schools','بعض المدارس الحكومية والخاصة','coverageOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Yes','نعم','indicatorOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('No','لا','indicatorOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Under Development','قيد التطوير','indicatorOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Yes','نعم','yesNoOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('No','لا','yesNoOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Yes','نعم','documentStatusOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('No','لا','documentStatusOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Planned','مخطط لها','documentStatusOptions')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Nurses','الممرضون','humanResources')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Health Educators','المثقفون الصحيون','humanResources')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Social workers','الأخصائيون الاجتماعيون','humanResources')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Canteen supervisors','مشرفو التغذية','humanResources')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Coordinators','المنسقون','humanResources')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Other','خيارات إضافية','humanResources')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Monthly','شهري','trainingFrequencies')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Quarterly','ربع سنوي','trainingFrequencies')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Biannually','نصف سنوي','trainingFrequencies')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Annually','سنوي','trainingFrequencies')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Other','حسب الحاجة / خيارات أخرى','trainingFrequencies')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('School Clinics','العيادات المدرسية','infrastructureSupports')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Mobile Units','الوحدات المتنقلة','infrastructureSupports')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Digital Platforms','المنصات الرقمية','infrastructureSupports')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Training Centers','مراكز التدريب','infrastructureSupports')
  INTO OPTIONS (DESCRIPTION, DESCRIPTION_AR, OPTION_TYPE) VALUES ('Other','خيارات إضافية','infrastructureSupports')
SELECT 1 FROM DUAL;

COMMIT;
