package life.majiang.community.provider;

import com.alibaba.fastjson.JSON;
import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

/*import lombok.extern.slf4j.Slf4j;
import okhttp3.*;*/

/**
 * Created by codedrinker on 2019/4/24.
 */
@Component    //把当前类初始化在spring里，不需要实列化自己。ioc
public class GithubProvider {
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        //用OkHttp
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
         //引入fastjson包；把对象变成json字符串，JSON.toJSONString
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String token = string.split("&")[0].split("=")[1];
            return token;
        } catch (Exception e) {
           /* log.error("getAccessToken error,{}", accessTokenDTO, e);*/
        }
        return null;
    }


    public GithubUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            //JSON.parseObject，把sting转换未对象
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (Exception e) {
           /* log.error("getUser error,{}", accessToken, e);*/
        }
        return null;
    }

}
