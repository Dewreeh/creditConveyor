package org.deal.model;

import jakarta.persistence.*;
import lombok.Data;
import org.deal.enums.Gender;
import org.deal.enums.MaritalStatus;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name="client")
public class Client {
    @Id
    private UUID clientId;

    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate birthDate;
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    private Integer dependentAmount;

    @ManyToOne
    @JoinColumn(name = "passportUuid", referencedColumnName = "passportUuid")
    private Passport passport;

    @ManyToOne
    @JoinColumn(name = "employmentUuid", referencedColumnName = "employmentUuid")
    private Employment employment;

    private String accountNumber;

}
