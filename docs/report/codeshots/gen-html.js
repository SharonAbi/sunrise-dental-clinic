const fs = require("fs");
const path = require("path");

function esc(s) {
  return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}

function codePage(title, lang, codeText) {
  return `<!DOCTYPE html>
<html><head><meta charset="utf-8">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/vs2015.min.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
<style>
  body { margin:0; background:#1e1e1e; }
  .titlebar { background:#323233; color:#cccccc; font:12px/1.4 Consolas,'Courier New',monospace; padding:7px 14px; }
  pre { margin:0 !important; }
  pre code.hljs { background:#1e1e1e !important; font-size:14px !important; line-height:1.55 !important; font-family: Consolas, 'Courier New', monospace !important; padding:16px 20px !important; display:block; }
</style>
</head><body>
<div class="titlebar">${esc(title)}</div>
<pre><code class="language-${lang}">${esc(codeText)}</code></pre>
<script>hljs.highlightAll();</script>
</body></html>`;
}

function terminalPage(title, codeText) {
  const colored = esc(codeText).split("\n").map(line => {
    if (line.startsWith("[ERROR]")) return `<span style="color:#f14c4c">${line}</span>`;
    if (line.startsWith("[INFO]")) return `<span style="color:#4ec9b0">${line}</span>`;
    if (line.startsWith("[WARNING]") || line.startsWith("[WARN]")) return `<span style="color:#dcdcaa">${line}</span>`;
    return `<span style="color:#d4d4d4">${line}</span>`;
  }).join("\n");
  return `<!DOCTYPE html>
<html><head><meta charset="utf-8">
<style>
  body { margin:0; background:#1e1e1e; }
  .titlebar { background:#323233; color:#cccccc; font:12px/1.4 Consolas,'Courier New',monospace; padding:7px 14px; }
  pre { margin:0; padding:16px 20px; font-size:14px; line-height:1.55; font-family: Consolas, 'Courier New', monospace; }
</style>
</head><body>
<div class="titlebar">${esc(title)}</div>
<pre>${colored}</pre>
</body></html>`;
}

const OUT = __dirname;
const SRC = path.resolve(__dirname, "../../..");

const inlineBlocks = [
  { id: "snippet-appointmentdao", title: "AppointmentDAO.java (excerpt)", lang: "java", code: `public interface AppointmentDAO {
    Appointment save(Appointment appointment) throws SQLException;
    Appointment findByAppointmentNumber(String appointmentNumber) throws SQLException;
    boolean existsByAppointmentNumber(String appointmentNumber) throws SQLException;
    List<Appointment> findAll() throws SQLException;
}` },
  { id: "snippet-dbconnection-getinstance", title: "DBConnection.java (excerpt)", lang: "java", code: `public static DBConnection getInstance() {
    if (instance == null) {
        synchronized (DBConnection.class) {
            if (instance == null) {
                instance = new DBConnection();
            }
        }
    }
    return instance;
}` },
  { id: "snippet-billingstrategy", title: "BillingStrategy.java + InsuranceBillingStrategy.java (excerpt)", lang: "java", code: `public interface BillingStrategy {
    double calculateDiscount(double consultationFee, double treatmentFee);
    double calculateTotal(double consultationFee, double treatmentFee);
    String getDescription();
}

public class InsuranceBillingStrategy implements BillingStrategy {
    private static final double INSURANCE_DISCOUNT_RATE = 0.20;
    public double calculateDiscount(double consultationFee, double treatmentFee) {
        return treatmentFee * INSURANCE_DISCOUNT_RATE;
    }
    public double calculateTotal(double consultationFee, double treatmentFee) {
        return consultationFee + treatmentFee - calculateDiscount(consultationFee, treatmentFee);
    }
}` },
];

const terminalBlocks = [
  { id: "snippet-tdd-red", title: "Terminal - mvn test (RED)", code: `[ERROR] COMPILATION ERROR :
[ERROR] .../ValidationUtilTest.java:[62,34] cannot find symbol
[ERROR]   symbol:   method isWithinClinicHours(java.time.LocalTime)
[ERROR]   location: class com.sunrise.dental.util.ValidationUtil
[INFO] BUILD FAILURE` },
  { id: "snippet-tdd-green", title: "Terminal - mvn test (GREEN)", code: `[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0 -- in ValidationUtilTest
[INFO] Tests run: 26, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS` },
];

const fileBlocks = [
  { id: "file-dbconnection", title: "DBConnection.java", lang: "java", file: "src/main/java/com/sunrise/dental/db/DBConnection.java" },
  { id: "file-daofactory", title: "DAOFactory.java", lang: "java", file: "src/main/java/com/sunrise/dental/dao/DAOFactory.java" },
  { id: "file-billingstrategyfactory", title: "BillingStrategyFactory.java", lang: "java", file: "src/main/java/com/sunrise/dental/service/billing/BillingStrategyFactory.java" },
  { id: "file-billservice", title: "BillService.java", lang: "java", file: "src/main/java/com/sunrise/dental/service/BillService.java" },
  { id: "file-schema", title: "sql/schema.sql", lang: "sql", file: "sql/schema.sql" },
  { id: "file-ci", title: ".github/workflows/ci.yml", lang: "yaml", file: ".github/workflows/ci.yml" },
];

for (const b of inlineBlocks) {
  fs.writeFileSync(path.join(OUT, b.id + ".html"), codePage(b.title, b.lang, b.code));
}
for (const b of terminalBlocks) {
  fs.writeFileSync(path.join(OUT, b.id + ".html"), terminalPage(b.title, b.code));
}
for (const b of fileBlocks) {
  const content = fs.readFileSync(path.join(SRC, b.file), "utf8");
  fs.writeFileSync(path.join(OUT, b.id + ".html"), codePage(b.title, b.lang, content));
}

console.log("Generated", inlineBlocks.length + terminalBlocks.length + fileBlocks.length, "HTML files");
