package common.file.core.client;

public interface FileClientFactory {

    /**
     * 获得文件客户端
     *
     * @param configKey 配置编号
     * @return 文件客户端
     */
    FileClient getFileClient(String configKey);

    /**
     * 创建文件客户端
     *
     * @param confKey 配置key
     * @param storage 存储器的枚举 {@link FileClientFactoryImpl}
     * @param config  文件配置
     */
    <Config extends FileClientConfig> void createOrUpdateFileClient(String confKey, String storage, Config config);

}
