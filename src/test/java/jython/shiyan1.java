package jython;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class shiyan1 {
   public static void main(String[] args) {
        /*PythonInterpreter pi = new PythonInterpreter();
        //执行python代码段
        pi.execfile("D:\\mouselocation.py");  //自己文件的路径
        //释放资源
        pi.leanup();
        pi.close();*/
        String a="600";
        String b="161";
        String button="left";
        String clicks="2";
        String interval="2";
        String duration="2";
        String content="hello";
        String def="xiqu";
        try{
            //System.out.println("start");
            //Process pr = Runtime.getRuntime().exec("C:\\Users\\15984\\AppData\\Local\\Programs\\Python\\Python36-32\\python.exe D:\\webpyproject.py "+def+" "+a+" "+b+" "+button+" "+duration);
            //            //            //Process pr = Runtime.getRuntime().exec("C:\\Users\\15984\\AppData\\Local\\Programs\\Python\\Python36-32\\python.exe D:\\webpyproject.py "+def+" "+a+" "+b+" "+button+" "+clicks+" "+interval);
             Process pr ;
            pr = Runtime.getRuntime().exec("C:\\Users\\15984\\AppData\\Local\\Programs\\Python\\Python36-32\\python.exe D:\\webpyproject.py "+def);
            //            //            //System.out.println("123");
            //Process pr = Runtime.getRuntime().exec("C:\\Users\\15984\\AppData\\Local\\Programs\\Python\\Python36-32\\python.exe D:\\webpyproject.py "+def+" "+a+" "+b+" "+button+" "+clicks+" "+interval+" "+duration+" "+content);
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            pr.waitFor();
            //System.out.println("end");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
    //Exception in thread "main" Traceback (most recent call last):File "D:\mouselocation.py", line 1, in <module>import pyautogui as pag ImportError: No module named pyautogui