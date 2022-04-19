package lt.bit.todo.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lt.bit.todo.config.VartotojasDetails;
import lt.bit.todo.dao.MazaUzduotisDAO;
import lt.bit.todo.dao.UzduotisDAO;
import lt.bit.todo.dao.VartotojasDAO;
import lt.bit.todo.data.MazaUzduotis;
import lt.bit.todo.data.Uzduotis;
import lt.bit.todo.data.Vartotojas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path = "/todo")
public class UzduotisController {

    @Autowired
    private UzduotisDAO uzduotisDAO;

    @Autowired
    private VartotojasDAO vartotojasDAO;

    @Autowired
    private MazaUzduotisDAO mazaUzduotisDAO;

    @GetMapping
    public ModelAndView list(Authentication auth,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "sortStatus", required = false) String sortStatus,
            @RequestParam(value = "sortDate", required = false) String sortDate,
            @RequestParam(value = "done", required = false) String done, 
            @RequestParam(value = "passChange", required = false) String passChange) {
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas v = vd.getVartotojas();
        ModelAndView mv = new ModelAndView("todo");
        List<Uzduotis> list;
        if (filter != null && !filter.trim().equals("")) {
            filter = "%" + filter + "%";
            list = uzduotisDAO.getByVartotojasFilter(v, filter);
        } else if (status != null && status.trim().equals("notCompleted")) {
            list = uzduotisDAO.getNotCompleted(v);
        } else if (status != null && status.trim().equals("completed")) {
            list = uzduotisDAO.getCompleted(v);
        } else if (sortStatus != null && sortStatus.trim().equals("asc")){
            list =uzduotisDAO.statusAsc(v);
        } else if (sortStatus != null && sortStatus.trim().equals("desc")){
            list =uzduotisDAO.statusDesc(v);
        } else if (sortDate != null && sortDate.trim().equals("desc")){
            list =uzduotisDAO.dateDesc(v);
        } else if (sortDate != null && sortDate.trim().equals("asc")){
            list =uzduotisDAO.dateAsc(v);
        } else if (done != null && done.trim().equals("asc")){
            list =uzduotisDAO.doneAsc(v);
        } else if (done != null && done.trim().equals("desc")){
            list =uzduotisDAO.doneDesc(v);
        }
        else {
            list = uzduotisDAO.getByVartotojas(v);
        }
        if (passChange != null && passChange.trim().equals("success")){
            mv.addObject("passChange", "Slaptaždis pakeistas sėkmingai");
        }
        if (passChange != null && passChange.trim().equals("fail")){
            mv.addObject("passChange", "Nepavyko pakeisti slaptažodžio");
        }
        mv.addObject("vartotojas", v);
        mv.addObject("uzduotys", list);
        return mv;
    }

    @GetMapping(path = "edit")
    public ModelAndView redagavimas(Authentication auth,
            @RequestParam(value = "todoId", required = false) Integer id) {
        ModelAndView mv = new ModelAndView("todoEdit");
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas v = vd.getVartotojas();
        mv.addObject("vartotojas", v);
        if (id != null) {
            Optional<Uzduotis> ou = uzduotisDAO.findById(id);
            if (ou.isEmpty()) {
                ModelAndView empty = new ModelAndView("redirect:../todo");
                return empty;
            }
            Uzduotis u = ou.get();
            List<MazaUzduotis> mazos = mazaUzduotisDAO.getByUzduotis(u);
            mv.addObject("todo", u);
            mv.addObject("mazos", mazos);
        }
        return mv;
    }

    @GetMapping(path = "detail")
    public ModelAndView mazosRedagavimas(
            Authentication auth,
            @RequestParam(value = "todoId") Integer todoId,
            @RequestParam(value = "mazaId", required = false) Integer mazaId) {
        ModelAndView mv = new ModelAndView("todoEdit");
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas v = vd.getVartotojas();
        mv.addObject("vartotojas", v);
        Optional<Uzduotis> ou = uzduotisDAO.findById(todoId);
        if (ou.isEmpty()) {
            ModelAndView empty = new ModelAndView("redirect:../todo");
            return empty;
        }
        Uzduotis u = ou.get();
        List<MazaUzduotis> mazos = mazaUzduotisDAO.getByUzduotis(u);
        mv.addObject("todo", u);
        mv.addObject("mazos", mazos);
        if (mazaId == null) {
            mv.addObject("newMaza", true);
        } else {
            Optional<MazaUzduotis> mazaO = mazaUzduotisDAO.findById(mazaId);
            if (mazaO.isEmpty()) {
                ModelAndView empty = new ModelAndView("redirect:../todo?&todoId=" + todoId);
                return empty;
            }
            MazaUzduotis maza = mazaO.get();
            mv.addObject("maza", maza);
        }
        return mv;
    }

    @PostMapping(path = "edit/save")
    @Transactional
    public String issaugojimas(
            Authentication auth,
            @RequestParam(value = "todoId", required = false) Integer todoId,
            @RequestParam(value = "pavadinimas") String pavadinimas,
            @RequestParam(value = "aprasymas", required = false) String aprasymas,
            @RequestParam(value = "ikiKada") String ikiKadaStr,
            @RequestParam(value = "statusas", required = false) Integer statusas,
            @RequestParam(value = "atlikta") String atliktaStr) {
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas v = vd.getVartotojas();
        Uzduotis u;
        if (todoId != null) {
            Optional<Uzduotis> ou = uzduotisDAO.findById(todoId);
            if (ou.isEmpty()) {
                return "redirect:../todo";
            }
            u = ou.get();
            if (!u.getVartotojas().equals(v)) {
                return "redirect:../todo";
            }
        } else {
            u = new Uzduotis();
            u.setVartotojas(v);
        }
        u.setPavadinimas(pavadinimas);
        u.setAprasymas(aprasymas);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date ikiKada;
        try {
            ikiKada = sdf.parse(ikiKadaStr);
        } catch (Exception ex) {
            ikiKada = null;
        }
        u.setIkiKada(ikiKada);
        if (statusas>100){
            u.setStatusas(100);
        } else if (statusas<0){
            u.setStatusas(0);
        } else {
            u.setStatusas(statusas);
        }        
        Date atlikta;
        try {
            atlikta = sdf.parse(atliktaStr);
        } catch (Exception ex) {
            atlikta = null;
        }
        if (u.getStatusas() == 100 && atlikta == null) {
            atlikta = new Date();
        }
        if (atlikta != null) {
            u.setStatusas(100);
        }
        u.setAtlikta(atlikta);
        if (todoId == null) {
            uzduotisDAO.save(u);
        }

        return "redirect:../edit?todoId=" + u.getId();
    }

    @PostMapping(path = "detail/save")
    @Transactional
    public String issaugojimasDetail(
            Authentication auth,
            @RequestParam(value = "todoId") Integer todoId,
            @RequestParam(value = "mazaId", required = false) Integer mazaId,
            @RequestParam(value = "pavadinimas") String pavadinimas,
            @RequestParam(value = "aprasymas", required = false) String aprasymas,
            @RequestParam(value = "atlikta") String atliktaStr) {
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas v = vd.getVartotojas();
        Optional<Uzduotis> ou = uzduotisDAO.findById(todoId);
        if (ou.isEmpty()) {
            return "redirect:../todo";
        }
        Uzduotis u = ou.get();
        if (!u.getVartotojas().equals(v)) {
            return "redirect:../todo";
        }
        MazaUzduotis mu;
        if (mazaId != null) {
            Optional<MazaUzduotis> oMaza = mazaUzduotisDAO.findById(mazaId);
            if (oMaza.isEmpty()) {
                return "redirect:../edit?todoId=" + todoId;
            }
            mu = oMaza.get();
            if (!mu.getUzduotis().equals(u)) {
                return "redirect:../edit?todoId=" + todoId;
            }
        } else {
            mu = new MazaUzduotis();
            mu.setUzduotis(u);
        }
        mu.setPavadinimas(pavadinimas);
        mu.setAprasymas(aprasymas);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date atlikta;
        try {
            atlikta = sdf.parse(atliktaStr);
        } catch (Exception ex) {
            atlikta = null;
        }
        mu.setAtlikta(atlikta);
        if (mazaId == null) {
            mazaUzduotisDAO.save(mu);
        }

        return "redirect:../edit?todoId=" + todoId;
    }

    @GetMapping(path = "delete")
    @Transactional
    public String delete(
            Authentication auth,
            @RequestParam(value = "todoId") Integer todoId) {
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas v = vd.getVartotojas();
        Optional<Uzduotis> ou = uzduotisDAO.findById(todoId);
        if (ou.isEmpty()) {
            return "redirect:../todo";
        }
        Uzduotis u = ou.get();
        if (!u.getVartotojas().equals(v)) {
            return "redirect:../todo";
        }
        uzduotisDAO.delete(u);
        return "redirect:../todo";
    }

    @GetMapping(path = "detail/delete")
    @Transactional
    public String deleteDetail(
            Authentication auth,
            @RequestParam(value = "todoId") Integer todoId,
            @RequestParam(value = "mazaId") Integer mazaId) {
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas v = vd.getVartotojas();
        Optional<Uzduotis> ou = uzduotisDAO.findById(todoId);
        if (ou.isEmpty()) {
            return "redirect:../todo";
        }
        Uzduotis u = ou.get();
        if (!u.getVartotojas().equals(v)) {
            return "redirect:../todo";
        }
        Optional<MazaUzduotis> oMaza = mazaUzduotisDAO.findById(mazaId);
        if (oMaza.isEmpty()) {
            return "redirect:../edit?todoId=" + todoId;
        }
        MazaUzduotis mu = oMaza.get();
        if (!mu.getUzduotis().equals(u)) {
            return "redirect:../todo";
        }
        mazaUzduotisDAO.delete(mu);
        return "redirect:../edit?todoId=" + todoId;
    }

    @GetMapping(path = "/done")
    @Transactional
    public String setDone(
            Authentication auth,
            @RequestParam(value = "todoId") Integer todoId) {
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas v = vd.getVartotojas();
        Optional<Uzduotis> ou = uzduotisDAO.findById(todoId);
        if (ou.isEmpty()) {
            return "redirect:../todo";
        }
        Uzduotis u = ou.get();
        if (!u.getVartotojas().equals(v)) {
            return "redirect:../todo";
        }
        u.setStatusas(100);
        u.setAtlikta(new Date());
        return "redirect:../todo";
    }

    @GetMapping(path = "/notDone")
    @Transactional
    public String setNotDone(
            Authentication auth,
            @RequestParam(value = "todoId") Integer todoId) {
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas v = vd.getVartotojas();
        Optional<Uzduotis> ou = uzduotisDAO.findById(todoId);
        if (ou.isEmpty()) {
            return "redirect:../todo";
        }
        Uzduotis u = ou.get();
        if (!u.getVartotojas().equals(v)) {
            return "redirect:../todo";
        }
        u.setStatusas(0);
        u.setAtlikta(null);
        return "redirect:../todo";
    }

    @GetMapping(path = "detail/done")
    @Transactional
    public String setDoneDetail(
            Authentication auth,
            @RequestParam(value = "todoId") Integer todoId,
            @RequestParam(value = "mazaId") Integer mazaId) {
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas v = vd.getVartotojas();
        Optional<Uzduotis> ou = uzduotisDAO.findById(todoId);
        if (ou.isEmpty()) {
            return "redirect:../";
        }
        Uzduotis u = ou.get();
        if (!u.getVartotojas().equals(v)) {
            return "redirect:../";
        }
        Optional<MazaUzduotis> oMaza = mazaUzduotisDAO.findById(mazaId);
        if (oMaza.isEmpty()) {
            return "redirect:../edit?todoId=" + todoId;
        }
        MazaUzduotis mu = oMaza.get();
        if (!mu.getUzduotis().equals(u)) {
            return "redirect:../todo";
        }
        mu.setAtlikta(new Date());
        return "redirect:../edit?todoId=" + todoId;
    }

    @GetMapping(path = "detail/notDone")
    @Transactional
    public String setNotDoneDetail(
            Authentication auth,
            @RequestParam(value = "todoId") Integer todoId,
            @RequestParam(value = "mazaId") Integer mazaId) {
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas v = vd.getVartotojas();
        Optional<Uzduotis> ou = uzduotisDAO.findById(todoId);
        if (ou.isEmpty()) {
            return "redirect:../";
        }
        Uzduotis u = ou.get();
        if (!u.getVartotojas().equals(v)) {
            return "redirect:../";
        }
        Optional<MazaUzduotis> oMaza = mazaUzduotisDAO.findById(mazaId);
        if (oMaza.isEmpty()) {
            return "redirect:../edit?todoId=" + todoId;
        }
        MazaUzduotis mu = oMaza.get();
        if (!mu.getUzduotis().equals(u)) {
            return "redirect:../todo";
        }
        mu.setAtlikta(null);
        return "redirect:../edit?todoId=" + todoId;
    }


}
