package life.majiang.community.controller;

import life.majiang.community.cache.HotTagCache;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.mapper.NoticeMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.mapper.WebrootMapper;
import life.majiang.community.model.Notice;
import life.majiang.community.model.User;
import life.majiang.community.model.Webroot;
import life.majiang.community.model.WebrootExample;
import life.majiang.community.service.QuestionService;
import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
@Controller
public class ProjectsController {
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/face")
    public String face(Model model) {
        return "faceProject";
    }
    @GetMapping("/shi/{action}")
    public String shi(@PathVariable(name = "action") String action, Model model) {
        if ("posts".equals(action)) {

        } else if ("lables".equals(action)) {

        }
        return "shi";
    }

}
