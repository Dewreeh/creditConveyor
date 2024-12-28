

import org.junit.jupiter.api.Test;
import org.statement.dto.LoanStatementRequestDto;
import org.statement.service.PrescoringService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PrescoringServiceTest {

    private final PrescoringService prescoringService = new PrescoringService();

    @Test
    void testValidDto() {
        LoanStatementRequestDto dto = createValidDto();

        boolean result = prescoringService.doPrescoring(dto);

        assertTrue(result);
    }

    @Test
    void testInvalidFirstName() {
        LoanStatementRequestDto dto = createValidDto();
        dto.setFirstName("1"); //невалидное имя

        boolean result = prescoringService.doPrescoring(dto);

        assertFalse(result);
    }

    @Test
    void testInvalidLastName() {
        LoanStatementRequestDto dto = createValidDto();
        dto.setLastName(null); // невалидная фамилия

        boolean result = prescoringService.doPrescoring(dto);

        assertFalse(result, "DTO with null last name should fail prescoring");
    }

    @Test
    void testInvalidAmount() {
        LoanStatementRequestDto dto = createValidDto();
        dto.setAmount(new BigDecimal("19999")); // сумма меньше минимальной

        boolean result = prescoringService.doPrescoring(dto);

        assertFalse(result);
    }

    @Test
    void testInvalidTerm() {
        LoanStatementRequestDto dto = createValidDto();
        dto.setTerm(5); // срок меньше минимального

        boolean result = prescoringService.doPrescoring(dto);

        assertFalse(result);
    }

    @Test
    void testInvalidEmail() {
        LoanStatementRequestDto dto = createValidDto();
        dto.setEmail("abo"); // невалидная почта

        boolean result = prescoringService.doPrescoring(dto);

        assertFalse(result);
    }

    @Test
    void testInvalidPassportSeries() {
        LoanStatementRequestDto dto = createValidDto();
        dto.setPassportSeries("123"); // невалидная серия

        boolean result = prescoringService.doPrescoring(dto);

        assertFalse(result);
    }

    @Test
    void testInvalidPassportNumber() {
        LoanStatementRequestDto dto = createValidDto();
        dto.setPassportNumber("12345"); // невалидный номер

        boolean result = prescoringService.doPrescoring(dto);

        assertFalse(result);
    }

    @Test
    void testInvalidBirthdate() {
        LoanStatementRequestDto dto = createValidDto();
        dto.setBirthdate(LocalDate.now().minusYears(17)); // неподходящий возраст

        boolean result = prescoringService.doPrescoring(dto);

        assertFalse(result);
    }

    private LoanStatementRequestDto createValidDto() {
        LoanStatementRequestDto dto = new LoanStatementRequestDto();
        dto.setFirstName("Cristiano");
        dto.setLastName("Ronaldo");
        dto.setMiddleName("Smith");
        dto.setAmount(new BigDecimal("50000"));
        dto.setTerm(12);
        dto.setEmail("crid@gmail.com");
        dto.setPassportSeries("1234");
        dto.setPassportNumber("123456");
        dto.setBirthdate(LocalDate.now().minusYears(25));
        return dto;
    }
}
