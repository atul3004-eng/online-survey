package com.kiptoo2000.surveyadmin.security;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
/** Checks GET, POST, AJAX, polling and download requests before the view executes. */
public class SurveyAccessFilter implements Filter {
 public void init(FilterConfig config) { }
 public void destroy() { }
 public static String requiredSurvey(String path) {
  if (path.startsWith("/faces/")) { path = path.substring(6); }
  if ("/jsf/school-health.xhtml".equals(path) || "/jsf/school-health-setup.xhtml".equals(path)) { return "school"; }
  if ("/jsf/wellness-results.xhtml".equals(path)) { return "wellness"; }
  return null;
 }
 public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
  HttpServletRequest req = (HttpServletRequest) request;
  HttpServletResponse res = (HttpServletResponse) response;
  String path = req.getServletPath();
  if (req.getPathInfo() != null) { path += req.getPathInfo(); }
  String canonical = path.startsWith("/faces/") ? path.substring(6) : path;
  // Reject retired routes even if an exploded deployment still contains an old page.
  if ("/school-health.xhtml".equals(canonical) || "/school-health-setup.xhtml".equals(canonical)) { res.sendError(404); return; }
  String survey = requiredSurvey(path);
  if (survey != null) {
   res.setHeader("Cache-Control", "no-store");
   if (!SurveyAccess.isAllowed(SurveyAccess.userId(req), survey)) {
    res.sendError(403, "You do not have access to this survey."); return;
   }
  } else if (path.startsWith("/jsf/") || path.startsWith("/faces/jsf/")) {
   res.sendError(403); return;
  }
  chain.doFilter(request, response);
 }
}
