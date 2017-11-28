package cn.dombro.dataimport.service;

import cn.dombro.dataimport.util.FilePathEnum;
import it.sauronsoftware.cron4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

public class TaskService {

    private Scheduler deleteScheduler = null;


    public void startSchedule(){

        deleteScheduler = new Scheduler();
        DeleteTask deleteTask = new DeleteTask();
        DeleteSchedulerListener schedulerListener = new DeleteSchedulerListener();
        //每天第24点删除文件
        String scheduleId = deleteScheduler.schedule(0+" "+0+" * * *",deleteTask);
        schedulerListener.setSchedulerId(scheduleId);
        //添加监听器
        deleteScheduler.addSchedulerListener(schedulerListener);
        //启动调度器
        deleteScheduler.start();
    }



    public Scheduler getScheduler() {
        return deleteScheduler;
    }
}

class DeleteTask extends Task{
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteTask.class);




    @Override
    public void execute(TaskExecutionContext taskExecutionContext) throws RuntimeException {
         //boolean isDelete = FilePathEnum.DELETE_FILE.deleteFile(downloadPath);
         File uploadDir = new File(FilePathEnum.UPLOAD_PATH.getFilePath());
         File tempDir = new File(FilePathEnum.TEMP_PATH.getFilePath());
         File downloadDir = new File(FilePathEnum.DOWNLOAD_PATH.getFilePath());
         deleteDir(uploadDir);
         deleteDir(tempDir);
         deleteDir(downloadDir);
         if (uploadDir.listFiles() == null && tempDir.listFiles() == null && downloadDir.listFiles() == null){
             LOGGER.info("上传文件夹已被清空");
             LOGGER.info("暂存文件夹已被清空");
             LOGGER.info("下载文件夹已被清空");
             taskExecutionContext.setStatusMessage("delete  success");
         }

    }

    @Override
    public boolean canBeStopped() {
        return true;
    }

    @Override
    public boolean supportsStatusTracking() {
        return true;
    }

    private void deleteDir(File dir){

        if (! dir.exists() || !dir.isDirectory()){
            return;
        }else {
            File[] childrenFiles = dir.listFiles();
            if (childrenFiles.length == 0) {
                for (int i = 0; i < childrenFiles.length; i++) {
                    if (childrenFiles[i].isFile()) {
                        FilePathEnum.DELETE_FILE.deleteFile(childrenFiles[i].getAbsolutePath());
                    } else {
                        deleteDir(childrenFiles[i]);
                        childrenFiles[i].delete();
                    }
                }
            }else {
                return;
            }

        }



    }


}

class DeleteSchedulerListener implements SchedulerListener{
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteSchedulerListener.class);

    private String schedulerId = null;

    @Override
    public void taskLaunching(TaskExecutor taskExecutor) {

    }

    //如果任务执行成功，从调度中删除任务，停止调度
    @Override
    public void taskSucceeded(TaskExecutor taskExecutor) {
        System.out.println(taskExecutor.getStatusMessage());
        if (taskExecutor.getStatusMessage().equals("delete success") && taskExecutor.getScheduler().isStarted()) {
            //移除调度中的任务
            taskExecutor.getScheduler().deschedule(schedulerId);
            //关闭调度器
            taskExecutor.getScheduler().stop();
            LOGGER.info("调度已关闭");
        }
    }

    @Override
    public void taskFailed(TaskExecutor taskExecutor, Throwable throwable) {
        if (taskExecutor.getScheduler().isStarted())
            taskExecutor.getScheduler().stop();
    }

    public void setSchedulerId(String schedulerId) {
        this.schedulerId = schedulerId;
    }
}
