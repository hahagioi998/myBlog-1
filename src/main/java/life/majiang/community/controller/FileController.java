package life.majiang.community.controller;

import life.majiang.community.dto.FileDTO;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.util.AliyunOSSUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * Created by codedrinker on 2019/6/26.
 */
@Controller
public class FileController {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    private static final String TO_PATH="upLoad";
    private static final String RETURN_PATH="success";
    @Autowired
    private AliyunOSSUtil aliyunOSSUtil;
    @Autowired
    private UserMapper userMapper;
   //阿里云的oss
    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("editormd-image-file");
        file.getContentType();
        String filename = file.getOriginalFilename();
        try {
            File newFile = new File(filename);
            FileOutputStream os = new FileOutputStream(newFile);
            os.write(file.getBytes());
            os.close();
            file.transferTo(newFile);
            // 上传到OSS
            String uploadUrl = aliyunOSSUtil.upLoad(newFile);
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSuccess(1);
            fileDTO.setUrl("https://zhuhaojie.oss-cn-beijing.aliyuncs.com/"+uploadUrl);
            return fileDTO;
        } catch (Exception ex) {
           // ex.printStackTrace();
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSuccess(0);
            fileDTO.setMessage("上传失败");
            return fileDTO;
        }
    }
    @RequestMapping("/file/uploadImg")
    //为刷新资源访问不了，需要重启服务器
    public String uploadImg(HttpServletRequest request) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("uploadImg");
        file.getContentType();
        String filename = file.getOriginalFilename();
        File newFile = new File(filename);
        FileOutputStream os = new FileOutputStream(newFile);
        os.write(file.getBytes());
        os.close();
        file.transferTo(newFile);
        // 上传到OSS
        String uploadUrl = aliyunOSSUtil.upLoad(newFile);
        user.setAvatarUrl("https://zhuhaojie.oss-cn-beijing.aliyuncs.com/"+uploadUrl);
        userMapper.updateByPrimaryKey(user);
        return  "redirect:/userhome/home/";
    }
    public static String getcontentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase(".jpeg") ||
                FilenameExtension.equalsIgnoreCase(".jpg") ||
                FilenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpg";
        }
        if (FilenameExtension.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase(".txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase(".pptx") ||
                FilenameExtension.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase(".docx") ||
                FilenameExtension.equalsIgnoreCase(".doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase(".xml")) {
            return "text/xml";
        }
        return "image/jpg";
    }
}
