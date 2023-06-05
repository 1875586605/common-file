package common.file.core.client.db;

import cn.hutool.extra.spring.SpringUtil;
import common.file.core.client.AbstractFileClient;

/**
 * 基于 DB 存储的文件客户端的配置类
 */
public class DBFileClient extends AbstractFileClient<DBFileClientConfig> {

    private DBFileContentFrameworkDAO dao;

    public DBFileClient(String confKey, DBFileClientConfig config) {
        super(confKey, config);
    }

    @Override
    protected void doInit() {
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        getDao().insert(getConfKey(), path, content);
        // 拼接返回路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        getDao().delete(getConfKey(), path);
    }

    @Override
    public byte[] getContent(String path) {
        return getDao().selectContent(getConfKey(), path);
    }

    private DBFileContentFrameworkDAO getDao() {
        // 延迟获取，因为 SpringUtil 初始化太慢
        if (dao == null) {
            dao = SpringUtil.getBean(DBFileContentFrameworkDAO.class);
        }
        return dao;
    }

}
