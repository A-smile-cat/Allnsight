package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.Model;
import com.example.demo.entity.TrainingTasks;
import com.example.demo.entity.UserInfo;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import com.example.demo.service.ModelService;
import com.example.demo.service.TrainingTasksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/")
public class TrainingTasksController {
    @Autowired
    TrainingTasksService trainingTasksService;

    @Autowired
    ModelService modelService;

    @GetMapping("/searchTask")
    public Result searchTask(@RequestParam(required = false,name = "taskName",defaultValue = "") String name, @RequestParam(name="type",required = false,defaultValue = "") String type, @RequestParam(name="status",required = false,defaultValue = "") String status, @RequestParam("user_id") int uid, HttpSession session){
        if (session == null || session.getAttribute("user") == null) {
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user =(UserInfo) session.getAttribute("user");
        if(uid != user.getId()){
            return ResultFactory.buildFailResult("无权限");
        }
        uid = user.getId();
        List<TrainingTasks> taskList = new ArrayList<>();

        taskList = trainingTasksService.query(uid,name,type,status);

        if(taskList.isEmpty()){
            return ResultFactory.buildSuccessResult("无查询结果");
        }else{
            return ResultFactory.buildSuccessResult("查询成功", JSON.toJSONString(taskList));
        }
    }

    @PostMapping("/task")
    public Result add(@RequestBody TrainingTasks task,HttpSession session){
        if (session == null || session.getAttribute("user") == null) {
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user =(UserInfo) session.getAttribute("user");
        if(user.getId() != task.getUser_id()){
            return ResultFactory.buildFailResult("无权限");
        }
        task.setUser_id(user.getId());
        int f = trainingTasksService.add(task,task.getModelName());
        if(f==1){
            return ResultFactory.buildSuccessResult("添加成功");
        }else{
            return ResultFactory.buildFailResult("添加失败");
        }

    }

    @PutMapping("/task/{id}")
    public Result update(@PathVariable(value = "id")Integer id,@RequestBody TrainingTasks task,HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getId() != task.getUser_id()){
            return ResultFactory.buildFailResult("无权限");
        }
        int uid = user.getId();
        int tid = id;
        TrainingTasks tasks = trainingTasksService.queryById(tid,uid);
        if(tasks==null){
            return ResultFactory.buildFailResult("任务不存在");
        }
        String status = tasks.getStatus();
        if(status.equals("training")|| status.equals("completed")){
            return ResultFactory.buildFailResult("当前状态不允许更新");
        }
        task.setUser_id(uid);
        task.setTask_id(tid);
        int f = trainingTasksService.update(task);
        if(f==1){
            return ResultFactory.buildSuccessResult("更新成功");
        }else{
            return ResultFactory.buildFailResult("更新失败");
        }
    }

    @DeleteMapping("/deleteTask/{task_id}")
    public Result deleteTask(@PathVariable("task_id") int taskId,@RequestParam("user_id") int userId,HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getId() != userId){
            return ResultFactory.buildFailResult("无权限");
        }
        userId = user.getId();
        TrainingTasks task = trainingTasksService.queryById(taskId,userId);
        if(task==null){
            return ResultFactory.buildFailResult("任务不存在");
        }else if(task.getStatus().equals("training")){
            return ResultFactory.buildFailResult("当前状态不允许删除");
        }
        else{
            int model_id = task.getModel_id();
            modelService.deleteById(model_id,userId);
            trainingTasksService.deleteById(taskId,userId);
            return ResultFactory.buildSuccessResult("删除成功");
        }
    }
}
