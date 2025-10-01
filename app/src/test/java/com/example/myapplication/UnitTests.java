package com.example.myapplication;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;

public class UnitTests {

    // Unit Test for Vacation End Date After Start Date
    @Test
    public void testVacationEndDateAfterStartDate() {
        LocalDate startDate = LocalDate.of(2025, 10, 10);
        LocalDate endDate = LocalDate.of(2025, 10, 9);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validateVacationDates(startDate, endDate);
        });

        assertEquals("End date cannot be before start date", exception.getMessage());
    }

    private void validateVacationDates(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    // Unit Test for Excursion Date Within Vacation Dates
    @Test
    public void testExcursionDateWithinVacationDate() {
        LocalDate vacationStart = LocalDate.of(2025, 10, 10);
        LocalDate vacationEnd = LocalDate.of(2025, 10, 15);
        LocalDate excursionDate = LocalDate.of(2025, 10, 16);

        try {
            validateExcursionDate(vacationStart, vacationEnd, excursionDate);
            fail("Expected IllegalArgumentException"); // if no exception thrown
        } catch (IllegalArgumentException e) {
            assertEquals("Excursion date must be within vacation dates", e.getMessage());
        }
    }

    private void validateExcursionDate(LocalDate start, LocalDate end, LocalDate excursion) {
        if (excursion.isBefore(start) || excursion.isAfter(end)) {
            throw new IllegalArgumentException("Excursion date must be within vacation dates");
        }
    }
}
