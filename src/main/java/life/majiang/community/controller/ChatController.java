package life.majiang.community.controller;

import life.majiang.community.dto.AttentionDTO;
import life.majiang.community.dto.ContactDTO;
import life.majiang.community.mapper.ContactMapper;
import life.majiang.community.mapper.MessageMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.*;
import life.majiang.community.service.AttentionService;
import life.majiang.community.service.ContactService;
import life.majiang.community.websocket.WebSocketOneToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class ChatController {
    @Autowired
    private WebSocketOneToOne webSocketOneToOne;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private AttentionService attentionService;
    @Autowired
    private ContactMapper contactMapper;
    @Autowired
    private ContactService contactService;
    /**
     * 登录界面直接聊天
     * **/
    @RequestMapping("/chat/{otheruserId}")
    public String chat(@PathVariable(name = "otheruserId") Long otheruserId, HttpSession session, Model model,HttpServletRequest request){
        //查询用户聊天历史纪录,只获取12条
        User user= (User)request.getSession().getAttribute("user");
        List<Message> messages=new ArrayList<>();
        List<ContactDTO> contactDTOList = new ArrayList<>();
        User otherUser=new User();
        String socketId=null;

        ////找聊天纪录
        ContactExample example1 = new ContactExample();
        example1.createCriteria()
                .andUser1EqualTo(user.getId());
        List<Contact> contacts1= contactMapper.selectByExample(example1);
        //user2
        ContactExample example2 = new ContactExample();
        example2.createCriteria()
                .andUser2EqualTo(user.getId());
        List<Contact> contacts2= contactMapper.selectByExample(example2);
        //整合
        contacts1.addAll(contacts2);
        if(otheruserId==0&&contacts1.size()!=0){
            //////如果有聊天纪录
             List<Contact> contacts3=new ArrayList<>();
             int j=0;
             for(Contact contact:contacts1){
                    if(contact.getUser1()==contact.getUser2()&&j==0){
                        contacts3.add(contact);
                        j=1;
                    }else if(contact.getUser1()!=contact.getUser2()){
                        contacts3.add(contact);
                    }
                }
                //按时间排顺序
                Collections.sort(contacts3, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact contact1, Contact contact2) {
                        Long gmt1 = contact1.getGmt();
                        Long gmt2 = contact2.getGmt();
                        //可以按Question对象的其他属性排序，只要属性支持compareTo方法
                        return gmt2.compareTo(gmt1);
                    }
                });
                //找到首个
                Contact contact0=contacts3.get(0);
                if(contacts1.size()!=0) {
                    if(contact0.getUser2()==user.getId()) {
                        return "redirect:/chat/"+contact0.getUser1();
                    }else {
                        return "redirect:/chat/"+contact0.getUser2();
                    }
                }
             }
           if(otheruserId!=0){
               otherUser=userMapper.selectByPrimaryKey(otheruserId);
               List<Contact> contacts3=new ArrayList<>();
               int j=0;
               for(Contact contact:contacts1){
                   if(contact.getUser1()==contact.getUser2()&&j==0){
                       contacts3.add(contact);
                       j=1;
                   }else if(contact.getUser1()!=contact.getUser2()){
                       contacts3.add(contact);
                   }
               }
               //按时间排顺序
               Collections.sort(contacts3, new Comparator<Contact>() {
                   @Override
                   public int compare(Contact contact1, Contact contact2) {
                       Long gmt1 = contact1.getGmt();
                       Long gmt2 = contact2.getGmt();
                       //可以按Question对象的其他属性排序，只要属性支持compareTo方法
                       return gmt2.compareTo(gmt1);
                   }
               });
               //找到首个
               Contact contact0=contacts3.get(0);
               socketId=contact0.getSocketid();

                //找到不是user的touser
                for (Contact c : contacts3) {
                    User touser =new User();
                    if(c.getUser1().longValue()==user.getId().longValue()){
                        touser = userMapper.selectByPrimaryKey(c.getUser2());
                    }else{
                        touser = userMapper.selectByPrimaryKey(c.getUser1());
                    }
                    //获取其它联系人的未读条数
                    int flagreadnumber = 0;
                    if(c.getId().longValue()!=contact0.getId().longValue()) {
                        List<Message> messages1 = new ArrayList<>();
                        MessageExample messageExample1 = new MessageExample();
                        messageExample1.createCriteria().andContactIdEqualTo(c.getId());
                        messages1 = messageMapper.selectByExample(messageExample1);
                        for (Message message : messages1) {
                            if (message.getFromuser().longValue() != user.getId().longValue() && message.getReadflag() == 0) {
                                flagreadnumber++;
                            }
                        }
                    }
                    ContactDTO contactDTO = new ContactDTO();
                    contactDTO.setFlagreadnumber(flagreadnumber);
                    contactDTO.setId(c.getId());
                    contactDTO.setGmt(c.getGmt());
                    contactDTO.setUser1(user);
                    contactDTO.setUser2(touser);
                    contactDTO.setSocketId(c.getSocketid());
                    // contactDTO.setMessages(messages);
                    contactDTOList.add(contactDTO);
                }
                //得到当前对话的message
                MessageExample messageExample=new MessageExample();
                messageExample.createCriteria().andContactIdEqualTo(contact0.getId());
                messages=messageMapper.selectByExample(messageExample);
                contactService.readMeassge(request,user.getId());
                ////////////联系人及关注的人s
          }
        List<AttentionDTO> attentionDTOS= attentionService.list(user.getId());
        model.addAttribute("attentionDTOS", attentionDTOS);
        model.addAttribute("messages",messages);
        model.addAttribute("contactDTOList",contactDTOList);
        model.addAttribute("socketId",socketId);
        model.addAttribute("otheruserId",otheruserId);
        model.addAttribute("otherUser",otherUser);
        return "chat";
    }

    /**
     * 发送消息 消息内容,发送人,接收人,会话标识
     * **/
   /* @RequestMapping("message")
    public void message(String msg,String from,String to,String socketId){
        //写入message到数据库
        System.out.println(from+"aaa"+to);
        webSocketOneToOne.send(msg, from, to, socketId);
    }*/
}