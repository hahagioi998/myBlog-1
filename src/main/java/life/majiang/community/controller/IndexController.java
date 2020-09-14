package life.majiang.community.controller;

//import life.majiang.community.androidService.ServerListener;

import life.majiang.community.cache.HotTagCache;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.model.User;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private HotTagCache hotTagCache;
    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(HttpServletRequest request, Model model, @RequestParam(name="page",defaultValue="1") Integer page, @RequestParam(name="size",defaultValue="5") Integer size,@RequestParam(name="search",required = false) String search,@RequestParam(name="tag",required = false) String tag){
            PaginationDTO paginationDTO=questionService.list(search,tag,page,size);
            ArrayList<User> userList = new ArrayList<User>();
            List<String> tags=hotTagCache.getHots();

          //  new ServerListener().start();

        String section2="home";
        model.addAttribute("section2",section2);
        model.addAttribute("pagination",paginationDTO);
        model.addAttribute("search",search);
        model.addAttribute("tag",tag);
        model.addAttribute("tags",tags);
        return "index";//.getUser().getId()
    }
}
