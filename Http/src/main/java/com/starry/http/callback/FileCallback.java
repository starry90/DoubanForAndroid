package com.starry.http.callback;

import com.starry.http.utils.MainHandler;
import com.starry.http.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 文件下载回调
 *
 * @author Starry Jerry
 * @since 2018/7/1.
 */

public abstract class FileCallback extends CommonCallback<File> {

    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir;
    /**
     * 目标文件存储的文件名
     */
    private String destFileName;


    public FileCallback(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    @Override
    public File parseResponse(Response response) throws Exception {
        return saveFile(response);
    }

    /**
     * save file
     */
    private File saveFile(Response response) throws Exception {
        //获取目录
        File dir = new File(destFileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //删除旧文件
        File file = new File(dir, destFileName);
        if (file.exists()) {
            file.delete();
        }

        int len;
        long sum = 0;
        byte[] buf = new byte[1024 * 8];
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            ResponseBody body = response.body();
            Util.checkNotNull(body);
            is = body.byteStream();
            final long total = body.contentLength();

            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                MainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        inProgress(finalSum * 1.0f / total, total);
                    }
                });
            }
            fos.flush();
            return file;
        } catch (Exception ex) {
            //下载出错，删除文件
            if (file.exists()) {
                file.delete();
            }
            throw ex;
        } finally {
            Util.closeQuietly(is);
            Util.closeQuietly(response);
            Util.closeQuietly(fos);
        }
    }

}
