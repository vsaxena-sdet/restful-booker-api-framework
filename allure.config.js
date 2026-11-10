// defineConfig is a pure type-helper in Allure 3 (identity at runtime).
// We define it locally so the file works without installing the allure npm
// package as a local dependency (the CLI injects it via its own node_modules).
const defineConfig = (config) => config;

// ─────────────────────────────────────────────────────────────────────────────
// All Allure 3 chart types with every configurable option set explicitly.
// Ref: https://allurereport.org/docs/visual-analytics/#charts-configuration
// ─────────────────────────────────────────────────────────────────────────────
const allCharts = [
  // ── 1. Current Status (pie chart of latest run) ───────────────────────────
  {
    type: "currentStatus",
    title: "Current status",
    statuses: ["passed", "failed", "broken", "skipped", "unknown"],
    metric: "passed", // status whose % appears in the centre of the pie
  },

  // ── 2. Severity chart (grouped by severity × status) ─────────────────────
  {
    type: "testResultSeverities",
    title: "Test results by severities",
    levels: ["blocker", "critical", "normal", "minor", "trivial"],
    statuses: ["passed", "failed", "broken", "skipped", "unknown"],
    includeUnset: true, // show a "No severity" section for un-labelled tests
  },

  // ── 3. Status dynamics (trend: status counts across runs) ─────────────────
  {
    type: "statusDynamics",
    title: "Status dynamics",
    statuses: ["passed", "failed", "broken", "skipped", "unknown"],
    limit: 10, // number of runs shown (including the latest)
  },

  // ── 4. Status transitions (fixed / regressed / malfunctioned per run) ─────
  {
    type: "statusTransitions",
    title: "Status transitions",
    limit: 10,
  },

  // ── 5. Test base growth dynamics (added / deleted tests per run) ──────────
  {
    type: "testBaseGrowthDynamics",
    title: "Test base growth dynamics",
    statuses: ["passed", "failed", "broken", "skipped", "unknown"],
    limit: 10,
  },

  // ── 6. Coverage diff map (tree map: coverage changes vs previous run) ─────
  {
    type: "coverageDiff",
    title: "Coverage diff map",
  },

  // ── 7. Success rate distribution (tree map: avg pass rate by feature) ─────
  {
    type: "successRateDistribution",
    title: "Success rate distribution",
  },

  // ── 8. Problems distribution by environment (heat map) ───────────────────
  {
    type: "problemsDistribution",
    title: "Problems distribution by environment",
    by: "environment",
  },

  // ── 9–11. Stability distribution (bar chart, one per grouping label) ──────
  {
    type: "stabilityDistribution",
    title: "Stability distribution by features",
    limit: 10,
    stabilizationPeriod: 5, // consecutive identical results before ignoring earlier changes
    threshold: 90,          // acceptable stability % (line drawn on chart)
    skipStatuses: ["skipped", "unknown"],
    groupBy: "feature",
  },
  {
    type: "stabilityDistribution",
    title: "Stability distribution by epics",
    limit: 10,
    stabilizationPeriod: 5,
    threshold: 90,
    skipStatuses: ["skipped", "unknown"],
    groupBy: "epic",
  },
  {
    type: "stabilityDistribution",
    title: "Stability distribution by stories",
    limit: 10,
    stabilizationPeriod: 5,
    threshold: 90,
    skipStatuses: ["skipped", "unknown"],
    groupBy: "story",
  },

  // ── 12. Durations histogram (all tests, no grouping) ─────────────────────
  {
    type: "durations",
    title: "Durations histogram",
    groupBy: "none",
  },

  // ── 13. Durations histogram grouped by layer label ────────────────────────
  {
    type: "durations",
    title: "Durations by layer histogram",
    groupBy: "layer",
  },

  // ── 14. Duration dynamics (execution time trend + speedup coefficient) ────
  {
    type: "durationDynamics",
    title: "Durations dynamics",
    limit: 10,
  },

  // ── 15. Status age pyramid (how long failed/broken/skipped tests persist) ─
  {
    type: "statusAgePyramid",
    title: "Status age pyramid",
    limit: 10,
  },

  // ── 16. Testing pyramid (unit / integration / e2e by layer label) ─────────
  {
    type: "testingPyramid",
    title: "Testing pyramid",
    layers: ["unit", "integration", "e2e"], // bottom → top
  },
];

export default defineConfig({
  name: "Restful Booker API Suite",
  output: "./allure-report",

  // ── History ────────────────────────────────────────────────────────────────
  // Required for all trend / dynamics / stability / age / diff charts.
  // Each `allure generate` run reads this file, then appends the current run.
  historyPath: "./history.jsonl",

  plugins: {
    // ── Awesome plugin: full interactive report ──────────────────────────────
    awesome: {
      options: {
        reportName: "Restful Booker API Suite",
        singleFile: false,
        reportLanguage: "en",
        charts: allCharts,
      },
    },

    // ── Dashboard plugin: charts-only report ─────────────────────────────────
    dashboard: {
      options: {
        reportName: "Restful Booker Dashboard",
        singleFile: false,
        reportLanguage: "en",
        layout: allCharts,
      },
    },
  },
});
