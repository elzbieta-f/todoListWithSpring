
package lt.bit.todo.dao;

import java.util.List;
import lt.bit.todo.data.Uzduotis;
import lt.bit.todo.data.Vartotojas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UzduotisDAO extends JpaRepository <Uzduotis, Integer> {
    @Query("select u from Uzduotis u where u.vartotojas= :vartotojas")
    public List<Uzduotis> getByVartotojas(@Param("vartotojas") Vartotojas v);
    
    @Query("select u from Uzduotis u where u.vartotojas= :vartotojas "
            + "and ( UPPER(u.pavadinimas) like UPPER( :filter) or UPPER(u.aprasymas) like UPPER( :filter))")
    public List<Uzduotis> getByVartotojasFilter(@Param("vartotojas") Vartotojas v, @Param("filter") String filter);
    
    @Query("select u from Uzduotis u where u.vartotojas= :vartotojas and u.atlikta is null")
    public List<Uzduotis> getNotCompleted(@Param("vartotojas") Vartotojas v);
    
    @Query("select u from Uzduotis u where u.vartotojas= :vartotojas and u.atlikta is not null")
    public List<Uzduotis> getCompleted(@Param("vartotojas") Vartotojas v);
    
    @Query("select u from Uzduotis u where u.vartotojas= :vartotojas order by u.statusas desc")
    public List<Uzduotis> statusDesc(@Param("vartotojas") Vartotojas v);
    
    @Query("select u from Uzduotis u where u.vartotojas= :vartotojas order by u.statusas asc")
    public List<Uzduotis> statusAsc(@Param("vartotojas") Vartotojas v);
    
    @Query("select u from Uzduotis u where u.vartotojas= :vartotojas order by u.ikiKada desc")
    public List<Uzduotis> dateDesc(@Param("vartotojas") Vartotojas v);
    
    @Query("select u from Uzduotis u where u.vartotojas= :vartotojas and u.atlikta is null order by u.ikiKada asc")
    public List<Uzduotis> dateAsc(@Param("vartotojas") Vartotojas v);
    
    @Query("select u from Uzduotis u where u.vartotojas= :vartotojas and u.atlikta is not null order by u.atlikta desc")
    public List<Uzduotis> doneDesc(@Param("vartotojas") Vartotojas v);
    
    @Query("select u from Uzduotis u where u.vartotojas= :vartotojas and u.atlikta is not null order by u.atlikta asc")
    public List<Uzduotis> doneAsc(@Param("vartotojas") Vartotojas v);
}
