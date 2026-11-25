// Renders preview still images: node scripts/stills.mjs <outDir> <frame> [frame...]
import { bundle } from "@remotion/bundler";
import { renderStill, selectComposition } from "@remotion/renderer";
import path from "node:path";
const [outDir, ...frames] = process.argv.slice(2);
const serveUrl = await bundle({ entryPoint: path.resolve("src/index.ts") });
const composition = await selectComposition({ serveUrl, id: "AdoptedStoryTime", browserExecutable: process.env.BROWSER_EXECUTABLE ?? null });
for (const f of frames) {
  await renderStill({ serveUrl, composition, frame: Number(f), output: path.join(outDir, `f${String(f).padStart(4, "0")}.png`), scale: 0.5, browserExecutable: process.env.BROWSER_EXECUTABLE ?? null });
  process.stdout.write(f + " ");
}
