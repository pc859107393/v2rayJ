package cn.v2rayj.util;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by pc on 2017/8/8.
 */
public class FileUtil {

    public final static BigDecimal byteLength = BigDecimal.valueOf(1024);

    public final static String COMPANY_KB = "K";

    public final static String COMPANY_MB = "M";

    public final static String COMPANY_GB = "G";


    /**
     * 获取文件大小，KB形式，不足1kb，以1kb计算
     *
     * @param size 文件大小，单位(b)
     * @return
     */
    public static BigDecimal BYTE_TO_KB(BigDecimal size) {
        return size.divide(BigDecimal.valueOf(1024), 0, RoundingMode.UP);
    }


    /**
     * 输入流转byte[]
     */
    public static byte[] input2byte(InputStream inStream) {
        if (inStream == null) {
            return null;
        }
        byte[] in2b = null;
        BufferedInputStream in = new BufferedInputStream(inStream);
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int rc;
        try {
            while ((rc = in.read()) != -1) {
                swapStream.write(rc);
            }
            in2b = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIo(inStream, in, swapStream);
        }
        return in2b;
    }

    /**
     * 输入流转byte[]
     */
    public static byte[] file2byte(String filename) throws FileNotFoundException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException(filename);
        }
        return file2byte(file);
    }

    /**
     * 输入流转byte[]
     */
    public static byte[] file2byte(URI uri) throws FileNotFoundException {
        File file = new File(uri);
        if (!file.exists()) {
            throw new FileNotFoundException(uri.getPath());
        }
        return file2byte(file);
    }

    /**
     * 输入流转byte[]
     */
    public static byte[] file2byte(File file) throws FileNotFoundException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        BufferedInputStream in;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            int bufSize = 1024;
            byte[] buffer = new byte[bufSize];
            int len;
            while (-1 != (len = in.read(buffer, 0, bufSize))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 关闭流
     */
    public static void closeIo(Closeable... closeable) {
        if (null == closeable || closeable.length <= 0) {
            return;
        }
        for (Closeable cb : closeable) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                throw new RuntimeException(
                        FileUtil.class.getName(), e);
            }
        }
    }

    public static void input2File(InputStream is, String filePath) throws IOException {
        File file = new File(filePath);
        // 判断父文件是否存在,不存在就创建
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                // 新建文件目录失败，抛异常
                throw new IOException("创建文件(父层文件夹)失败, filepath: " + file.getAbsolutePath());
            }
        }
        // 判断文件是否存在，不存在则创建
        if (!file.exists()) {
            if (!file.createNewFile()) {
                // 新建文件失败，抛异常
                throw new IOException("创建文件失败, filepath: " + file.getAbsolutePath());
            }
        }

        FileOutputStream fileOut = null;
        FileChannel fileChannel = null;
        try {
            fileOut = new FileOutputStream(file);
            fileChannel = fileOut.getChannel();

            ReadableByteChannel readableChannel = Channels.newChannel(is);
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 32);
            while (true) {
                buffer.clear();
                if (readableChannel.read(buffer) == -1) {
                    readableChannel.close();
                    break;
                }
                buffer.flip();
                fileChannel.write(buffer);
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("保存文件失败, filepath: " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new IOException("保存文件失败, filepath: " + file.getAbsolutePath(), e);
        } finally {
            closeIo(fileOut, is, fileChannel);
        }
    }

    public static void bytes2File(byte[] bytes, String filePath) throws IOException {
        input2File(new ByteArrayInputStream(bytes), filePath);
    }

    /**
     * 保存文件
     *
     * @param filePath
     * @param fileName
     * @param content
     * @return
     */
    public static boolean save(String filePath, String fileName, byte[] content) {
        try {
            File path = new File(filePath);
            if (!path.exists()) {
                boolean mkdirs = path.mkdirs();
            }
            File file = new File(path, fileName);
            OutputStream os = new FileOutputStream(file);
            os.write(content, 0, content.length);
            os.flush();
            os.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 删除文件
     *
     * @param filePath
     * @param fileName
     */
    public static void del(String filePath, String fileName) {
        File file = new File(filePath.concat(fileName));
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 获取文件流
     *
     * @param filePath
     * @param fileName
     * @return
     */
    public static byte[] getFileByteArray(String filePath, String fileName) {
        byte[] buffer = null;
        try {
            File file = new File(filePath, fileName);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 4];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
            fis.close();
            bos.close();
        } catch (Exception e) {
            //TODO print Exception
        }
        return buffer;
    }

}
