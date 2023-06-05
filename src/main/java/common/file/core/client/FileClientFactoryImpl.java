package common.file.core.client;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import common.file.core.enums.FileStorageEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 文件客户端的工厂实现类
 */
@Slf4j
public class FileClientFactoryImpl implements FileClientFactory {

    /**
     * 文件客户端 Map
     * key：配置编号
     */
    private final ConcurrentMap<String, AbstractFileClient<?>> clients = new ConcurrentHashMap<>();

    @Override
    public FileClient getFileClient(String configKey) {
        AbstractFileClient<?> client = clients.get(configKey);
        if (client == null) {
            log.error("[getFileClient][配置编号({}) 找不到客户端]", configKey);
        }
        return client;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Config extends FileClientConfig> void createOrUpdateFileClient(String confKey, String storage, Config config) {
        AbstractFileClient<Config> client = (AbstractFileClient<Config>) clients.get(confKey);
        if (client == null) {
            client = this.createFileClient(confKey, storage, config);
            client.init();
            clients.put(client.getConfKey(), client);
        } else {
            client.refresh(config);
        }
    }

    @SuppressWarnings("unchecked")
    private <Config extends FileClientConfig> AbstractFileClient<Config> createFileClient(
            String confKey, String storage, Config config) {
        FileStorageEnum storageEnum = FileStorageEnum.getByStorage(storage);
        Assert.notNull(storageEnum, String.format("文件配置(%s) 为空", storageEnum));
        // 创建客户端
        return (AbstractFileClient<Config>) ReflectUtil.newInstance(storageEnum.getClientClass(), confKey, config);
    }

}
