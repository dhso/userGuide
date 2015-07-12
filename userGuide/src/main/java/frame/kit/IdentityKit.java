package frame.kit;

import java.security.SecureRandom;
import java.util.UUID;

public class IdentityKit {
	private static SecureRandom random = new SecureRandom();

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间有-分割.
	 */
	public static String uuid() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
	 */
	public static String uuid2() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 封装JDK自带的UUID, 通过当前时间和uuid生成.
	 */
	public static String uuid3() {
		return System.currentTimeMillis() + "-" + uuid2();
	}

	/**
	 * 封装JDK自带的UUID, 通过当前时间生成.
	 */
	public static String uuid4() {
		return Long.toString(System.currentTimeMillis());
	}

	/**
	 * 使用SecureRandom随机生成Long.
	 */
	public static long randomLong() {
		return Math.abs(random.nextLong());
	}

	/**
	 * 基于Base62编码的SecureRandom随机生成bytes.
	 */
	public static String randomBase62(int length) {
		byte[] randomBytes = new byte[length];
		random.nextBytes(randomBytes);
		return EncodeKit.encodeBase62(randomBytes);
	}

	/**
	 * 获取随机数验证码
	 */
	public static String randomCaptcha(int length) {
		double code = (Math.random() * 9 + 1) * Math.pow(10, length - 1);
		return String.valueOf((int) Math.floor(code));
	}
}
