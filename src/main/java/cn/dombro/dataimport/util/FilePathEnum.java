package cn.dombro.dataimport.util;

import com.jfinal.kit.PathKit;

import java.io.File;

public enum FilePathEnum {


    UPLOAD_PATH(PathKit.getWebRootPath()+File.separator+"upload"+File.separator),
    DOWNLOAD_PATH(PathKit.getWebRootPath()+File.separator+"download"+File.separator),
    TEMP_PATH(PathKit.getWebRootPath()+File.separator+"temp"+File.separator),
    DELETE_FILE(),
    RENAME_FILE();

    private String filePath;

    private FilePathEnum() {
    }

    private FilePathEnum(String filePath){

        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }


    public boolean deleteFile(String deleteFilePath){
        File deleteFile = new File(deleteFilePath);
        if (deleteFile.exists()){
            if(deleteFile.delete()){
                return true;
            }else {
                System.gc();
                return deleteFile.delete();
            }
        }
        return false;
    }

    public String renameFile(String uploadFilePath){
        File file = new File(uploadFilePath);
        String parentPath = file.getParent();
        String fileName = file.getName();
        String prefix = fileName.substring(0,fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String target = GeneratorUtil.createRandomString();
        prefix = prefix+target;
        fileName = prefix+suffix;
        File renameFile = new File(parentPath+File.separator+fileName);
        file.renameTo(renameFile);
        return renameFile.getAbsolutePath();
    }


 }
