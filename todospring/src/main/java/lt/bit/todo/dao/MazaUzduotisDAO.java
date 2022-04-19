
package lt.bit.todo.dao;

import java.util.List;
import lt.bit.todo.data.MazaUzduotis;
import lt.bit.todo.data.Uzduotis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface MazaUzduotisDAO extends JpaRepository <MazaUzduotis, Integer> {
    @Query("select mu from MazaUzduotis mu where mu.uzduotis= :uzduotis")
    public List<MazaUzduotis> getByUzduotis(@Param("uzduotis") Uzduotis u);
}
