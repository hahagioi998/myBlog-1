package life.majiang.community.controller;

import com.baidu.aip.face.AipFace;
import life.majiang.community.mapper.AccesstokenMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Accesstoken;
import life.majiang.community.model.User;
import life.majiang.community.util.face.GsonUtils;
import life.majiang.community.util.face.HttpUtil;
import net.sf.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class LoginController {
    public static final String APP_ID = "";
    public static final String API_KEY = "";
    public static final String SECRET_KEY = "";
   // public static final String accessToken = "";
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AccesstokenMapper accesstokenMapper;
    @GetMapping("userlogin")
    public String login(Model model){
        String section2="login";
        model.addAttribute("section2",section2);
        return  "login";
    }

    @GetMapping("userregister")
    public String register(Model model){
        String section2="login";
        model.addAttribute("section2",section2);
        return  "register";
    }

    @GetMapping("hello2")
    public String hello2(){
        return  "introduce";
    }

    //人脸识别登录，添加人脸
    @ResponseBody//因为没加这个，一直错
    @RequestMapping(value = "/face1", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> face1(@RequestBody Map<String,Object> requestMap, HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");
        String imgData = requestMap.get("imgData").toString();

        String result= null;
        try {
            result = add(imgData,user.getId().toString(),user.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
        Map<String,Object> result1= JSONObject.fromObject(result);
        Map<String, Object> resultMap = new HashMap<>();
        if(result1.get("error_msg").equals("SUCCESS")){   //还有一些情况
            resultMap.put("code",200);//录入成功
            user.setFace("1");
            userMapper.updateByPrimaryKey(user);
        }else if(result1.get("error_msg").equals("face is fuzzy")){//face is fuzzy  脸模糊，liveness check fail   活性检测失败
            resultMap.put("code",201);
        }else{
            resultMap.put("code",202);
        }
        //返回一些信息
        return resultMap;
    }
    //人脸识别登录，添加人脸,未录入登录错误，未写
    @ResponseBody
    @RequestMapping(value = "/face2", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> face2(@RequestBody Map<String,Object> requestMap, HttpServletRequest request,HttpServletResponse response) {
        //User user = (User) request.getSession().getAttribute("user");
        String imgData = requestMap.get("imgData").toString();
        String result= null;
        try {
            result = faceSearch(imgData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
        Map<String, Object> resultMap = new HashMap<>();
        //返回一些信息
        Map<String,Object> result1= JSONObject.fromObject(result);
        if(result1.get("error_msg").equals("SUCCESS")){   //还有一些情况
            resultMap.put("code",200);//识别成功
            String userid=result1.get("result").toString();
            Object user_list = JSONObject.fromObject(userid).get("user_list");
            String userList=user_list.toString();
            userList=userList.substring(1,userList.length()-1);
            String score= String.valueOf(JSONObject.fromObject(userList).get("score"));//匹配度
            if(Integer.parseInt(score)>50){
                Object userId=JSONObject.fromObject(userList).get("user_id");
                User user=userMapper.selectByPrimaryKey(Long.valueOf(userId.toString()));
                String token = UUID.randomUUID().toString();
                user.setToken(token);
                user.setGmtModified(System.currentTimeMillis());
                userMapper.updateByPrimaryKey(user);
                response.addCookie(new Cookie("token", token));
                request.getSession().setAttribute("user", user);
                resultMap.put("score",51);//匹配度
            }else{
                resultMap.put("score",0);//匹配度
            }
        }else if(result1.get("error_msg").equals("face is fuzzy")){//face is fuzzy  脸模糊，liveness check fail   活性检测失败
            resultMap.put("code",201);
            resultMap.put("score",0);//匹配度
        }else{
            resultMap.put("code",202);
            resultMap.put("score",0);//匹配度
        }
            //result:{"error_code":0,"error_msg":"SUCCESS"
        //result:{"error_code":223121,"error_msg":"left eye is occlusion","log_id":4589994500175,"timestamp":1597395869,"cached":0,"result":null}
        return resultMap;
    }
    @ResponseBody//因为没加这个，一直错
    @RequestMapping(value = "/face3", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> face3(@RequestBody Map<String,Object> requestMap, HttpServletRequest request, Model model) {
       // User user = (User) request.getSession().getAttribute("user");
        String imgData = requestMap.get("img").toString();
        String imgData1 = requestMap.get("img1").toString();
        org.json.JSONObject res1=match1(imgData);
        org.json.JSONObject res2=match1(imgData1);
       // String talk=null;
        Map<String, Object> result = new HashMap<>();

        Map<String, String> result1= dataprocessing(res1);
        Map<String, String> result2= dataprocessing(res2);

        result.put("talk1",result1.get("talkface_type"));
        result.put("talk2",result2.get("talkface_type"));

        if(Double.parseDouble(result1.get("championappearance"))>Double.parseDouble(result2.get("championappearance"))){
            result.put("champion","img");
        }else if(Double.parseDouble(result1.get("championappearance"))==Double.parseDouble(result2.get("championappearance"))){
            result.put("champion","img0");
        }else{
            result.put("champion","img1");
        }

        result.put("championappearance",result1.get("championappearance"));  //颜值
        result.put("runnerupappearance",result2.get("championappearance"));
        //result.put("similarity",res.substring(113,124));
        //返回一些信息
        return result;
    }
    public static org.json.JSONObject match1(String image) {
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        //String image = Base64Util.encode(path);
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age,beauty,expression,face_shape,gender,glasses,race,eye_status,emotion,face_type");
        org.json.JSONObject res = client.detect(image,"BASE64", options);
        if (res.getString("error_msg") != null && res.getString("error_msg").equals("SUCCESS")) {
            JSONArray faceList = res.getJSONObject("result").getJSONArray("face_list");
            org.json.JSONObject jsonObject = faceList.getJSONObject(0);
            System.out.println("年龄：" + jsonObject.getInt("age"));
            // 美丑打分，范围0-100，越大表示越美。
            System.out.println("美丑打分：" + jsonObject.getDouble("beauty"));
            // none:不笑；smile:微笑；laugh:大笑
            System.out.println("表情：" + jsonObject.getJSONObject("expression").getString("type"));
            // square: 正方形 triangle:三角形 oval: 椭圆 heart: 心形 round: 圆形
            System.out.println("脸型：" + jsonObject.getJSONObject("face_shape").getString("type"));
            // male:男性 female:女性
            System.out.println("性别：" + jsonObject.getJSONObject("gender").getString("type"));
            // yellow: 黄种人 white: 白种人 black:黑种人 arabs: 阿拉伯人
            System.out.println("人种：" + jsonObject.getJSONObject("race").getString("type"));
            // [0,1]取值，越接近0闭合的可能性越大
            System.out.println("右眼状态（睁开/闭合）：" + jsonObject.getJSONObject("eye_status").getInt("right_eye"));
            System.out.println("左眼状态（睁开/闭合）：" + jsonObject.getJSONObject("eye_status").getInt("left_eye"));
            System.out.println("人脸置信度，范围【0~1】：" + jsonObject.getInt("face_probability"));
            // none:无眼镜，common:普通眼镜，sun:墨镜
            System.out.println("是否带眼镜：" + jsonObject.getJSONObject("glasses").getString("type"));
            // angry:愤怒 disgust:厌恶 fear:恐惧 happy:高兴 sad:伤心 surprise:惊讶 neutral:无情绪
            System.out.println("情绪：" + jsonObject.getJSONObject("emotion").getString("type"));
            // human: 真实人脸 cartoon: 卡通人脸
            System.out.println("真实人脸/卡通人脸：" + jsonObject.getJSONObject("face_type").getString("type"));
            System.out.println("face_token：" + jsonObject.getString("face_token"));

        } else {
            System.out.println(res.toString());
        }
        return res;
    }
    //数据处理
    public static Map<String, String> dataprocessing(org.json.JSONObject res) {
        Map<String, String> result = new HashMap<String, String>();
        if (res.getString("error_msg") != null && res.getString("error_msg").equals("SUCCESS")) {
            JSONArray faceList = res.getJSONObject("result").getJSONArray("face_list");
            org.json.JSONObject jsonObject = faceList.getJSONObject(0);
            // 美丑打分，范围0-100，越大表示越美。
            result.put("championappearance", String.valueOf(jsonObject.getDouble("beauty")));
            String sex=jsonObject.getJSONObject("gender").getString("type");
            String talkface_type=null;
            if(sex.equals("male")){//男
                String face_type=jsonObject.getJSONObject("face_type").getString("type");
                if(face_type.equals("human")){
                    String talksex="男   ";
                    String talkrace=null;
                    String race=jsonObject.getJSONObject("race").getString("type");
                    if(race.equals("yellow")){
                        talkrace="亚洲人";
                    }else if(race.equals("white")){
                        talkrace="西方人";
                    }else if(race.equals("black")){
                        talkrace="非洲人";
                    }else{
                        talkrace="阿拉伯人";
                    }
                    String talkage=null;
                    int age=jsonObject.getInt("age");
                    if(age>=0&&age<=16){
                        talkage=age+"岁  ";
                    }else if(age>17&&age<25){
                        talkage=age+"岁  ";
                    }else if(age>=25&&age<=35){
                        talkage=age+"岁   ";
                    }else if(age>35&&age<=50){
                        talkage=age+"岁   ";
                    }else if(age>50&&age<=70){
                        talkage=age+"岁   ";
                    }else if(age>70&&age<=90){
                        talkage=age+"岁   ";
                    }else{
                        talkage=age+"岁   ";
                    }
                    String talkexpression=null;
                    String expression=jsonObject.getJSONObject("expression").getString("type");
                    if(expression.equals("none")){
                        talkexpression="听说有表情会加分哦，";
                    }else if(expression.equals("smile")){
                        talkexpression="微微一笑，";
                    }else if(expression.equals("laugh")){
                        talkexpression="你笑起来真好看，";
                    }else{
                        talkexpression="好吧，我看不出来你的表情，";
                    }
                    String talkglasses=null;
                    String glasses=jsonObject.getJSONObject("glasses").getString("type");
                    if(glasses.equals("none")){
                        talkglasses="试试戴上眼镜会加分不，";
                    }else if(glasses.equals("common")){
                        talkglasses="试试不戴眼镜会加分不，";
                    }else if(glasses.equals("sun")){
                        talkglasses="这墨镜，社会，";
                    }
                    String talkemotion=null;
                    String emotion=jsonObject.getJSONObject("emotion").getString("type");
                    if(emotion.equals("angry")){
                        talkemotion="你好像心情不好，";
                    }else if(emotion.equals("disgust")){
                        talkemotion="你是在嫌弃我吗？";
                    }else if(emotion.equals("fear")){
                        talkemotion="你怎么有点害怕，";
                    }else if(emotion.equals("happy")){
                        talkemotion="你笑起来像个小帅哥，";
                    }else if(emotion.equals("sad")){
                        talkemotion="你应该开心点，";
                    }else if(emotion.equals("surprise")){
                        talkemotion="什么事这个惊讶，";
                    }else{
                        talkemotion="你如果笑起来应该挺帅的，";
                    }
                    talkface_type=talksex+talkage+talkemotion+talkexpression+talkglasses;//+talkrace;
                }else{
                    talkface_type="不要用卡通漫画脸忽悠我，我拒绝。";
                }
            }else{
                //女
                String face_type=jsonObject.getJSONObject("face_type").getString("type");
                if(face_type.equals("human")){
                    String talksex="女  ，";
                    String talkrace=null;
                    String race=jsonObject.getJSONObject("race").getString("type");
                    if(race.equals("yellow")){
                        talkrace="亚洲人";
                    }else if(race.equals("white")){
                        talkrace="西方人";
                    }else if(race.equals("black")){
                        talkrace="非洲人";
                    }else{
                        talkrace="阿拉伯人";
                    }
                    String talkage=null;
                    int age=jsonObject.getInt("age");
                    if(age>=0&&age<=16){
                        talkage=age+"岁";
                    }else if(age>17&&age<25){
                        talkage=age+"岁";
                    }else if(age>=25&&age<=35){
                        talkage=age+"岁  ";
                    }else if(age>35&&age<=50){
                        talkage=age+"岁   ";
                    }else if(age>50&&age<=70){
                        talkage=age+"岁  ";
                    }else if(age>70&&age<=90){
                        talkage=age+"岁   ";
                    }else{
                        talkage=age+"岁   ";
                    }
                    String talkexpression=null;
                    String expression=jsonObject.getJSONObject("expression").getString("type");
                    if(expression.equals("none")){
                        talkexpression="听说有表情会加分哦，";
                    }else if(expression.equals("smile")){
                        talkexpression="微微一笑";
                    }else if(expression.equals("laugh")){
                        talkexpression="你笑起来真好看";
                    }else{
                        talkexpression="好吧，我看不出来你的表情，";
                    }
                    String talkglasses=null;
                    String glasses=jsonObject.getJSONObject("glasses").getString("type");
                    if(expression.equals("none")){
                        talkglasses="试试戴上眼镜会加分不，";
                    }else if(expression.equals("common")){
                        talkglasses="试试不戴眼镜会加分不，";
                    }else if(expression.equals("sun")){
                        talkglasses="墨镜，社会，";
                    }
                    String talkemotion=null;
                    String emotion=jsonObject.getJSONObject("emotion").getString("type");
                    if(emotion.equals("angry")){
                        talkemotion="你心情不好吗，";
                    }else if(emotion.equals("disgust")){
                        talkemotion="你这是什么表情";
                    }else if(emotion.equals("fear")){
                        talkemotion="你在害怕什么";
                    }else if(emotion.equals("happy")){
                        talkemotion="你笑起来像个小仙女，";
                    }else if(emotion.equals("sad")){
                        talkemotion="开心点，";
                    }else if(emotion.equals("surprise")){
                        talkemotion="咋了，古灵精怪的，";
                    }else{
                        talkemotion="你如果笑起来应该很好看，";
                    }
                    talkface_type=talksex+talkage+talkemotion+talkexpression+talkglasses;//+talkrace;
                }else{
                    talkface_type="不要用卡通漫画脸忽悠我，我拒绝。";
                }
            }
            result.put("talkface_type",talkface_type);
        } else {

        }
        return result;
    }
    public String add(String image1, String userId, String username) throws Exception {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", image1);
            map.put("group_id", "group_repeat");
            map.put("user_id", userId);
            map.put("user_info", username);
            map.put("liveness_control", "NORMAL");
            //map.put("image_type", "FACE_TOKEN");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);
            // 客户端可自行缓存，过期后重新获取。
            Accesstoken accessToken=accesstokenMapper.selectByPrimaryKey(1);
            String result = HttpUtil.post(url, accessToken.getAccessToken(), "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String faceSearch(String image1) throws Exception {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", image1);
            map.put("liveness_control", "NORMAL");
            map.put("group_id_list", "group_repeat");
            // map.put("image_type", "FACE_TOKEN");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);
            Accesstoken accessToken=accesstokenMapper.selectByPrimaryKey(1);
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            //String accessToken = accessToken.getAccessToken();

            String result = HttpUtil.post(url, accessToken.getAccessToken(), "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
