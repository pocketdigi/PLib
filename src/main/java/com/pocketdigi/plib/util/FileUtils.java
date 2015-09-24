package com.pocketdigi.plib.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 文件操作类
 *
 * @author fhp
 */
public class FileUtils {

    /**
     * 写入字符串到文件
     *
     * @param str      　待写字符串
     * @param filePath 　保存位置
     */
    public static boolean writeStr2File(String str, String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] bs = str.getBytes();

            bos.write(bs);
            bos.flush();
            bos.close();
            fos.close();

            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 从文件读取字符串
     *
     * @param filePath
     * @return
     */
    public static String readStrFromFile(String filePath) {
        StringBuffer sb = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fis);

            BufferedReader reader = new BufferedReader(new InputStreamReader(bis));
            while (reader.ready()) {
                sb.append((char) reader.read());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 判断目录是否存在，不存在则创建目录
     *
     * @param path
     */
    public static void mkdirs(String path) {
        if (path != null) {
            File tempPath = new File(path);
            if (!tempPath.exists()) {
                tempPath.mkdirs();
            }
        }
    }

    /**
     * 用时间戳生成文件路径
     *
     * @param dirPath 所在目录
     * @param suffix  后缀，带.
     * @return
     */
    public static String generateFileName(String dirPath, String suffix) {
        StringBuffer sb = new StringBuffer();
        sb.append(dirPath);
        mkdirs(dirPath);

        if (!dirPath.endsWith("/")) {
            sb.append("/");
        }
        sb.append(System.currentTimeMillis());
        sb.append(suffix);

        if (isFileExist(sb.toString())) {
            return generateFileName(dirPath, suffix);
        }
        return sb.toString();
    }

    /**
     * 判断文件或目录是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            return file.exists();
        }
        return false;
    }

    /**
     * 删除文件或目录，会清空目录下所有文件
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (File file1 : files) {
                        deleteFile(file1.getAbsolutePath());
                    }
                } else {
                    file.delete();
                }
            }
        }
    }

    /**
     * 获取目录占用空间
     *
     * @param dirPath
     * @return
     */
    public static double getDirSize(String dirPath) {
        File file = new File(dirPath);
        // 判断文件是否存在
        if (file.exists()) {
            // 如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f.getAbsolutePath());
                return size;
            } else {// 如果是文件则直接返回其大小,以“兆”为单位
                double size = (double) file.length() / 1024 / 1024;
                return size;
            }
        } else {
            return 0.0;
        }
    }

    /**
     * 重命名文件(移动文件)
     *
     * @param oldPath
     * @param newPath
     */
    public static boolean rename(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        return oldFile.renameTo(new File(newPath));
    }

    /**
     * 复制文件
     * @param oldPath
     * @param newPath
     */
    public static void copy(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("error  ");
            e.printStackTrace();
        }
    }
}
