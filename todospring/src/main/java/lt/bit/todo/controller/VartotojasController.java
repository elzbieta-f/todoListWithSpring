package lt.bit.todo.controller;

import lt.bit.todo.config.VartotojasDetails;
import lt.bit.todo.dao.VartotojasDAO;
import lt.bit.todo.data.Uzduotis;
import lt.bit.todo.data.Vartotojas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path = "vartotojas")
public class VartotojasController {

    @Autowired
    VartotojasDAO vartotojasDAO;

    @Autowired
    private PasswordEncoder pe;

    @GetMapping(path = "new")
    public ModelAndView naujas() {
        ModelAndView mv = new ModelAndView("register");
        return mv;
    }

    @PostMapping(path = "register")
    @Transactional
    public String register(
            @RequestParam(value = "username") String vardas,
            @RequestParam(value = "password") String slaptazodis,
            @RequestParam(value = "password2") String slaptazodis2) {

        if (slaptazodis.equals(slaptazodis2)) {
            Vartotojas v = new Vartotojas();
            v.setVardas(vardas);
            v.setSlaptazodis(pe.encode(slaptazodis));
            vartotojasDAO.save(v);
            return "redirect:../";
        } else {
            return "redirect:../";
        }
    }

    @GetMapping(path = "change")
    public ModelAndView changeForm() {
        ModelAndView mv = new ModelAndView("change");
        return mv;
    }

    @PostMapping(path = "changePassword")
    @Transactional
    public String changePassword(
            Authentication auth,
            @RequestParam(value = "password") String old,
            @RequestParam(value = "new") String naujas,
            @RequestParam(value = "new2") String naujas2) {
        VartotojasDetails vd = (VartotojasDetails) auth.getPrincipal();
        Vartotojas v = vd.getVartotojas();
        if (pe.matches(old, v.getSlaptazodis()) && naujas.equals(naujas2)) {
            v.setSlaptazodis(pe.encode(naujas));
            vartotojasDAO.save(v);
            return "redirect:../todo?passChange=success";
        } else {
            return "redirect:../todo?passChange=fail";
        }
    }

}
