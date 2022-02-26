package cn.v2rayj.util.http.utils;

import cn.v2rayj.util.http.constants.HttpContentType;
import cn.v2rayj.util.http.exception.HttpClientException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class FileRequestBody {

    public static RequestBody create(String contentType, final byte[] bytes, boolean isChunked) {
        if (bytes == null) {
            throw new HttpClientException("file content must be not null!");
        }
        return isChunked
                ? createChunkedFileRequestBody(contentType, bytes)
                : createFileRequestBody(contentType, bytes);
    }

    /**
     * 创建文件流请求
     */
    private static RequestBody createFileRequestBody(String contentType, final byte[] bytes) {
        return new RequestBody() {
            @Override
            @Nullable
            public MediaType contentType() {
                return MediaType.parse(null == contentType ? HttpContentType.FILE : contentType);
            }

            @Override
            public long contentLength() {
                return bytes.length;
            }

            @Override
            public void writeTo(@NotNull BufferedSink sink) throws IOException {
                sink.write(bytes, 0, bytes.length);
            }
        };
    }

    /**
     * 创建分片文件流请求
     */
    public static RequestBody createChunkedFileRequestBody(String contentType, final byte[] bytes) {
        return new RequestBody() {
            //计算分块数
            int offset = 0;
            int count = 0;
            final int segmentSize = 4096;

            @Override
            @Nullable
            public MediaType contentType() {
                return MediaType.parse(contentType);
            }

            @Override
            public long contentLength() {
                return bytes.length;
            }

            @Override
            public void writeTo(@NotNull BufferedSink sink) {
                count = bytes.length / segmentSize + (bytes.length % segmentSize != 0 ? 1 : 0);
                for (int i = 0; i < count; i++) {
                    int chunk = (i < count - 1) ? segmentSize : bytes.length - offset;
                    try {
                        sink.write(bytes, offset, chunk);//每次写入SEGMENT_SIZE 字节
                        sink.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    offset += chunk;
                }
            }
        };
    }
}
