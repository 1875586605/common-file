package common.file.config;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import common.file.core.client.FileClientConfig;
import common.file.core.enums.FileStorageEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author cjb
 * @date 2023/5/9 9:38
 * @describe 文件配置类
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "file.conf")
public class FileConfigConfiguration {

    private Map<String, FileConfigDTO> confMap;


    @PostConstruct
    public void init() {
        log.info("初始化数据文件配置,进行多态配置");
        for (String key : confMap.keySet()) {
            FileConfigDTO fileConfigDTO = confMap.get(key);
            FileStorageEnum storageEnum = FileStorageEnum.getByStorage(fileConfigDTO.getStorage());
            String propertyKeyPrefix = "file.conf.confMap." + fileConfigDTO.getName() + ".config.";
            HashMap<String, Object> beanMap = new HashMap<>();
            BeanDesc beanDesc = BeanUtil.getBeanDesc(storageEnum.getConfigClass());
            Collection<PropDesc> props = beanDesc.getProps();

            for (PropDesc prop : props) {
                String fieldName = prop.getFieldName();
                String propertyKey = propertyKeyPrefix + fieldName;
                String fieldValue = SpringUtil.getProperty(propertyKey);
                if (ObjectUtil.isNotEmpty(fieldValue)) {
                    beanMap.put(fieldName, fieldValue);
                }
            }
            FileClientConfig bean = BeanUtil.toBean(beanMap, storageEnum.getConfigClass());
            fileConfigDTO.setConfig(bean);
        }
        log.info("初始化数据文件配置,多态配置完成");

    }

    public FileConfigDTO getConfByKey(String key) {
        return this.confMap.get(key);
    }

    public List<FileConfigDTO> getFileConfList() {
        ArrayList<FileConfigDTO> fileConfigDTOS = new ArrayList<>();
        for (String key : this.confMap.keySet()) {
            fileConfigDTOS.add(this.confMap.get(key));
        }
        return fileConfigDTOS;

    }

}
