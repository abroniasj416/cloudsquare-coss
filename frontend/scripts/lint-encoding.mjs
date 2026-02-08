import { readdirSync, readFileSync, statSync } from "node:fs";
import { join } from "node:path";

const rootDir = join(process.cwd(), "src");
const blockedPatterns = ["\uFFFD", "���"];

const offenders = [];

function walk(dir) {
  const entries = readdirSync(dir);
  for (const entry of entries) {
    const fullPath = join(dir, entry);
    const stats = statSync(fullPath);
    if (stats.isDirectory()) {
      walk(fullPath);
      continue;
    }

    const content = readFileSync(fullPath, "utf8");
    const found = blockedPatterns.filter((pattern) => content.includes(pattern));
    if (found.length > 0) {
      offenders.push({ file: fullPath, patterns: found });
    }
  }
}

walk(rootDir);

if (offenders.length > 0) {
  console.error("Encoding check failed. Garbled text patterns were found:");
  for (const offender of offenders) {
    console.error(`- ${offender.file}: ${offender.patterns.join(", ")}`);
  }
  process.exit(1);
}

console.log("Encoding check passed. No mojibake patterns found in frontend/src.");
