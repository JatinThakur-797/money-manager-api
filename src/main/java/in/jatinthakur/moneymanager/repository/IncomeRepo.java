package in.jatinthakur.moneymanager.repository;

import in.jatinthakur.moneymanager.entity.ExpenseEntity;
import in.jatinthakur.moneymanager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepo extends JpaRepository<IncomeEntity, Long> {
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    //select * from tbl_incomes where profile_id = ? order by date desc limit 5
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(e.amount) FROM IncomeEntity e WHERE e.profile.id = :profileId")
    BigDecimal findTotalIncomeAmountByProfileId(@Param("profileId") Long profileId);

    //select * from tbl_incomes where profile_id = ? and date between ? and ? and name like %?%
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId, LocalDate startDate, LocalDate endDate, String name , Sort sort);

    //select * from tbl_incomes where profile_id = ? and date between ? and ?
    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    //select * from tbl_incomes where profile_id = ? and date = ?
    List<IncomeEntity> findByProfileIdAndDate(Long profileId, LocalDate date);

    List<IncomeEntity> findByProfileId(Long id);
}
