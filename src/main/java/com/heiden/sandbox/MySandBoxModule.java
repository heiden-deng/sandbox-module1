package com.heiden.sandbox;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import org.kohsuke.MetaInfServices;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * @Author: heiden
 * @Date: 2021/6/4 14:08
 * @Project: spring-boot-study
 */

@MetaInfServices(Module.class)
@Information(id="pts-sandbox-module", author = "dengjianquan@ebchinatech.com",version = "0.0.1")
public class MySandBoxModule implements Module {
    //日志输出，默认采用logback，这里的日志输出到切入的服务日志中
    private Logger LOG = Logger.getLogger(MySandBoxModule.class.getName());

    @Resource
    private ModuleEventWatcher moduleEventWatcher;

    @Command("addLog")
    public void addLog(){
        new EventWatchBuilder(moduleEventWatcher)
                .onClass("com.heiden.example.callchainbasedemo.service.impl.PersonActImpl")
                .onBehavior("callPerson")
                .onWatch(new AdviceListener(){
                    @Override
                    protected void before(Advice advice) throws Throwable {
                        Object[] parameterArray = advice.getParameterArray();
                        if (parameterArray != null){
                            for (Object paramObj : parameterArray){
                                if (paramObj != null){
                                    if (paramObj.getClass().getName().contains("Person")){
                                        Field nameField = paramObj.getClass().getDeclaredField("name");
                                        nameField.setAccessible(true);
                                        LOG.info("Person param name field value is :" + String.valueOf(nameField.get(paramObj)));
                                    }
                                }
                            }
                        }
                        super.before(advice);
                    }
                });
    }

}
