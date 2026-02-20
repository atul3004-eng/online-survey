# online-survey-system-java

This is a project for developing an online survey system using Java. The focus of the project is to build an online platform that can efficiently collect the viewpoints of the target audience of a survey via the internet. This application can launch online surveys and also send email notifications.

## Maven build

This repository now includes Maven configuration as a multi-module project:

- `survey` (user-facing app)
- `surveyadmin` (admin app)

Build both WAR files:

```bash
mvn clean package
```

Artifacts:

- `survey/target/survey.war`
- `surveyadmin/target/surveyadmin.war`

Prerequisites:

- Java 8+ for build
- A Java EE compatible servlet container/application server to deploy WARs
- MySQL DB reachable at `jdbc:mysql://localhost:3306/survey` with user/password `root/root` (configured in DAO classes)

## Quick run (Tomcat + MySQL)

1. Create DB schema:

```sql
source mysql-schema.sql
```

2. Build WARs:

```bash
mvn clean package -DskipTests
```

3. Deploy `survey` and `surveyadmin` WARs to Tomcat `webapps/`.

4. Start Tomcat with Java 8:

```bash
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202
```
