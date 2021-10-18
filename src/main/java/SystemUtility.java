
public class SystemUtility {
	private static final String OS_LINUX = "linux";
	private static final String OS_WINDOWS = "windows";
	public static final String OS_NAME = System.getProperty("os.name");

	public static boolean isLinux() {
		return OS_NAME.toLowerCase().indexOf(OS_LINUX) >= 0;
	}

	public static boolean isWindows() {
		return OS_NAME.toLowerCase().indexOf(OS_WINDOWS) >= 0;
	}
}
