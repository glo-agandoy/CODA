import { defineConfig, devices } from '@playwright/test';
import * as dotenv from 'dotenv';

dotenv.config();

/**
 * Playwright configuration for Airline UI Automation.
 * All values come from environment variables or defaults.
 */
export default defineConfig({
  testDir: './tests',
  fullyParallel: false,           // Sequential execution (airline booking flows have session state)
  forbidOnly: !!process.env.CI,   // Fail on .only in CI
  retries: process.env.CI ? 1 : 0,
  workers: 1,                     // Single worker (booking flow is stateful)

  reporter: [
    ['list'],
    ['allure-playwright', { outputFolder: 'allure-results' }],
    ['html', { outputFolder: 'playwright-report', open: 'never' }],
  ],

  use: {
    baseURL: process.env.BASE_URL,  // Required — set via environment variable
    headless: process.env.HEADLESS === 'true',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    trace: 'on-first-retry',
    actionTimeout: 15_000,
    navigationTimeout: 30_000,
  },

  projects: [
    {
      name: 'chromium',
      use: {
        ...devices['Desktop Chrome'],
        viewport: { width: 1920, height: 1080 },
      },
    },
  ],

  outputDir: 'test-results/',
});
