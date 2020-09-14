package life.majiang.community.controller;

//import life.majiang.community.androidService.ServerListener;

import life.majiang.community.mapper.AutoactionMapper;
import life.majiang.community.mapper.DoautoactionMapper;
import life.majiang.community.model.*;
import org.h2.mvstore.type.StringDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PyautoguiController {
    @Autowired
    private AutoactionMapper autoactionMapper;
    @Autowired
    private DoautoactionMapper doautoactionMapper;

    static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
    @GetMapping("/newActiondo/{actionId}")
    public String newActiondo(Model model,@PathVariable String actionId){
        //返回内容
        DoautoactionExample doautoactionExample=new DoautoactionExample();
        doautoactionExample.createCriteria().andActionIdEqualTo(Long.valueOf(actionId));
        List<Doautoaction> doautoactions=doautoactionMapper.selectByExample(doautoactionExample);

        model.addAttribute("doautoactions",doautoactions);
        model.addAttribute("actionId",actionId);
        return "doaction";
    }
    @GetMapping("/pyautogui")
    public String pyautogui(Model model,HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        AutoactionExample autoactionExample=new AutoactionExample();
        autoactionExample.createCriteria().andUserIdEqualTo(user.getId());
        List<Autoaction> autoactions=autoactionMapper.selectByExample(autoactionExample);
        model.addAttribute("autoactions",autoactions);
        return "pyautogui";
    }
    @GetMapping("/delectaction/{id}")
    public String delectaction(@PathVariable String id){
        autoactionMapper.deleteByPrimaryKey(Long.valueOf(id));
        return "redirect:/pyautogui";
    }
    @PostMapping("/newAction")
    public String newAction(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        String title= request.getParameter("title");
        Autoaction autoaction=new Autoaction();
        autoaction.setGmtCreate(System.currentTimeMillis());
        autoaction.setGmtModified(System.currentTimeMillis());
        autoaction.setName(title);
        autoaction.setUserId(user.getId());
        autoaction.setIsdelete(0);
        autoactionMapper.insert(autoaction);
        return "redirect:/pyautogui";
    }
    @PostMapping("/deletesAction")
    public String deletesAction(HttpServletRequest request){
        String ids[] = request.getParameterValues("tongzhi");
        for(int i=0;i<ids.length;i++) {
            autoactionMapper.deleteByPrimaryKey(Long.valueOf(ids[i]));
        }
        return "redirect:/pyautogui";
    }
    @PostMapping("/newActiondo/{bu}")
    public String newActiondo(HttpServletRequest request,@PathVariable String bu,@RequestParam(name = "actionId") Integer actionId){
        //需要先把以前的都删除了
        DoautoactionExample doautoactionExample=new DoautoactionExample();
        doautoactionExample.createCriteria().andActionIdEqualTo(Long.valueOf(actionId));
        doautoactionMapper.deleteByExample(doautoactionExample);
        //////////
        int bu1= Integer.parseInt(bu);
        for(int i=1;i<=bu1;i++){
            String name1="weizhi"+i;
            String name2="jian"+i;
            name1=request.getParameter(name1);
            Doautoaction doautoaction=new Doautoaction();
            doautoaction.setActionId(Long.valueOf(actionId));
            if(!name1.equals("")){
                //鼠标点击
                name2=request.getParameter(name2);
                String name3="clickcount"+i;
                name3=request.getParameter(name3);
                String name4="interval"+i;
                name4=request.getParameter(name4);
                doautoaction.setLocation(name1);
                doautoaction.setClickCount(name3);
                doautoaction.setLor(name2);
                doautoaction.setJian(name4);
                doautoaction.setTuotime("");
                doautoaction.setContent("");
                doautoactionMapper.insert(doautoaction);
                //doautoaction.setId(10L);
            }else{
                String tuoname1="tuoweizhi"+i;
                tuoname1=request.getParameter(tuoname1);
                if(!tuoname1.equals("")){
                    //鼠标拖动
                    String tuoname2="tuojian"+i;
                    tuoname2=request.getParameter(tuoname2);
                    String tuotime="tuotime"+i;
                    tuotime=request.getParameter(tuotime);
                    doautoaction.setLocation(tuoname1);
                    doautoaction.setClickCount("");
                    doautoaction.setLor(tuoname2);
                    doautoaction.setJian("");
                    doautoaction.setContent("");
                    doautoaction.setTuotime(tuotime);
                    doautoactionMapper.insert(doautoaction);
                }else{
                    //表单
                    String name5="content"+i;
                    name5=request.getParameter(name5);
                    doautoaction.setLocation("");
                    doautoaction.setClickCount("");
                    doautoaction.setLor("");
                    doautoaction.setJian("");
                    doautoaction.setTuotime("s");
                    doautoaction.setContent(name5);
                    doautoactionMapper.insert(doautoaction);
                }
            }
        }
        Autoaction autoaction=autoactionMapper.selectByPrimaryKey(Long.valueOf(actionId));
        autoaction.setGmtModified(System.currentTimeMillis());
        autoactionMapper.updateByPrimaryKey(autoaction);
        return "redirect:/pyautogui";
    }

    /*@ResponseBody//因为没加这个，一直错
    @RequestMapping(value = "/xiqu", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> xiqu(@RequestBody Map<String,Object> requestMap, HttpServletRequest request, Model model) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap=pyautogui("xiqu","","","","","","","");
       //Process pr = Runtime.getRuntime().exec("C:\\Users\\15984\\AppData\\Local\\Programs\\Python\\Python36-32\\python.exe D:\\mouselocation.py");
        return resultMap;
    }*/
    @ResponseBody//因为没加这个，一直错
    @RequestMapping(value = "/xiqu", method = RequestMethod.GET, headers = "Accept=application/json")
    public Map<String, Object> xiqu() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap=pyautogui("xiqu","","","","","","","");
        //Process pr = Runtime.getRuntime().exec("C:\\Users\\15984\\AppData\\Local\\Programs\\Python\\Python36-32\\python.exe D:\\mouselocation.py");
        return resultMap;
    }
    @ResponseBody//因为没加这个，一直错
    @RequestMapping(value = "/doAction", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> doAction(@RequestBody Map<String,Object> requestMap, HttpServletRequest request, Model model) {
        Map<String, Object> resultMap1 = new HashMap<>();
        Long actionId= Long.valueOf(requestMap.get("actionId").toString());
        int count= Integer.parseInt(requestMap.get("count").toString());
        Long jiange= Long.valueOf(requestMap.get("jiange").toString());
        DoautoactionExample doautoactionExample=new DoautoactionExample();
        doautoactionExample.createCriteria().andActionIdEqualTo(actionId);
        List<Doautoaction> doautoactions=doautoactionMapper.selectByExample(doautoactionExample);
        for(int i=0;i<count;i++){
          for(Doautoaction doautoaction:doautoactions) {
            if (!doautoaction.getLocation().equals("")) {
                String x = doautoaction.getLocation().split(",")[0];
                String y = doautoaction.getLocation().split(",")[1];
                String button = doautoaction.getLor();
                if(doautoaction.getClickCount().equals("")){ //拖动
                    String tuotime=doautoaction.getTuotime();
                    pyautogui("tuo",x,y,button,"","",tuotime,"");
                  //  Process pr = Runtime.getRuntime().exec("C:\\Users\\15984\\AppData\\Local\\Programs\\Python\\Python36-32\\python.exe D:\\webpyproject3.py "+x+" "+y+" "+tuotime+" "+button);
                }else{
                    //点击
                    String clickCount = doautoaction.getClickCount();
                    String jian = doautoaction.getJian();
                    pyautogui("dian",x,y,button,clickCount,jian,"","");
                   // Process pr = Runtime.getRuntime().exec("C:\\Users\\15984\\AppData\\Local\\Programs\\Python\\Python36-32\\python.exe D:\\webpyproject1.py " + x + " " + y + " " + button + " " + clickCount + " " + jian);
                }
            } else {//填写
                String content1=null;
                String content2=null;
                if(doautoaction.getContent().split(",").length==1){
                    content2=doautoaction.getContent().split(",")[0];
                }else{
                    content1 = doautoaction.getContent().split(",")[0];
                    content2 = doautoaction.getContent().split(",")[1];
                    int a =i;
                    if(content2.contains("x")){
                        content2 = content2.replaceAll("x", String.valueOf(a));
                    }
                    try {
                        content2= String.valueOf(jse.eval(content2));
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    }
                    content2=content1+content2;
                }
                pyautogui("biao","","","","","","",content2);
               // Process pr = Runtime.getRuntime().exec("C:\\Users\\15984\\AppData\\Local\\Programs\\Python\\Python36-32\\python.exe D:\\webpyproject2.py " + content3);
            }
          }
          try {
              Thread.sleep (jiange) ;
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
        }
        return resultMap1;
    }
    public static Map<String, Object> pyautogui(String def, String a, String b, String button, String clicks, String interval, String duration,String content){
        Map<String, Object> resultMap=new HashMap<>();
        String line = null;
        try {
            Process pr;
            if(def.equals("dian")){
                 pr= Runtime.getRuntime().exec("C:\\Users\\15984\\AppData\\Local\\Programs\\Python\\Python36-32\\python.exe D:\\webpyproject.py "+def+" "+a+" "+b+" "+button+" "+clicks+" "+interval);
            }else if(def.equals("biao")){
                pr = Runtime.getRuntime().exec("C:\\Users\\15984\\AppData\\Local\\Programs\\Python\\Python36-32\\python.exe D:\\webpyproject.py "+def+" "+content);
            }else if(def.equals("tuo")){
                pr = Runtime.getRuntime().exec("C:\\Users\\15984\\AppData\\Local\\Programs\\Python\\Python36-32\\python.exe D:\\webpyproject.py "+def+" "+a+" "+b+" "+button+" "+duration);
            }else{
                pr = Runtime.getRuntime().exec("C:\\Users\\15984\\AppData\\Local\\Programs\\Python\\Python36-32\\python.exe D:\\webpyproject.py "+def);
            }
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(pr.getInputStream()));
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                resultMap.put("line", line);
            }
            in.close();
            pr.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }
}
