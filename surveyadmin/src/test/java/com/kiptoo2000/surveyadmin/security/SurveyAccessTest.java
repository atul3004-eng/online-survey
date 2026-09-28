package com.kiptoo2000.surveyadmin.security;
import java.lang.reflect.Proxy;
import java.security.Principal;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.*;
import javax.servlet.http.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SurveyAccessTest {
 @SuppressWarnings("unchecked")
 private static <T> T proxy(Class<T> type, java.lang.reflect.InvocationHandler handler) {
  return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
 }
 private HttpServletRequest request(String servletPath, String pathInfo, String principal, String mock, String method) {
  ServletContext context = proxy(ServletContext.class, (o,m,a) -> "getInitParameter".equals(m.getName()) ? mock : null);
  return proxy(HttpServletRequest.class, (o,m,a) -> {
   switch (m.getName()) {
    case "getServletPath": return servletPath;
    case "getPathInfo": return pathInfo;
    case "getUserPrincipal": return principal == null ? null : (Principal) () -> principal;
    case "getServletContext": return context;
    case "getMethod": return method;
    case "getParameter": return "school-user"; // Spoofed IDs must never grant access.
    default: return null;
   }
  });
 }
 private void check(String servletPath, String pathInfo, String user, String mock, String method, boolean allowed) throws Exception {
  AtomicInteger status = new AtomicInteger(200), calls = new AtomicInteger();
  HttpServletResponse response = proxy(HttpServletResponse.class, (o,m,a) -> {
   if ("sendError".equals(m.getName())) status.set((Integer)a[0]);
   return null;
  });
  new SurveyAccessFilter().doFilter(request(servletPath,pathInfo,user,mock,method),response,(req,res)->calls.incrementAndGet());
  assertEquals(allowed ? 1 : 0,calls.get());
  assertEquals(allowed ? 200 : 403,status.get());
 }
 @Test public void grantsAreExplicitAndSurveySpecific() {
  assertTrue(SurveyAccess.isAllowed("school-user","school"));
  assertTrue(SurveyAccess.isAllowed("wellness-user","wellness"));
  assertFalse(SurveyAccess.isAllowed("school-user","wellness"));
  assertFalse(SurveyAccess.isAllowed("wellness-user","school"));
  assertFalse(SurveyAccess.isAllowed(null,"school"));
  assertFalse(SurveyAccess.isAllowed("unknown","school"));
  assertFalse(SurveyAccess.isAllowed("school-user",""));
 }
 @Test public void bothMappingsProtectGetPostAjaxAndDownloadPaths() throws Exception {
  for(String method : new String[]{"GET","POST"}) {
   for(String page : new String[]{"school-health.xhtml","school-health-setup.xhtml","wellness-results.xhtml"}) {
    boolean wellness = page.startsWith("wellness");
    String owner = wellness ? "wellness-user" : "school-user";
    String other = wellness ? "school-user" : "wellness-user";
    check("/jsf/"+page,null,owner,null,method,true);
    check("/jsf/"+page,null,other,null,method,false);
    check("/faces","/jsf/"+page,owner,null,method,true);
    check("/faces","/jsf/"+page,other,null,method,false);
    check("/jsf/"+page,null,null,null,method,false);
   }
  }
 }
 @Test public void principalOverridesMockAndRequestsCannotChooseUser() throws Exception {
  check("/jsf/school-health.xhtml",null,null,"school-user","GET",true);
  check("/jsf/school-health.xhtml",null,"wellness-user","school-user","POST",false);
  check("/jsf/school-health.xhtml",null,null,"","GET",false);
 }
 @Test public void publicPagesWorkAndUnknownProtectedPagesFailClosed() throws Exception {
  check("/login.xhtml",null,null,null,"GET",true);
  check("/wellness.xhtml",null,null,null,"POST",true);
  check("/jsf/unconfigured.xhtml",null,"school-user",null,"GET",false);
 }
 @Test public void beanGuardRejectsCallsWithoutRequest() {
  try { SurveyAccess.require("school"); fail("Expected denial"); }
  catch (SecurityException expected) { }
 }
}
