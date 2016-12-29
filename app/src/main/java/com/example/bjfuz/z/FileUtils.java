package com.example.bjfuz.z;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by bjfuz on 2016/1/2.
 */
public class FileUtils {
    private static String TAG = "File";

    private static String getSDCardRootPath()
    {
        String sDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        return sDCardRoot;
    }

    public static void saveToSDCard(Bitmap bitmap,String filePath,String fileName)
    {
        String sdcardroot = getSDCardRootPath();
        File dir = new File(sdcardroot+File.separator+filePath);
        Log.i(TAG, "dir:" + dir);
        if (!dir.exists())
            dir.mkdirs();

        File targetFile = new File(dir,fileName);

        try
        {
            targetFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
