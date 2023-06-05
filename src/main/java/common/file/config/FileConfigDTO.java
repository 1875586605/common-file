package common.file.config;

import common.file.core.client.FileClientConfig;
import common.file.core.enums.FileStorageEnum;
import lombok.Data;

/**
 * @author cjb
 * @date 2023/5/9 9:48
 * @describe
 */
@Data
public class FileConfigDTO {

    /**
     * 配置名
     */
    private String name;


    /**
     * 存储器
     * <p>
     * 枚举 {@link FileStorageEnum}
     */
    private String storage;

    /**
     * 是否为主配置
     * <p>
     * 由于我们可以配置多个文件配置，默认情况下，使用主配置进行文件的上传
     */
    private Boolean master;

    /**
     * 配置文件详情
     */
    private FileClientConfig config;


}
