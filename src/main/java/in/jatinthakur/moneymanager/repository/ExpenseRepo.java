package in.jatinthakur.moneymanager.repository;

import in.jatinthakur.moneymanager.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepo extends JpaRepository<ExpenseEntity,Long> {
   //select * from tbl_expenses where profile_id = ? order by date desc
    List<ExpenseEntity>  findByProfileIdOrderByDateDesc(Long profileId);

    //select * from tbl_expenses where profile_id = ? order by date desc limit 5
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id = :profileId")
    BigDecimal findTotalExpenseAmountByProfileId(@Param("profileId") Long profileId);

    //select * from tbl_expenses where profile_id = ? and date between ? and ? and name like %?%
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId, LocalDate startDate, LocalDate endDate, String name , Sort sort);

    //select * from tbl_expenses where profile_id = ? and date between ? and ?
   List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    //select * from tbl_expenses where profile_id = ? and date = ?
   List<ExpenseEntity> findByProfileIdAndDate(Long profileId, LocalDate date);

    List<ExpenseEntity> findByProfileId(Long id);
}

