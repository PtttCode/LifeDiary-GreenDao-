package andy.ham;

/**
 * Created by PATIR on 2018/6/20.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

public class SaveFileService {

    //保存数据
    public static boolean saveFile(Context context,String totalNum){
        //创建文件对象
        //File file = new File("/data/data/cn.yzx.login", "info.txt");

        //创建文件对象 通过file目录
        //File file = new File(context.getFilesDir(),"info.txt");

        //创建文件对象 通过cache目录
        File file = new File(context.getCacheDir(), "TotalNum.txt");

        try {
            //文件输出流
            FileOutputStream fos = new FileOutputStream(file);
            //写数据
            fos.write((totalNum).getBytes());

            //关闭文件流
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //数据回显
    public static int getUserInfo(Context context){
        //获取文件对象
        //File file = new File("/data/data/cn.yzx.login", "info.txt");

        //获取文件对象
        //File file = new File(context.getFilesDir(), "info.txt");

        //获取文件对象
        File file = new File(context.getCacheDir(), "TotalNum.txt");

        try {
            //输入流
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            //读取文件中的内容
            String result = br.readLine();
            Log.e("tag","result"+result);

            //将数据存到map集合中
            int num=Integer.parseInt(result);

            return num;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }

    }
}