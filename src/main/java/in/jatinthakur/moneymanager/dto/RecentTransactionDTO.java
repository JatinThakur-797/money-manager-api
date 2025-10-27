package in.jatinthakur.moneymanager.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RecentTransactionDTO {

    private Long id;
    private Long profileId;
    private String name;
    private String icon;
    private BigDecimal amount;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String type; // "INCOME" or "EXPENSE"



}
