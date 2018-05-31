package vn.iadd.oim.util;

/**
 * ObjectUtil
 * Object util for OIM
 * 
 * @author DaiNV
 * @since 20180531
 *
 */
public class ObjectUtil {

	/**
	 * Try close 
	 * @param closeable AutoCloseable
	 */
	public static void tryClose(AutoCloseable closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
