package com.example.bjfuz.z;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;

/**
 * Created by bjfuz on 2016/1/2.
 */

public class UploadFileTask extends AsyncTask<String, Void, String> {
    //requestURL是交给web端处理的servlet路径
    //public static final String requestURL="http://159.226.179.182:8080/AndroidUploadFileWeb/FileImageUploadServlet";
    public static final String requestURL="http://159.226.179.80:8080/zy/Upload";
    /**
     *  可变长的输入参数，与AsyncTask.exucute()对应
     */
    private ProgressDialog pdialog;
    private Activity context=null;
    public UploadFileTask(Activity ctx)
    {
        this.context=ctx;
        pdialog=ProgressDialog.show(context, "正在加载...", "系统正在处理您的请求");
    }
    @Override
    protected void onPostExecute(String result) {
        // 返回HTML页面的内容
        pdialog.dismiss();
        if(UploadUtils.SUCCESS.equalsIgnoreCase(result)){
            Toast.makeText(context, "上传成功!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, "上传失败!",Toast.LENGTH_LONG ).show();
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    //uploadFileTask.execute(picPath);
    @Override
    protected String doInBackground(String... params)
    {
        //params[0]指的是图片的实际路径
        File file=new File(params[0]);
        return UploadUtils.uploadFile( file, requestURL,"");
    }
    @Override
    protected void onProgressUpdate(Void... values)
    {
    }
}

