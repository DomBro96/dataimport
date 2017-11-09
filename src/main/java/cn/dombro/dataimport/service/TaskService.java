package cn.dombro.dataimport.service;

import cn.dombro.dataimport.util.FilePathEnum;
import it.sauronsoftware.cron4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class TaskService {

    private Scheduler deleteScheduler = null;
    private int month;
    private int dayOfMonth;
    private int minute;
    private int hour;



    public void startSchedule(String downloadPath){

        deleteScheduler = new Scheduler();
        DeleteTask deleteTask = new DeleteTask();
        DeleteSchedulerListener schedulerListener = new DeleteSchedulerListener();
        deleteTask.setDownloadPath(downloadPath);
        setDeleteTime();
 //       String scheduleId = deleteScheduler.schedule(minute+" "+hour+" "+dayOfMonth+" "+month+" *",deleteTask);
        String scheduleId = deleteScheduler.schedule( "50 18 2 11 *",deleteTask);
        schedulerListener.setSchedulerId(scheduleId);
        //添加监听器
        deleteScheduler.addSchedulerListener(schedulerListener);
        //启动调度器
        deleteScheduler.start();
    }


    private void setDeleteTime(){
        LocalDateTime now = LocalDateTime.now().withNano(0);
        //十二个小时后进行文件删除
        LocalDateTime tomorrow = now.plusHours(12);
        month = tomorrow.getMonthValue();
        dayOfMonth = tomorrow.getDayOfMonth();
        minute = tomorrow.getMinute();
        hour = tomorrow.getHour();
    }

    public Scheduler getScheduler() {
        return deleteScheduler;
    }
}

class DeleteTask extends Task{
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteTask.class);

    String downloadPath = null;

    @Override
    public void execute(TaskExecutionContext taskExecutionContext) throws RuntimeException {
         boolean isDelete = FilePathEnum.DELETE_FILE.deleteFile(downloadPath);
         System.out.println(isDelete);
         if (isDelete == true){
            taskExecutionContext.setStatusMessage("delete success");
             LOGGER.info("下载文件"+downloadPath+"已被删除");
         }else {
             LOGGER.info("下载文件"+downloadPath+"删除失败");
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

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
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
