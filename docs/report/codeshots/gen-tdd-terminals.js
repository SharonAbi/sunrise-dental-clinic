const fs = require("fs");
const path = require("path");

function esc(s) {
  return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}

function terminalPage(title, prompt, codeText) {
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
  pre { margin:0; padding:16px 20px; font-size:14px; line-height:1.5; font-family: Consolas, 'Courier New', monospace; }
  .prompt { color:#6a9955; }
</style>
</head><body>
<div class="titlebar">${esc(title)}</div>
<pre><span class="prompt">${esc(prompt)}</span>
${colored}</pre>
</body></html>`;
}

const OUT = __dirname;

function clean(line) {
  return line
    .replace(/\r/g, "")
    .replace(/D:\\BSc\\ABI\\tdd-red-worktree\\/g, "")
    .replace(/D:\/BSc\/ABI\/tdd-red-worktree\//g, "")
    .replace(/\\/g, "/");
}

const redFull = fs.readFileSync("C:\\Users\\MYPC~1\\AppData\\Local\\Temp\\red_output.txt", "utf8").split("\n").map(clean);
const redErrStart = redFull.findIndex(l => l.includes("COMPILATION ERROR"));
const redFailIdx = redFull.findIndex(l => l.includes("BUILD FAILURE"));
const redLines = [
  ...redFull.slice(redErrStart, redErrStart + 6),
  ...redFull.slice(redFailIdx - 1, redFailIdx + 5),
];
const redTail = redLines.join("\n");

const greenFull = fs.readFileSync("C:\\Users\\MYPC~1\\AppData\\Local\\Temp\\green_output.txt", "utf8").split("\n").map(clean);
const greenClean = greenFull.filter(l =>
  !l.includes("WARNING:") && !l.includes("Java HotSpot") && l.trim() !== ""
);
const greenSuccessIdx = greenClean.findIndex(l => l.includes("Running com.sunrise"));
const greenEndIdx = greenClean.findIndex(l => l.includes("Finished at"));
const greenTail = greenClean.slice(greenSuccessIdx, greenEndIdx + 1).join("\n");

fs.writeFileSync(path.join(OUT, "snippet-tdd-red-real.html"), terminalPage(
  "Terminal - worktree @ 542f15f (TDD red step commit)",
  "$ mvn -B test",
  redTail
));

fs.writeFileSync(path.join(OUT, "snippet-tdd-green-real.html"), terminalPage(
  "Terminal - main (current, TDD green step applied)",
  "$ mvn -B test",
  greenTail
));

console.log("done");
