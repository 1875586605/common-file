package common.file.core.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import common.file.config.FileConfigConfiguration;
import common.file.config.FileConfigDTO;
import common.file.core.client.FileClient;
import common.file.core.client.FileClientFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

/**
 * @author cjb
 * @date 2023/5/9 10:17
 * @describe 文件上传连接工具
 */
@Slf4j
@Service
public class FileClientUtils {

    /**
     * Master FileClient 对象，有且仅有一个
     */
    private FileClient masterFileClient;

    private static FileClientUtils fileClientUtils;


    @Resource
    private FileClientFactory fileClientFactory;

    @Resource
    private FileConfigConfiguration fileConfigConfiguration;

    @PostConstruct
    public void init() {
        // 获取所有配置
        List<FileConfigDTO> configs = fileConfigConfiguration.getFileConfList();
        // 加载所有配置

        // 第二步：构建缓存：创建或更新文件 Client
        configs.forEach(config -> {
            fileClientFactory.createOrUpdateFileClient(config.getName(), config.getStorage(), config.getConfig());
            // 如果是 master，进行设置
            if (Boolean.TRUE.equals(config.getMaster())) {
                masterFileClient = fileClientFactory.getFileClient(config.getName());
            }
        });
        log.info("[initLocalCache][缓存文件配置，数量为:{}]", configs.size());

        // 初始化静态工具
        fileClientUtils = this;
        fileClientUtils.fileClientFactory = this.fileClientFactory;
        fileClientUtils.fileConfigConfiguration = this.fileConfigConfiguration;
    }

    /**
     * 获得 Master 文件客户端
     *
     * @return 文件客户端
     */
    public static FileClient getMasterFileClient() {
        return fileClientUtils.masterFileClient;
    }


    /**
     * 获得指定编号的文件客户端
     *
     * @param confKey 配置编号
     * @return 链接对象
     */
    public static FileClient getFileClient(String confKey) {
        return fileClientUtils.fileClientFactory.getFileClient(confKey);
    }

    /**
     * 上传文件
     * @param inputStream 文件流
     * @return
     */
    public static String uploadFile(InputStream inputStream) {
        return uploadFile(IoUtil.readBytes(inputStream));
    }

    /**
     * 上传文件返回文件路径
     *
     * @param name        文件名称
     * @param inputStream 文件流
     * @return
     */
    public static String uploadFile(String name,InputStream inputStream) {
        return uploadFile(name, IoUtil.readBytes(inputStream));
    }

    /**
     * 上传文件
     * @param content 文件byte
     * @return
     */
    public static String uploadFile(byte[] content) {
        return uploadFile(null, null, content);
    }

    /**
     * 上传文件返回文件路径
     *
     * @param name        文件名称
     * @param content 文件字节对象
     * @return
     */
    public static String uploadFile(String name,byte[] content) {
        return uploadFile(name,null, content);
    }

    /**
     * 上传文件
     * @param name        文件名称
     * @param content 文件byte
     * @param confKey 链接配置key
     * @return
     */
    public static String uploadFile(String name,byte[] content,String confKey) {
        return uploadFile(name, null, content,confKey);
    }


    /**
     * 上传文件
     * @param content 文件byte
     * @param confKey 链接配置key
     * @return
     */
    public static String uploadFile(byte[] content,String confKey) {
        return uploadFile(null, null, content,confKey);
    }




    /**
     * 上传文件返回文件路径
     *
     * @param name        文件名称
     * @param path        文件存储路径
     * @param inputStream 文件输入流
     * @return
     */
    public static String uploadFile(String name, String path, InputStream inputStream) {
        return uploadFile(name, path, IoUtil.readBytes(inputStream));
    }

    /**
     * 上传文件返回文件路径
     *
     * @param name        文件名称
     * @param path        文件存储路径
     * @param inputStream 文件输入流
     * @param confKey     链接对象配置key
     * @return
     */
    public static String uploadFile(String name, String path, InputStream inputStream, String confKey) {
        FileClient fileClient = getFileClient(confKey);
        return uploadFile(name, path, IoUtil.readBytes(inputStream), fileClient);
    }

    public static String uploadFile(String name, String path, byte[] content, String confKey) {
        FileClient fileClient = getFileClient(confKey);
        return uploadFile(name, path, content, fileClient);
    }

    /**
     * 上传文件返回文件路径
     *
     * @param name    文件名称
     * @param path    文件存储路径
     * @param content 文件字节对象
     * @return
     */
    @SneakyThrows
    public static String uploadFile(String name, String path, byte[] content) {
        // 计算默认的 path 名
        String type = FileTypeUtils.getMineType(content, name);
        if (StrUtil.isEmpty(path)) {
            path = FileUtils.generatePath(content, name);
        }
        FileClient client = getMasterFileClient();
        Assert.notNull(client, "客户端(master) 不能为空");
        return client.upload(content, path, type);
    }

    /**
     * 上传文件返回文件路径
     *
     * @param name    文件名称
     * @param path    文件存储路径
     * @param content 文件字节对象
     * @param client  指定链接对象
     * @return
     */
    @SneakyThrows
    public static String uploadFile(String name, String path, byte[] content, FileClient client) {
        // 计算默认的 path 名
        String type = FileTypeUtils.getMineType(content, name);
        if (StrUtil.isEmpty(path)) {
            path = FileUtils.generatePath(content, name);
        }
        Assert.notNull(client, "客户端({}) 不能为空", client.getConfKey());
        return client.upload(content, path, type);
    }


    /**
     * 删除文件
     *
     * @param path 文件路径
     * @return
     */
    @SneakyThrows
    public static void deleteFile(String path) {
        FileClient client = getMasterFileClient();
        Assert.notNull(client, "客户端(master) 不能为空");
        client.delete(path);
    }

    /**
     * 删除文件
     *
     * @param path    文件路径
     * @param confKey 链接配置key
     * @return
     */
    @SneakyThrows
    public static void deleteFile(String path, String confKey) {
        FileClient client = getFileClient(confKey);
        Assert.notNull(client, "客户端({}) 不能为空", client.getConfKey());
        client.delete(path);
    }

    /**
     * 下载文件
     *
     * @param path 文件路径
     * @return
     */
    @SneakyThrows
    public static byte[] getFileContent(String path) {
        FileClient client = getMasterFileClient();
        Assert.notNull(client, "客户端(master) 不能为空");
        return client.getContent(path);
    }

    /**
     * 下载文件
     *
     * @param path 文件路径
     * @return
     */
    @SneakyThrows
    public static byte[] getFileContent(String path, String confKey) {
        FileClient client = getFileClient(confKey);
        Assert.notNull(client, "客户端({}) 不能为空", client.getConfKey());
        return client.getContent(path);
    }

}
