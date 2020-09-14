package life.majiang.community.controller;

import com.baidu.aip.face.AipFace;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.TagsMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.*;
//import life.majiang.community.util.shiyan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ShiyanController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private TagsMapper tagsMapper;


    @GetMapping("/shiyan7")
    public String shiyan7(Model model, HttpServletRequest request, @RequestParam(name="lng") String lng,@RequestParam(name="lat") String lat) {
        request.setAttribute("lng",lng);
        request.setAttribute("lat",lat);
        return "shiyan7";
    }//in4DvX3Bj7iUq7nNpu8PYCy1xExOXFLQ


    @RequestMapping("/QuestionTagPublish")
    @ResponseBody
    public List<Tags> questionTagPublish() {
        TagsExample tagsExample=new TagsExample();
        tagsExample.createCriteria().andIdIsNotNull();
         List<Tags> tags=tagsMapper.selectByExample(tagsExample);
        return tags;
    }
    @RequestMapping("/QuestionTagPublish1")
    @ResponseBody
    public List<Tags> questionTagPublish1() {
        TagsExample tagsExample=new TagsExample();
        tagsExample.createCriteria().andCountNotEqualTo(0);
        List<Tags> tags=tagsMapper.selectByExample(tagsExample);
        return tags;
    }
    @RequestMapping("/QuestionTagPublish3")
    @ResponseBody
    public List<Tagclass> questionTagPublish3() {

        List<Tagclass> tagclass = new ArrayList<>();
        TagsExample tagsExample=new TagsExample();
        tagsExample.createCriteria().andIdIsNotNull();
        List<Tags> tags=tagsMapper.selectByExample(tagsExample);//最好是读取数据库中的类个数，循环生成tag

        List<Echarts> tag1=new ArrayList<>();
        List<Echarts> tag2=new ArrayList<>();
        List<Echarts> tag3=new ArrayList<>();
        List<Echarts> tag4=new ArrayList<>();
        List<Echarts> tag5=new ArrayList<>();
        List<Echarts> tag6=new ArrayList<>();
        Tagclass program = new Tagclass();
        Tagclass framework = new Tagclass();
        Tagclass server = new Tagclass(); Tagclass db = new Tagclass();
        Tagclass tool = new Tagclass(); Tagclass other = new Tagclass();

        for(int i=0;i<tags.size();i++){
            if(tags.get(i).getCla().equals("开发语言")){
                program.setCategoryName("开发语言");
                Echarts echarts1=new Echarts();
                echarts1.setName(tags.get(i).getName());
                echarts1.setValue(tags.get(i).getCount());
                tag1.add(echarts1);
            }
            if(tags.get(i).getCla().equals("平台框架")){
                framework.setCategoryName("平台框架");
                Echarts echarts2=new Echarts();
                echarts2.setName(tags.get(i).getName());
                echarts2.setValue(tags.get(i).getCount());
                tag2.add(echarts2);
            }
            if(tags.get(i).getCla().equals("服务器")){
                server.setCategoryName("服务器");
                Echarts echarts3=new Echarts();
                echarts3.setName(tags.get(i).getName());
                echarts3.setValue(tags.get(i).getCount());
                tag3.add(echarts3);
            }
            if(tags.get(i).getCla().equals("数据库")){
                db.setCategoryName("数据库");
                Echarts echarts4=new Echarts();
                echarts4.setName(tags.get(i).getName());
                echarts4.setValue(tags.get(i).getCount());
                tag4.add(echarts4);
            }
            if(tags.get(i).getCla().equals("开发工具")){
                tool.setCategoryName("开发工具");
                Echarts echarts5=new Echarts();
                echarts5.setName(tags.get(i).getName());
                echarts5.setValue(tags.get(i).getCount());
                tag5.add(echarts5);
            }
            if(tags.get(i).getCla().equals("其它")){
                other.setCategoryName("其它");
                Echarts echarts6=new Echarts();
                echarts6.setName(tags.get(i).getName());
                echarts6.setValue(tags.get(i).getCount());
                tag6.add(echarts6);
            }
        }
        program.setTags(tag1);
        framework.setTags(tag2);
        server.setTags(tag3);
        db.setTags(tag4);
        tool.setTags(tag5);
        other.setTags(tag6);
        tagclass.add(program);
        tagclass.add(framework);
        tagclass.add(server);
        tagclass.add(db);
        tagclass.add(tool);
        tagclass.add(other);
        System.out.println(tagclass);
        return tagclass;
    }
    @RequestMapping("/QuestionTagPublish2")
    @ResponseBody
    public List<DateCount> questionTagPublish2() {
        QuestionExample questionExample=new QuestionExample();
        questionExample.createCriteria().andIdIsNotNull();
        List<Question> list=questionMapper.selectByExample(questionExample);
        List<DateCount> list1 = new ArrayList<DateCount>();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String temp=dateformat.format(list.get(0).getGmtCreate());
        String dateStr=null;
        int count=0;
        for (int i=0;i<list.size();i++){
            dateStr= dateformat.format(list.get(i).getGmtCreate());
           // System.out.println(dateStr);
            //temp=dateStr;
            DateCount dateCount=new DateCount();
            if(!dateStr.equals(temp)||i==list.size()-1){
                dateCount.setDate(temp);
                temp=dateStr;
                if(i==list.size()-1){
                   count++;
                }
                dateCount.setValue(count);
                list1.add(dateCount);
                count=0;
               // break;
            }
            count++;

        }
        return list1;
    }

    }
