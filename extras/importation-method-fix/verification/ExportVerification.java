package dps.jsf;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import javax.enterprise.concurrent.ManagedExecutorService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/** Verification fixtures only; never deploy this directory. */
public class ExportVerification {
    private static int checks;
    private static final byte[] PAYLOAD = {1, 2, 3};
    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        ManagedExecutorService managed = (ManagedExecutorService) Proxy.newProxyInstance(
                ExportVerification.class.getClassLoader(), new Class<?>[]{ManagedExecutorService.class},
                (proxy, method, values) -> {
                    try { return method.invoke(pool, values); }
                    catch (InvocationTargetException ex) { throw ex.getCause(); }
                });
        try {
            TestController c = new TestController(managed);
            ImportationMasterController filters = new ImportationMasterController();
            c.setImportationMasterController(filters);
            check(!c.isExportReady() && c.getFile() == null, "initial state");
            for (int status=0; status<3; status++) {
                filters.approved = status==0; filters.pending = status==1;
                c.started = new CountDownLatch(1); c.release = new CountDownLatch(1);
                c.next = content(PAYLOAD);
                c.prepareImportation();
                check(c.started.await(3, TimeUnit.SECONDS), "worker starts");
                check(c.worker != Thread.currentThread(), "generation uses background thread");
                check(c.isExportInProgress() && c.getFile()==null, "request returns before generation finishes");
                filters.approved = false; filters.pending = false;
                int calls = c.calls;
                c.prepareStatisticalImportation();
                check(c.calls==calls, "duplicate request ignored");
                c.release.countDown(); await(c);
                check(new String[]{"Approved", "In-Progress", "All"}[status].equals(c.status), "captured filter is immutable");
                check(c.isExportReady(), "ready only after completion");
                StreamedContent first=c.getFile(), second=c.getFile();
                check(first.getStream()!=second.getStream(), "fresh download streams");
                check(Arrays.equals(PAYLOAD, read(first)) && Arrays.equals(PAYLOAD, read(second)), "repeat downloads");
            }
            c.started = new CountDownLatch(1); c.release = new CountDownLatch(0);
            for (StreamedContent bad : new StreamedContent[]{null,content(new byte[0]),
                    new DefaultStreamedContent(null,"application/vnd.ms-excel","bad.xls"),
                    new DefaultStreamedContent(new InputStream(){public int read() throws IOException {throw new IOException("fixture");}},"application/vnd.ms-excel","bad.xls")}) {
                c.next=bad; c.prepareImportation(); await(c); failed(c);
            }
            c.fail=true; c.prepareImportation(); await(c); failed(c); c.fail=false;
            c.next=content(PAYLOAD); c.prepareStatisticalImportation(); await(c);
            check(c.statistical && c.isExportReady() && c.getExportError()==null, "statistical retry");
            check("Download Statistical Excel".equals(c.getDownloadLabel()), "statistical label");
            c.allowed=false;
            try { c.getFile(); throw new AssertionError("download authorization missing"); } catch(SecurityException expected) {checks++;}
            try { c.prepareImportation(); throw new AssertionError("generation authorization missing"); } catch(SecurityException expected) {checks++;}
            c.allowed=true;
            c.started=new CountDownLatch(1); c.release=new CountDownLatch(1);
            c.prepareImportation(); check(c.started.await(3,TimeUnit.SECONDS),"cancellation worker started");
            c.destroy(); c.release.countDown();
            Future<?> fence=pool.submit(()->{}); fence.get(3,TimeUnit.SECONDS);
            check(!c.isExportReady() && !c.isExportInProgress(),"destroy prevents late result publication");
            pool.shutdown();
            TestController rejected=new TestController(managed); rejected.setImportationMasterController(filters);
            rejected.prepareImportation(); failed(rejected);
            verifyWorkbook("SpecialImportationExcelExporter",25);
            verifyWorkbook("SpecialImportationStatisticalExcelExporter",51);
            System.out.println("PASS: "+checks+" background, failure, download and workbook checks.");
        } finally { pool.shutdownNow(); }
    }
    private static void verifyWorkbook(String name,int columns) throws Exception {
        Class<?> type=Class.forName("dps.jsf.ImportationExportController$"+name);
        Constructor<?> ctor=type.getDeclaredConstructor(); ctor.setAccessible(true);
        boolean statistical=columns==51;
        Method export=statistical ? type.getDeclaredMethod("export",List.class) : type.getDeclaredMethod("export",String.class,List.class);
        export.setAccessible(true);
        List<dps.ejb.ImportationMaster> records=Collections.singletonList(new dps.ejb.ImportationMaster());
        StreamedContent file=(StreamedContent)(statistical ? export.invoke(ctor.newInstance(),records) : export.invoke(ctor.newInstance(),"All",records));
        check(file.getName().endsWith(".xls") && "application/vnd.ms-excel".equals(file.getContentType()),"XLS metadata");
        try(HSSFWorkbook workbook=new HSSFWorkbook(file.getStream())) {
            check(workbook.getSheetAt(0).getRow(0).getLastCellNum()==columns,"all report columns retained");
            check(workbook.getSheetAt(0).getLastRowNum()==1,"sample record exported");
        }
    }
    private static StreamedContent content(byte[] data) { return new DefaultStreamedContent(new ByteArrayInputStream(data),"application/vnd.ms-excel","report.xls"); }
    private static byte[] read(StreamedContent file) throws IOException {
        try(InputStream input=file.getStream(); ByteArrayOutputStream out=new ByteArrayOutputStream()) {
            int next; while((next=input.read())!=-1) out.write(next); return out.toByteArray();
        }
    }
    private static void await(ImportationExportController c) throws Exception {
        long end=System.nanoTime()+TimeUnit.SECONDS.toNanos(3);
        while(c.isExportInProgress() && System.nanoTime()<end) Thread.sleep(5);
        check(!c.isExportInProgress(),"worker completes");
    }
    private static void failed(ImportationExportController c) { check(!c.isExportInProgress() && !c.isExportReady() && c.getFile()==null && c.getExportError()!=null,"failure clears result"); }
    private static void check(boolean ok,String message) { if(!ok) throw new AssertionError(message); checks++; }
    private static class TestController extends ImportationExportController {
        final ManagedExecutorService managed;
        volatile CountDownLatch started=new CountDownLatch(1),release=new CountDownLatch(0);
        volatile StreamedContent next;
        volatile String status;
        volatile boolean statistical,fail;
        volatile int calls;
        volatile Thread worker;
        boolean allowed=true;
        TestController(ManagedExecutorService executor) {managed=executor;}
        @Override protected ManagedExecutorService executor() {return managed;}
        @Override protected void authorize(boolean statistical) {if(!allowed) throw new SecurityException("fixture");}
        @Override protected StreamedContent buildReport(String status,boolean statistical) throws Exception {
            calls++; worker=Thread.currentThread(); started.countDown(); release.await();
            this.status=status; this.statistical=statistical;
            if(fail) throw new IOException("fixture failure");
            return next;
        }
    }
}
class ImportationMasterController implements Serializable {
    boolean approved,pending;
    public boolean isApprovedItems() {return approved;}
    public boolean isPendingItems() {return pending;}
}
