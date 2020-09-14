package life.majiang.community.cache;

import life.majiang.community.dto.TagDTO;
import life.majiang.community.mapper.TagsMapper;
import life.majiang.community.model.Tags;
import life.majiang.community.model.TagsExample;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by codedrinker on 2019/6/5.
 */
@Service
public class TagCache {
    @Autowired
    private TagsMapper tagsMapper;

    public List<TagDTO> get() {
        List<TagDTO> tagDTOS = new ArrayList<>();
        //最好通过数据库获取
        TagsExample tagsExample=new TagsExample();
        tagsExample.createCriteria().andIdIsNotNull();
        List<Tags> tags=tagsMapper.selectByExample(tagsExample);//最好是读取数据库中的类个数，循环生成tag

        List<String> tag1=new ArrayList<>();
        List<String> tag2=new ArrayList<>();
        List<String> tag3=new ArrayList<>();
        List<String> tag4=new ArrayList<>();
        List<String> tag5=new ArrayList<>();
        List<String> tag6=new ArrayList<>();
        TagDTO program = new TagDTO();
        TagDTO framework = new TagDTO();
        TagDTO server = new TagDTO(); TagDTO db = new TagDTO();
        TagDTO tool = new TagDTO(); TagDTO other = new TagDTO();
        for(int i=0;i<tags.size();i++){
            if(tags.get(i).getCla().equals("开发语言")){
                program.setCategoryName("开发语言");
                tag1.add(tags.get(i).getName());
            }
            if(tags.get(i).getCla().equals("平台框架")){
                framework.setCategoryName("平台框架");
                tag2.add(tags.get(i).getName());
            }
            if(tags.get(i).getCla().equals("服务器")){
                server.setCategoryName("服务器");
                tag3.add(tags.get(i).getName());
            }
            if(tags.get(i).getCla().equals("数据库")){
                db.setCategoryName("数据库");
                tag4.add(tags.get(i).getName());
            }
            if(tags.get(i).getCla().equals("开发工具")){
                tool.setCategoryName("开发工具");
                tag5.add(tags.get(i).getName());
            }
            if(tags.get(i).getCla().equals("其它")){
                other.setCategoryName("其它");
                tag6.add(tags.get(i).getName());
            }
        }
        program.setTags(tag1);
        framework.setTags(tag2);
        server.setTags(tag3);
        db.setTags(tag4);
        tool.setTags(tag5);
        other.setTags(tag6);

        tagDTOS.add(program);
        tagDTOS.add(framework);
        tagDTOS.add(server);
        tagDTOS.add(db);
        tagDTOS.add(tool);
        tagDTOS.add(other);
       /* TagDTO program = new TagDTO();
        program.setCategoryName("开发语言");
        program.setTags(Arrays.asList("javascript", "php", "css", "html", "html5", "java", "node.js", "python", "c++", "c", "golang", "objective-c", "typescript", "shell", "swift", "c#", "sass", "ruby", "bash", "less", "asp.net", "lua", "scala", "coffeescript", "actionscript", "rust", "erlang", "perl"));
        tagDTOS.add(program);

        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台框架");
        framework.setTags(Arrays.asList("laravel", "spring", "express", "django", "flask", "yii", "ruby-on-rails", "tornado", "koa", "struts"));
        tagDTOS.add(framework);

        TagDTO server = new TagDTO();
        server.setCategoryName("服务器");
        server.setTags(Arrays.asList("linux", "nginx", "docker", "apache", "ubuntu", "centos", "缓存", "tomcat", "负载均衡", "unix", "hadoop", "windows-server"));
        tagDTOS.add(server);

        TagDTO db = new TagDTO();
        db.setCategoryName("数据库");
        db.setTags(Arrays.asList("mysql", "redis", "mongodb", "sql", "oracle", "tomcat", "sqlserver", "postgresql", "sqlite"));
        tagDTOS.add(db);

        TagDTO tool = new TagDTO();
        tool.setCategoryName("开发工具");
        tool.setTags(Arrays.asList("git", "github", "visual-studio-code", "vim", "sublime-text", "xcode","intellij-idea", "eclipse", "maven", "ide", "svn", "visual-studio", "atom emacs", "textmate", "hg"));
        tagDTOS.add(tool);

        TagDTO other = new TagDTO();
        other.setCategoryName("其它");
        other.setTags(Arrays.asList("其它"));
        tagDTOS.add(other);*/
        return tagDTOS;
    }

    public String filterInvalid(String tags) {
        String[] split = StringUtils.split(tags, ",");
        List<TagDTO> tagDTOS = get();

        List<String> tagList = tagDTOS.stream().flatMap(tag -> tag.getTags().stream()).collect(Collectors.toList());
        String invalid = Arrays.stream(split).filter(t -> StringUtils.isBlank(t) || !tagList.contains(t)).collect(Collectors.joining(","));
        return invalid;
    }

    /*public void main(String[] args) {
        int i = (5 - 1) >>> 1;
        System.out.println(i);
    }*/
}
