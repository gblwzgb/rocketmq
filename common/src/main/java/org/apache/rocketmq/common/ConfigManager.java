/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.common;

import java.io.IOException;
import org.apache.rocketmq.common.constant.LoggerName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConfigManager {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.COMMON_LOGGER_NAME);

    public abstract String encode();  // 模板方法，由子类实现

    public boolean load() {
        String fileName = null;
        try {
            fileName = this.configFilePath();  // 调用子类的实现，获取配置文件的路径
            String jsonString = MixAll.file2String(fileName);  // 读取配置文件，文件里存的是Json字符串

            if (null == jsonString || jsonString.length() == 0) {
                return this.loadBak(); // 读.bak文件
            } else {
                this.decode(jsonString);  // 调用子类的实现，将字符串的内容，解析成Java对象里的属性
                log.info("load {} OK", fileName);
                return true;
            }
        } catch (Exception e) {
            log.error("load [{}] failed, and try to load backup file", fileName, e);
            return this.loadBak();
        }
    }

    /**
     * 模板方法，由子类实现
     * 根据根路径（可配），拼接上后缀，获取各种配置文件的路径
     *
     * @return 配置文件的路径
     */
    public abstract String configFilePath();

    private boolean loadBak() {
        String fileName = null;
        try {
            fileName = this.configFilePath();
            String jsonString = MixAll.file2String(fileName + ".bak");
            if (jsonString != null && jsonString.length() > 0) {
                this.decode(jsonString);
                log.info("load [{}] OK", fileName);
                return true;
            }
        } catch (Exception e) {
            log.error("load [{}] Failed", fileName, e);
            return false;
        }

        return true;
    }

    public abstract void decode(final String jsonString);  // 模板方法，由子类实现

    public synchronized void persist() {
        String jsonString = this.encode(true);
        if (jsonString != null) {
            String fileName = this.configFilePath();
            try {
                MixAll.string2File(jsonString, fileName);
            } catch (IOException e) {
                log.error("persist file [{}] exception", fileName, e);
            }
        }
    }

    public abstract String encode(final boolean prettyFormat);  // 模板方法，由子类实现
}
