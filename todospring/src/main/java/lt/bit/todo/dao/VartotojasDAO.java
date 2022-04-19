
package lt.bit.todo.dao;

import java.util.List;
import lt.bit.todo.data.Uzduotis;
import lt.bit.todo.data.Vartotojas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface VartotojasDAO extends JpaRepository <Vartotojas, Integer>{
    @Query("select v from Vartotojas v")
    public List<Vartotojas> getVartotojai();
    
    @Query("select v from Vartotojas v where v.vardas= :un")
    public List<Vartotojas> byVardas(@Param("un") String vardas);
    
    @Query("select v from Vartotojas v where v.vardas like :un")
    public List<Vartotojas> byVardasFilter(@Param("un") String vardas);
    
//    @Query("select v from Vartotojas v where v.vardas= :vardas and v.slaptazodis= :slaptazodis")
//    public Vartotojas login(@Param("vardas") String vardas, @Param("slaptazodis") String slaptazodis);
}
