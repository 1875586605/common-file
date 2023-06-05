package common.file.core.client.db;

/**
 * 文件内容 Framework DAO 接口
 */
public interface DBFileContentFrameworkDAO {

    /**
     * 插入文件内容
     *
     * @param confKey 配置编号
     * @param path    路径
     * @param content 内容
     */
    void insert(String confKey, String path, byte[] content);

    /**
     * 删除文件内容
     *
     * @param confKey 配置编号
     * @param path    路径
     */
    void delete(String confKey, String path);

    /**
     * 获得文件内容
     *
     * @param confKey 配置编号
     * @param path    路径
     * @return 内容
     */
    byte[] selectContent(String confKey, String path);

}
