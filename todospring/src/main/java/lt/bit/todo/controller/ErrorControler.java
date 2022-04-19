
package lt.bit.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path = "error")
public class ErrorControler {
    
    @GetMapping
    public ModelAndView klaida(@RequestParam("klaida") String klaida) {
        ModelAndView mv = new ModelAndView("error");
        mv.addObject(klaida);
        return mv;
    }
    
}
