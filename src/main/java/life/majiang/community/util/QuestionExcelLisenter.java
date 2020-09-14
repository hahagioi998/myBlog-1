package life.majiang.community.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.model.Question;
import life.majiang.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;

@RestController
public class QuestionExcelLisenter extends AnalysisEventListener<Question> {
    //一行一行读取excel内容
    //spring中处理service,controller可以调用mapper,其她类调用需要添加
    // @PostConstruct
    //    public void init() {
    //        webSocketOneToOne = this;
    //    }
    //引用时需要写webSocketOneToOne.xxxmapper
    @Autowired
    private QuestionMapper questionMapper;

    public static QuestionExcelLisenter questionExcelLisenter;

    @PostConstruct
     public void init() {
        questionExcelLisenter = this;
     }

    @Override
    public void invoke(Question data, AnalysisContext analysisContext) {
        Question question=new Question();
        question.setTitle(data.getTitle());
        question.setDescription(data.getDescription());
        question.setGmtModified(System.currentTimeMillis());
        question.setGmtCreate(System.currentTimeMillis());
        question.setCreator(data.getCreator());
        question.setCommentCount(0);
        question.setViewCount(0);
        question.setLikeCount(0);
        question.setTag(data.getTag());

        questionExcelLisenter.questionMapper.insert(question);
    }
    //读取表头内容
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("表头："+headMap);
    }
    //读取完成之后
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) { }
}
