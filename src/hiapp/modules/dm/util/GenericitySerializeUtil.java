package hiapp.modules.dm.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by shtou on 2018/4/14.
 */
public final class GenericitySerializeUtil {
    /**
     * 序列化对象
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        byte[] bytes = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            bytes = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return bytes;
    }

    /**
     * 反序列化对象
     * @param bytes
     * @return
     */
    public static <T> T unserialize(byte[] bytes) {
        Object obj = null;
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
            ois.close();
            bais.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T)obj;
    }
}
