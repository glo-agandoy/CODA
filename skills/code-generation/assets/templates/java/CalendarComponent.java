package components;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * CalendarComponent — Handles the airline's custom date picker widget.
 *
 * Key design decisions:
 * - All dates calculated relative to today (T+N) — NEVER hardcoded
 * - Navigates to correct month automatically
 * - Uses data-testid selectors for maximum stability
 *
 * NOTE: Selectors are generic defaults. Update with real values from web-exploration
 * if the airline uses different data-testid values or a different date attribute format.
 * See skills/airline-domain/references/ui-patterns.md (Section 6: Calendar Date Picker).
 *
 * @author Code Generation Skill
 * @version 2.0
 */
public class CalendarComponent {

    private final WebDriver     driver;
    private final WebDriverWait wait;

    private static final By     CALENDAR_CONTAINER = By.cssSelector("[data-testid='calendar']");
    private static final By     NEXT_MONTH_BTN     = By.cssSelector("[data-testid='calendar-next']");
    private static final By     PREV_MONTH_BTN     = By.cssSelector("[data-testid='calendar-prev']");
    /** Pattern: [data-testid='calendar-day-2025-03-15'] */
    private static final String DAY_SELECTOR_PATTERN = "[data-testid='calendar-day-%s']";

    public CalendarComponent(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Select a date N days from today.
     * This is the primary method — always use this instead of selectDate(LocalDate).
     *
     * @param daysFromToday Number of days from today (must be >= 1)
     */
    @Step("Select outbound date: T+{daysFromToday} days from today")
    public void selectDateFromToday(int daysFromToday) {
        if (daysFromToday < 1) {
            throw new IllegalArgumentException(
                "daysFromToday must be >= 1 (no same-day or past dates). Got: " + daysFromToday);
        }
        selectDate(LocalDate.now().plusDays(daysFromToday));
    }

    /**
     * Select a specific date on the calendar.
     * Automatically navigates to the correct month.
     *
     * @param date Target date (must be in the future)
     */
    @Step("Select calendar date: {date}")
    public void selectDate(LocalDate date) {
        // Wait for calendar to be visible
        wait.until(d -> d.findElement(CALENDAR_CONTAINER).isDisplayed());

        // Navigate to the correct month
        navigateToMonth(date);

        // Build and click the day selector
        String daySelector = String.format(DAY_SELECTOR_PATTERN,
            date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // e.g., "2025-03-15"

        WebElement dayElement = wait.until(
            ExpectedConditions.elementToBeClickable(By.cssSelector(daySelector)));
        dayElement.click();
    }

    /**
     * Navigate the calendar to the month containing the target date.
     * Clicks "next month" button as many times as needed.
     */
    private void navigateToMonth(LocalDate targetDate) {
        LocalDate currentMonthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate targetMonthStart  = targetDate.withDayOfMonth(1);

        while (currentMonthStart.isBefore(targetMonthStart)) {
            WebElement nextBtn = wait.until(
                ExpectedConditions.elementToBeClickable(NEXT_MONTH_BTN));
            nextBtn.click();
            currentMonthStart = currentMonthStart.plusMonths(1);
        }
    }
}
