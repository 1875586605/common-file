package common.file.core.enums;

import cn.hutool.core.util.ArrayUtil;
import common.file.core.client.FileClient;
import common.file.core.client.FileClientConfig;
import common.file.core.client.db.DBFileClient;
import common.file.core.client.db.DBFileClientConfig;
import common.file.core.client.ftp.FtpFileClient;
import common.file.core.client.ftp.FtpFileClientConfig;
import common.file.core.client.local.LocalFileClient;
import common.file.core.client.local.LocalFileClientConfig;
import common.file.core.client.s3.S3FileClient;
import common.file.core.client.s3.S3FileClientConfig;
import common.file.core.client.sftp.SftpFileClient;
import common.file.core.client.sftp.SftpFileClientConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件存储器枚举
 *
 * @author
 */
@AllArgsConstructor
@Getter
public enum FileStorageEnum {

    DB("db", DBFileClientConfig.class, DBFileClient.class),

    LOCAL("local", LocalFileClientConfig.class, LocalFileClient.class),
    FTP("ftp", FtpFileClientConfig.class, FtpFileClient.class),
    SFTP("sftp", SftpFileClientConfig.class, SftpFileClient.class),

    S3("s3", S3FileClientConfig.class, S3FileClient.class),
    ;

    /**
     * 存储器
     */
    private final String storage;

    /**
     * 配置类
     */
    private final Class<? extends FileClientConfig> configClass;
    /**
     * 客户端类
     */
    private final Class<? extends FileClient> clientClass;

    public static FileStorageEnum getByStorage(String storage) {
        return ArrayUtil.firstMatch(o -> o.getStorage().equals(storage), values());
    }

}
