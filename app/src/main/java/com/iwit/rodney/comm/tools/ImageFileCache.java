package com.iwit.rodney.comm.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class ImageFileCache {
    private  String CACHDIR = "AppCenterImgCach";
    private static final String WHOLESALE_CONV = ".cach";

    private static final int MB = 1024 * 1024;
    private static final int CACHE_SIZE = 30;
    private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 5;

    public ImageFileCache(String cachDir) {
        this.CACHDIR=cachDir;
        // 清理文件缓存
      //  removeCache(getDirectory());
    }
    
    public ImageFileCache() {
        // 清理文件缓存
     //   removeCache(getDirectory());
    }

    /** 从缓存中获取图片 **/
    public Bitmap getImage(final String url) {
        final String path = getDirectory() + "/" + convertUrlToFileName(url);
        File file = new File(path);
        if (file.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(path);
            if (bmp == null) {
                file.delete();
            } else {
                updateFileTime(path);
                return bmp;
            }
        }
        return null;
    }

    /** 将图片存入文件缓存 **/  
    public boolean saveBitmap(Bitmap bm, String url) {
        if (bm == null) {
            return false;
        }
        // 如果文件的空间小于了5M 就将文件删除一些不长用的
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
            // SD空间不足
        	removeCache(getDirectory());
            return  false;
        }
        String filename = convertUrlToFileName(url);
        String dir = getDirectory();
        File dirFile = new File(dir);
        if (!dirFile.exists())
            dirFile.mkdirs();
        File file = new File(dir + "/" + filename);
        try {
            file.createNewFile();
            OutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            return  true;
        } catch (FileNotFoundException e) {
        	Log.w("ImageFileCache", "FileNotFoundException");
            return  false;
        } catch (IOException e) {
        	Log.w("ImageFileCache", "IOException"+e);
            return  false;
        }
    }
    
    public void removeBitmapFromFile(String url){
        
        String filename = convertUrlToFileName(url);
        String dir = getDirectory();
        
        try {
            File file = new File(dir + "/" + filename);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 
     * 计算存储目录下的文件大小， 
     * 当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定 
     * 那么删除40%最近没有被使用的文件 
     */  
    private boolean removeCache(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return true;
        }
        if (!android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return false;
        }

        int dirSize = 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains(WHOLESALE_CONV)) {
                dirSize += files[i].length();
            }
        }

        if (dirSize > CACHE_SIZE * MB
                || FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
            int removeFactor = (int) ((0.4 * files.length) + 1);
            Arrays.sort(files, new FileLastModifSort());
            //按照递增排列
            for (int i = 0; i < removeFactor; i++) {
                if (files[i].getName().contains(WHOLESALE_CONV)) {
                    files[i].delete();
                }
            }
        }

        if (freeSpaceOnSd() <= CACHE_SIZE) {
            return false;
        }

        return true;
    }

    /** 修改文件的最后修改时间 **/ 
    public void updateFileTime(String path) {
        File file = new File(path);
        long newModifiedTime = System.currentTimeMillis();
        file.setLastModified(newModifiedTime);
    }

    /** 计算sdcard上的剩余空间 **/
    private int freeSpaceOnSd() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
                .getPath());
        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
                .getBlockSize()) / MB;
        return (int) sdFreeMB;
    }

    /** 将url转成文件名 **/  
    private String convertUrlToFileName(String url) {
    	//以下这段代码有问题，生成的文件名如下：
    	//http192.168.16.71fileioseebook14061170999783.png.cach
//        String[] strs = url.split("/");
//        String ss="";
//        for(int i=0;i<strs.length;i++){
//            ss=ss+strs[i];
//        }
//        ss=ss.replace(":", "");
    	//取url中尾段作为文件名，修改者：Tao
        String[] strs = url.split("/");  
        return strs[strs.length - 1] + WHOLESALE_CONV;  
    }

    /** 获得缓存目录 **/
    private String getDirectory() {
        String dir = getSDPath() + "/" + CACHDIR;
        return dir;
    }

    /** 取SD卡路径 **/  
    private String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在  
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory(); //获取根目录  
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }
    }

    /** 
     * 根据文件的最后修改时间进行排序 
     */  
    private class FileLastModifSort implements Comparator<File> {
        public int compare(File arg0, File arg1) {
            if (arg0.lastModified() > arg1.lastModified()) {
                return 1;
            } else if (arg0.lastModified() == arg1.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }
    }

}