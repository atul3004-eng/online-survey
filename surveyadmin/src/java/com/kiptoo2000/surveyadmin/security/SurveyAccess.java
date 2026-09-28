package com.kiptoo2000.surveyadmin.security;
import java.io.*;
import java.security.Principal;
import java.util.*;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
/** Server-owned user-to-survey grants. Missing identities and grants fail closed. */
public final class SurveyAccess {
 private static final Properties GRANTS = load();
 private SurveyAccess() { }
 private static Properties load() {
  Properties grants = new Properties();
  try (InputStream in = SurveyAccess.class.getResourceAsStream("/survey-access.properties")) {
   if (in == null) { throw new IllegalStateException("Missing survey-access.properties"); }
   grants.load(in); return grants;
  } catch (IOException ex) { throw new IllegalStateException("Cannot load survey grants", ex); }
 }
 public static boolean isAllowed(String userId, String survey) {
  if (userId == null || survey == null || survey.trim().isEmpty()) { return false; }
  return Arrays.stream(GRANTS.getProperty(userId, "").split(",")).map(String::trim).anyMatch(survey::equals);
 }
 public static String userId(HttpServletRequest request) {
  Principal principal = request.getUserPrincipal();
  if (principal != null) { return principal.getName(); }
  // Development-only server setting, never a request parameter or header.
  String mock = request.getServletContext().getInitParameter("survey.mockUserId");
  return mock == null || mock.trim().isEmpty() ? null : mock.trim();
 }
 public static void require(String survey) {
  FacesContext faces = FacesContext.getCurrentInstance();
  if (faces == null) { throw new SecurityException("Survey access requires a request"); }
  HttpServletRequest request = (HttpServletRequest) faces.getExternalContext().getRequest();
  if (!isAllowed(userId(request), survey)) { throw new SecurityException("Survey access denied"); }
 }
}
