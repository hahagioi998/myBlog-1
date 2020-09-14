package life.majiang.community.controller;

import com.baidu.aip.face.AipFace;
import life.majiang.community.mapper.AccesstokenMapper;
import life.majiang.community.model.Accesstoken;
import life.majiang.community.util.face.Base64Util;
import life.majiang.community.util.face.GsonUtils;
import life.majiang.community.util.face.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by codedrinker on 2019/6/26.
 */
@Controller
public class FaceProjectController{
    @Autowired
    private AccesstokenMapper accesstokenMapper;
    public static final String APP_ID = "";
    public static final String API_KEY = "";
    public static final String SECRET_KEY = "";

    @GetMapping("/getAccesstoken")
    public void getAccesstoken() {
        String clientId = API_KEY;// 官网获取的 API Key 更新为你注册的
        String clientSecret = SECRET_KEY;// 官网获取的 Secret Key 更新为你注册的
        String accessToken=getAuth(clientId, clientSecret);
        Accesstoken accesstoken=new Accesstoken();
        accesstoken.setAccessToken(accessToken);
        accesstoken.setId(1);
        accesstokenMapper.updateByPrimaryKey(accesstoken);
    }
    public static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                + "grant_type=client_credentials"		// 1. grant_type为固定参数
                + "&client_id=" + ak					// 2. 官网获取的 API Key
                + "&client_secret=" + sk;				// 3. 官网获取的 Secret Key
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            System.err.println("result:" + result);
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }
    @RequestMapping("/faceProject")
    //为刷新资源访问不了，需要重启服务器
    public String faceProject(HttpServletRequest request, Model model) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file1 = multipartRequest.getFile("uploadImg1");
        MultipartFile file2 = multipartRequest.getFile("uploadImg2");
        System.out.println(file1.getOriginalFilename());
        Accesstoken accessToken=accesstokenMapper.selectByPrimaryKey(1);
        String res=match(file1.getBytes(),file2.getBytes(),accessToken.getAccessToken());
        JSONObject res1=match1(file1.getBytes());
        JSONObject res2=match1(file2.getBytes());
        //找到图片在电脑上的地址，返回
        String talk=null;
        Map<String, String> result = new HashMap<String, String>();
     // Message message=new Message();
        Map<String, String> result1= dataprocessing(res1);
        Map<String, String> result2= dataprocessing(res2);

        result.put("talk1",result1.get("talkface_type"));
        result.put("talk2",result2.get("talkface_type"));

        if(Double.parseDouble(result1.get("championappearance"))>Double.parseDouble(result2.get("championappearance"))){
            result.put("champion","d:dd1.jpg");
        }else if(Double.parseDouble(result1.get("championappearance"))==Double.parseDouble(result2.get("championappearance"))){
            result.put("champion","d:dd1.jpg+d:dd1.jpg");
        }else{
            result.put("champion","d:dd2.jpg");
        }

        result.put("championappearance",result1.get("championappearance"));
        result.put("runnerupappearance",result2.get("championappearance"));
        result.put("similarity",res.substring(113,124));
        result.put("path1","d:dd1.jpg");
        result.put("path2","d:dd2.jpg");
       // model.addAttribute("message",message);
        model.addAttribute("result",result);
        return  "faceProjectresult";
    }
    public static String match(byte[] path1,byte[] path2,String accessToken) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/match";
        try {
            //byte[] bytes1 = FileUtil.readFileByBytes(path1);
           // byte[] bytes2 = FileUtil.readFileByBytes(path2);
            String image1 = Base64Util.encode(path1);
            String image2 = Base64Util.encode(path2);
            List<Map<String, Object>> images = new ArrayList<Map<String, Object>>();
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("image", image1);
            map1.put("image_type", "BASE64");
            map1.put("face_type", "LIVE");
            map1.put("quality_control", "LOW");
            map1.put("liveness_control", "NORMAL");
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("image", image2);
            map2.put("image_type", "BASE64");
            map2.put("face_type", "LIVE");
            map2.put("quality_control", "LOW");
            map2.put("liveness_control", "NORMAL");
            images.add(map1);
            images.add(map2);
            String param = GsonUtils.toJson(images);
            String result = HttpUtil.post(url, accessToken, "application/json", param);
            //System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject match1(byte[] path) {
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        String image = Base64Util.encode(path);

        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age,beauty,expression,face_shape,gender,glasses,race,eye_status,emotion,face_type");
        JSONObject res = client.detect(image,"BASE64", options);

        if (res.getString("error_msg") != null && res.getString("error_msg").equals("SUCCESS")) {
            JSONArray faceList = res.getJSONObject("result").getJSONArray("face_list");
            JSONObject jsonObject = faceList.getJSONObject(0);
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
    public static Map<String, String> dataprocessing(JSONObject res) {
        Map<String, String> result = new HashMap<String, String>();
        if (res.getString("error_msg") != null && res.getString("error_msg").equals("SUCCESS")) {
            JSONArray faceList = res.getJSONObject("result").getJSONArray("face_list");
            JSONObject jsonObject = faceList.getJSONObject(0);

            // 美丑打分，范围0-100，越大表示越美。
            result.put("championappearance", String.valueOf(jsonObject.getDouble("beauty")));
            String sex=jsonObject.getJSONObject("gender").getString("type");
            String talkface_type=null;
            if(sex.equals("male")){//男
                String face_type=jsonObject.getJSONObject("face_type").getString("type");
                if(face_type.equals("human")){
                    String talksex="我没猜错的话，你是男的吧，";
                    /*String talkrace=null;
                    String race=jsonObject.getJSONObject("race").getString("type");
                    if(race.equals("yellow")){
                        talkrace="你好，炎黄子孙，";
                    }else if(race.equals("white")){
                        talkrace="你看起来像西方人哎，";
                    }else if(race.equals("black")){
                        talkrace="你好黑呀，像个灰洲人，";
                    }else{
                        talkrace="你像个阿拉伯人，";
                    }*/
                    String talkage=null;
                    int age=jsonObject.getInt("age");
                    if(age>=0&&age<=16){
                        talkage="你看起来才"+age+"岁，年少稚气的你，";
                    }else if(age>17&&age<25){
                        talkage="你看起来才"+age+"岁，年轻气盛的你，";
                    }else if(age>=25&&age<=35){
                        talkage="你看起来才"+age+"岁，年轻气盛的你，";
                    }else if(age>35&&age<=50){
                        talkage="你看起来像"+age+"岁，年轻气盛的你，";
                    }else if(age>50&&age<=70){
                        talkage="你看起来已经"+age+"岁，年轻气盛的你，";
                    }else if(age>70&&age<=90){
                        talkage="你看起来已经"+age+"岁，年轻气盛的你，";
                    }else{
                        talkage="你看起来已经"+age+"岁，年轻气盛的你，";
                    }
                    String talkexpression=null;
                    String expression=jsonObject.getJSONObject("expression").getString("type");
                    if(expression.equals("none")){
                        talkexpression="你拍照怎么没有表情啊，听说有表情会加分的哦，";
                    }else if(expression.equals("smile")){
                        talkexpression="微微一笑，有那味儿了，";
                    }else if(expression.equals("laugh")){
                        talkexpression="你看你笑得，吃别人家大米了？";
                    }else{
                        talkexpression="好吧，我看不出来你的表情，";
                    }
                    String talkglasses=null;
                    String glasses=jsonObject.getJSONObject("glasses").getString("type");
                    if(glasses.equals("none")){
                        talkglasses="你看过你戴眼镜的样子吗？";
                    }else if(glasses.equals("common")){
                        talkglasses="我jiao得，你不带眼睛更好看，";
                    }else if(glasses.equals("sun")){
                        talkglasses="哟，墨镜，社会，";
                    }
                    String talkemotion=null;
                    String emotion=jsonObject.getJSONObject("emotion").getString("type");
                    if(emotion.equals("angry")){
                        talkemotion="为什么感觉你心情不好呀，";
                    }else if(emotion.equals("disgust")){
                        talkemotion="什么表情哦，嫌弃谁呢？";
                    }else if(emotion.equals("fear")){
                        talkemotion="拍个照而已，怕啥？";
                    }else if(emotion.equals("happy")){
                        talkemotion="你笑起来像个小帅哥，";
                    }else if(emotion.equals("sad")){
                        talkemotion="别这样呀，开心点，";
                    }else if(emotion.equals("surprise")){
                        talkemotion="咋了，稀奇古怪的，";
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
                    String talksex="我没猜错的话，你是女的吧，";
                  /*  String talkrace=null;
                    String race=jsonObject.getJSONObject("race").getString("type");
                    if(race.equals("yellow")){
                        talkrace="你好，炎黄姑娘，";
                    }else if(race.equals("white")){
                        talkrace="你看起来像西方人哎，";
                    }else if(race.equals("black")){
                        talkrace="你好黑呀，像个灰洲人，";
                    }else{
                        talkrace="你像个阿拉伯人，";
                    }*/
                    String talkage=null;
                    int age=jsonObject.getInt("age");
                    if(age>=0&&age<=16){
                        talkage="你看起来才"+age+"岁，窦娥年华的你，";
                    }else if(age>17&&age<25){
                        talkage="你看起来才"+age+"岁，桃李年华的你，";
                    }else if(age>=25&&age<=35){
                        talkage="你看起来才"+age+"岁，花信年华的你，";
                    }else if(age>35&&age<=50){
                        talkage="你看起来像"+age+"岁，风韵犹存的你，";
                    }else if(age>50&&age<=70){
                        talkage="你看起来已经"+age+"岁，有点皱纹的你，";
                    }else if(age>70&&age<=90){
                        talkage="你看起来已经"+age+"岁，安享晚年的你，";
                    }else{
                        talkage="你看起来已经"+age+"岁，年过古稀的你，";
                    }
                    String talkexpression=null;
                    String expression=jsonObject.getJSONObject("expression").getString("type");
                    if(expression.equals("none")){
                        talkexpression="你拍照怎么没有表情啊，听说有表情会加分的哦，";
                    }else if(expression.equals("smile")){
                        talkexpression="微微一笑，一笑倾城，";
                    }else if(expression.equals("laugh")){
                        talkexpression="你看你笑得，吃别人家大米了？";
                    }else{
                        talkexpression="好吧，我看不出来你的表情，";
                    }
                    String talkglasses=null;
                    String glasses=jsonObject.getJSONObject("glasses").getString("type");
                    if(expression.equals("none")){
                        talkglasses="你看过你戴眼镜的样子吗？";
                    }else if(expression.equals("common")){
                        talkglasses="我jiao得，你不带眼睛更好看，";
                    }else if(expression.equals("sun")){
                        talkglasses="哟，墨镜，像个社会一姐，";
                    }
                    String talkemotion=null;
                    String emotion=jsonObject.getJSONObject("emotion").getString("type");
                    if(emotion.equals("angry")){
                        talkemotion="为什么感觉你心情不好呀，";
                    }else if(emotion.equals("disgust")){
                        talkemotion="什么表情哦，嫌弃谁呢？";
                    }else if(emotion.equals("fear")){
                        talkemotion="拍个照而已，怕啥？";
                    }else if(emotion.equals("happy")){
                        talkemotion="你笑起来像个小仙女，";
                    }else if(emotion.equals("sad")){
                        talkemotion="别这样呀，开心点，";
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
}
