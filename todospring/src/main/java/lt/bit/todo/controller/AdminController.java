package lt.bit.todo.controller;

import java.util.List;
import java.util.Optional;
import lt.bit.todo.config.VartotojasDetails;
import lt.bit.todo.dao.VartotojasDAO;
import lt.bit.todo.data.Vartotojas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path = "admin")
public class AdminController {

    @Autowired
    VartotojasDAO vartotojasDAO;

    @GetMapping
    public ModelAndView vartotojaiList(
            Authentication auth,
            @RequestParam(value = "filter", required = false) String filter) {
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas admin = vd.getVartotojas();
        List<Vartotojas> list;
        if (filter != null && !filter.trim().equals("")) {
            filter = "%" + filter + "%";
            list = vartotojasDAO.byVardasFilter(filter);
        } else {
            list = vartotojasDAO.getVartotojai();
        }
        ModelAndView mv = new ModelAndView("vartotojai");
        mv.addObject("admin", admin);
        mv.addObject("vartotojai", list);
        return mv;
    }

    @GetMapping(path = "set")
    @Transactional
    public String setAdmin(Authentication auth,
            @RequestParam(value = "userId") Integer userId) {
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas admin = vd.getVartotojas();
        Optional<Vartotojas> ov = vartotojasDAO.findById(userId);
        if (ov.isEmpty()) {
            return "redirect:../admin";
        }
        Vartotojas v = ov.get();
        if (v.equals(admin)) {
            return "redirect:../admin";
        }
        v.setAdmin(Boolean.TRUE);
        return "redirect:../admin";
    }

    @GetMapping(path = "remove")
    @Transactional
    public String removeAdmin(Authentication auth,
            @RequestParam(value = "userId") Integer userId) {
        Optional<Vartotojas> ov = vartotojasDAO.findById(userId);
        if (ov.isEmpty()) {
            return "redirect:../admin";
        }
        Vartotojas v = ov.get();
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas admin = vd.getVartotojas();
        if (v.equals(admin)) {
            return "redirect:../admin";
        }
        v.setAdmin(Boolean.FALSE);
        return "redirect:../admin";
    }

    @GetMapping(path = "delete")
    @Transactional
    public String delete(Authentication auth,
            @RequestParam(value = "userId") Integer userId) {
        Optional<Vartotojas> ov = vartotojasDAO.findById(userId);
        if (ov.isEmpty()) {
            return "redirect:../admin";
        }
        Vartotojas v = ov.get();
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas admin = vd.getVartotojas();
        if (v.equals(admin)) {
            return "redirect:../admin";
        }
        vartotojasDAO.delete(v);
        return "redirect:../admin";
    }
}
