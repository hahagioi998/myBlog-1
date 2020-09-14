package life.majiang.community.controller;

import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.model.UserExample;
import life.majiang.community.util.AliyunSmsUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
public class SmsController {
    @Autowired
    private UserMapper userMapper;
    @RequestMapping(value = "/sendMsg", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> sendMsg(@RequestBody Map<String,Object> requestMap) {
        String phoneNumber = requestMap.get("phoneNumber").toString();
        //看有没有该手机号
        UserExample userExample=new UserExample();
        userExample.createCriteria().andPhoneEqualTo(phoneNumber);
        List<User> users=userMapper.selectByExample(userExample);
        if(users.size()==0){
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("hash", 0);
            resultMap.put("tamp", 0);
            resultMap.put("code", 100);
            return resultMap;
        }else {
            String randomNum = RandomStringUtils.randomNumeric(6);
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MINUTE, 5);
            String currentTime = sf.format(c.getTime());// 生成5分钟后时间，用户校验是否过期
            System.out.println(randomNum);
            AliyunSmsUtil.send(phoneNumber,randomNum);
            // String hash =  MD5Utils.getMD5Code(KEY + "@" + currentTime + "@" + randomNum);//生成MD5值
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("hash", randomNum);
            resultMap.put("tamp", currentTime);
            resultMap.put("code", 200);
            return resultMap; //将hash值和tamp时间返回给前端
        }
    }
    @RequestMapping(value = "/validateNum", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> validateNum(@RequestBody Map<String,Object> requestMap) {
        String requestHash = requestMap.get("hash").toString();
        String tamp = requestMap.get("tamp").toString();
        String msgNum = requestMap.get("msgNum").toString();
        // String hash = MD5Utils.getMD5Code(KEY + "@" + tamp + "@" + msgNum);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 5);
        String currentTime = sf.format(c.getTime());
        if (tamp.compareTo(currentTime)< 0) {
            if (requestHash.equalsIgnoreCase(requestHash)){
                //校验成功
               // Map<String, Object> resultMap1 = new HashMap<>();
                requestMap.put("code",101);
                return requestMap;
            }else {
                //验证码不正确，校验失败
                Map<String, Object> resultMap1 = new HashMap<>();
                resultMap1.put("code", 102);
                return requestMap;
            }
        } else {
            // 超时
            Map<String, Object> resultMap1 = new HashMap<>();
            requestMap.put("code", 103);
            return requestMap;
        }
    }
    @RequestMapping(value = "/sendMsg1", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> sendMsg1(@RequestBody Map<String,Object> requestMap) {
        String phoneNumber = requestMap.get("phoneNumber").toString();
        //看有没有该手机号
        UserExample userExample=new UserExample();
        userExample.createCriteria().andPhoneEqualTo(phoneNumber);
        List<User> users=userMapper.selectByExample(userExample);
        if(users.size()==0){
            String randomNum = RandomStringUtils.randomNumeric(6);
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MINUTE, 5);
            String currentTime = sf.format(c.getTime());// 生成5分钟后时间，用户校验是否过期
            System.out.println(randomNum);
            AliyunSmsUtil.send(phoneNumber,randomNum);
            // String hash =  MD5Utils.getMD5Code(KEY + "@" + currentTime + "@" + randomNum);//生成MD5值
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("hash", randomNum);
            resultMap.put("tamp", currentTime);
            resultMap.put("code", 200);
            return resultMap; //将hash值和tamp时间返回给前端
        }else {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("hash", 0);
            resultMap.put("tamp", 0);
            resultMap.put("code", 100);
            return resultMap;
        }
    }
    @RequestMapping(value = "/validateNum1", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> validateNum1(@RequestBody Map<String,Object> requestMap) {
        String requestHash = requestMap.get("hash").toString();
        String tamp = requestMap.get("tamp").toString();
        String msgNum = requestMap.get("msgNum").toString();
        // String hash = MD5Utils.getMD5Code(KEY + "@" + tamp + "@" + msgNum);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 5);
        String currentTime = sf.format(c.getTime());
        if (tamp.compareTo(currentTime)< 0) {
            if (requestHash.equalsIgnoreCase(requestHash)){
                //校验成功
                // Map<String, Object> resultMap1 = new HashMap<>();
                requestMap.put("code",101);
                return requestMap;
            }else {
                //验证码不正确，校验失败
                Map<String, Object> resultMap1 = new HashMap<>();
                resultMap1.put("code", 102);
                return requestMap;
            }
        } else {
            // 超时
            Map<String, Object> resultMap1 = new HashMap<>();
            requestMap.put("code", 103);
            return requestMap;
        }
    }
}
