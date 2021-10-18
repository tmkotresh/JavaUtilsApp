package files;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileNameExtensionFilter;

import string.StringUtility;
import system.SystemUtility;
import threads.ThreadUtility;

public class FilesUtil {

	private static Set<PosixFilePermission> fullPermission = new HashSet<>();
	private static FileAttribute<Set<PosixFilePermission>> posixAttrPermissions = null;
	static {
		init();
	}

	private static void init() {
		fullPermission.add(PosixFilePermission.OWNER_EXECUTE);
		fullPermission.add(PosixFilePermission.OWNER_READ);
		fullPermission.add(PosixFilePermission.OWNER_WRITE);

		fullPermission.add(PosixFilePermission.GROUP_EXECUTE);
		fullPermission.add(PosixFilePermission.GROUP_READ);
		fullPermission.add(PosixFilePermission.GROUP_WRITE);

		fullPermission.add(PosixFilePermission.OTHERS_EXECUTE);
		fullPermission.add(PosixFilePermission.OTHERS_READ);
		fullPermission.add(PosixFilePermission.OTHERS_WRITE);
		posixAttrPermissions = PosixFilePermissions.asFileAttribute(fullPermission);
	}

	public static String findFileName(String dir, String extn) throws IOException {
		File[] filenames = findAllFiles(dir, extn);
		return dir + filenames[0].getName();
	}

	public static File[] findFilesStartsWithMatchingName(String dir, String strMatch) throws IOException {
		File fileDir = new File(dir);
		if (fileDir.exists() && fileDir.isDirectory()) {
			File[] fileList = fileDir.listFiles((file, name) -> name.toUpperCase().startsWith(strMatch.toUpperCase()));
			if (Objects.nonNull(fileList) && fileList.length > 0) {
				return fileList;
			}
		}
		throw new FileNotFoundException("No matching files found in directory:" + dir);
	}

	public static String findMatchingFileName(String dir, String strMatch, String extn) throws IOException {
		File[] fileNames = findAllFiles(dir, extn);
		String fileFound = null;
		for (int i = 0; i < fileNames.length; i++) {
			if (fileNames[i].getName().contains(strMatch)) {
				fileFound = fileNames[i].getName();
				break;
			}
		}
		if (Objects.isNull(fileFound)) {
			throw new FileNotFoundException(dir + strMatch + "*" + extn);
		}
		return dir + fileFound;
	}

	public static File[] findAllFiles(String dir, String extn) throws IOException {
		File fileSys = new File(dir);
		if (fileSys.exists() && fileSys.isDirectory()) {
			File[] filenames = fileSys.listFiles((dir1, name) -> name.toUpperCase().endsWith(extn.toUpperCase()));
			Arrays.sort(filenames, Comparator.comparingLong(File::lastModified).reversed());
			if (!Objects.nonNull(filenames) || filenames.length < 1) {
				throw new FileNotFoundException(dir);
			}
			return filenames;
		} else {
			System.out.println(" =>  dir not found : {} " + dir);
			throw new FileNotFoundException(dir);
		}
	}

	public static boolean isExists(String filePath) {
		if (StringUtility.isNotBlank(filePath)) {
			return Paths.get(filePath).toFile().exists();
		}
		return false;
	}

	public static void saveFileLocally(byte[] bytes, String filePath, String fileName) throws IOException {
		Files.createDirectories(Paths.get(filePath));
		Files.write(Paths.get(filePath + fileName), bytes);
	}

	public static void createDirectory(String filePath) {
		if (Objects.nonNull(filePath)) {
			File directory = new File(filePath);
			directory.mkdirs();
			System.out.println("Directory Created >> {}" + filePath);
		}
	}

	public static void createDirectoryWithPermissions(String strPath) {
		if (StringUtility.isBlank(strPath)) {
			System.out.println(" Path cannot be blank!");
			return;
		}
		Path path = null;
		try {
			path = Paths.get(strPath);
			System.out.println(">> osName:{} " + SystemUtility.OS_NAME);
			if (!path.toFile().exists()) {
				if (SystemUtility.isLinux()) {
					Files.createDirectories(path, posixAttrPermissions);
					System.out.println(" filePath:{} created.. " + strPath);
					Files.setPosixFilePermissions(path, fullPermission);
					System.out.println(" filePath:{} set permissions " + strPath);
				} else {
					Files.createDirectories(path);
					System.out.println(" filePath:{} created.. " + strPath);
				}
			}
		} catch (Exception e) {
			System.out.println(">> Failed to create directories : {} " + strPath);
			System.out.println("" + e);
		} finally {
			logPermissions(path);
		}
	}

	public static boolean isEmptyDirectory(File file) {
		return Objects.nonNull(file) && file.exists() && file.isDirectory() && Objects.nonNull(file.list())
				&& file.list().length == 0;
	}

	public static void delete(File file) {
		try {
			if (isEmptyDirectory(file) || (Objects.nonNull(file) && file.isFile())) {
				Files.delete(file.toPath());
				System.out.println(" Deleted file: {} " + file.getAbsolutePath());
			}
		} catch (Exception e) {
			System.out.println(">> Unable to delete file  " + e);
		}
	}

	public static void delete(Path path) {
		if (Objects.isNull(path)) {
			return;
		}
		File pFile = path.toFile();
		try {
			if (Objects.isNull(pFile) || !pFile.exists()) {
				System.out.println("Path Not Exists:{}" + path);
				return;
			}
			if (pFile.isDirectory() && Objects.nonNull(pFile.list()) && pFile.list().length > 0) {
				System.out.println("isDirectory:{}" + path);
				Arrays.stream(pFile.listFiles()).forEach(file -> {
					System.out.println("file:{}" + file);
					delete(file.toPath());
				});
			}
			delete(pFile);
		} catch (Exception e) {
			System.out.println(">> Unable to delete file: {} " + path.toFile().getAbsolutePath());
			System.out.println("" + e);
		}
	}

	public static Date getPreviousDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	public static boolean isModifiedToday(File file) {
		Date modifiedDate = new Date(file.lastModified());
		Date previousDate = null;
		try {
			previousDate = getPreviousDate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modifiedDate.after(previousDate);
	}

	public static File findPatternMatchingFile(String filePath, List<String> patterns) {
		File fileSys = new File(filePath);
		if (fileSys.exists() && fileSys.isDirectory()) {
			File[] filenames = fileSys.listFiles((dir1, name) -> matches(name, patterns));
			Arrays.sort(filenames, Comparator.comparingLong(File::lastModified).reversed());
			if (Objects.nonNull(filenames) && filenames.length > 0) {
				return filenames[0];
			}
		} else {
			System.out.println(" =>  dir not found : {} " + filePath);
		}
		return null;
	}

	private static boolean matches(String name, List<String> patterns) {
		Pattern fileExtnPtrn = null;
		for (String pattern : patterns) {
			fileExtnPtrn = Pattern.compile(pattern);
			if (fileExtnPtrn.matcher(name).matches()) {
				return true;
			}
		}
		return false;
	}

	public static void logPermissions(Path path) {
		while (Objects.nonNull(path)) {
			System.out.println();
			path = path.getParent();
		}
	}

	public static void deleteAsyncAfterMinutes(Path path, int sleepTime) {
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		try {
			CompletableFuture.supplyAsync(() -> {
				System.out.println("@Before wait>>>> {} " + path);
				ThreadUtility.sleepQuietly(TimeUnit.MINUTES.toMillis(sleepTime));
				System.out.println("@Before delete>>>>{} " + path);
				delete(path);
				System.out.println("@After delete>>>>{} " + path);
				return 0;
			}, executorService);
			System.out.println(">>");
		} catch (Exception e) {
			System.out.println(">>" + e);
		} finally {
			executorService.shutdown();
		}
	}

	public static void cleanOldSmdOutputFiles(String dirPath) {
		if (!Paths.get(dirPath).toFile().exists()) {
			System.out.println("invalid path >{} " + dirPath);
			return;
		}
		File rootDir = new File(dirPath);
		File[] files = rootDir.listFiles(new FileFilter() {
			private final FileNameExtensionFilter filter = new FileNameExtensionFilter("Compressed files", "zip",
					"prot", "log", "plmxml", "txt");

			public boolean accept(File file) {
				return filter.accept(file);
			}
		});

		if (Objects.nonNull(files) && files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				delete(files[i]);
			}
		} else {
			System.out.println("no files in dir >{} " + dirPath);
		}
	}

	public static void copyFileQuietly(String fileAbsPath, String fileName, String targetDir) {
		try {
			createDirectoryWithPermissions(targetDir);
			Files.copy(Paths.get(fileAbsPath), Paths.get(targetDir + File.separator + fileName));
		} catch (IOException e) {
			System.out.println(">>" + e);
		}
	}

}
