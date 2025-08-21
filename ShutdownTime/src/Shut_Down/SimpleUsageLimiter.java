package Shut_Down;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class SimpleUsageLimiter {
	private static final int LIMIT_MINUTES = 120;
	private static final String FILE = "C:\\SD\\ShutdownTime\\usage.txt";
	private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy");

	public static void main(String[] args) throws Exception {
		LocalDate today = LocalDate.now();

		// đọc số phút đã dùng hôm nay
		int usedMinutes = readUsage(today);

		// nếu đủ 120p thì tắt ngay
		if (usedMinutes >= LIMIT_MINUTES) {
			shutdownNow();
			return;
		}

		Path path = Paths.get(FILE);
		System.out.println("File thực tế nằm ở: " + path.toAbsolutePath());

		long start = System.currentTimeMillis();
		System.out.println("Ngày: " + today.format(FORMAT));
		System.out.println("Bạn còn " + (LIMIT_MINUTES - usedMinutes) + " phút để dùng.");

		// vòng lặp giả lập (check mỗi phút)
		while (true) {
			Thread.sleep(60 * 1000); // 1 phút
			int minutes = (int) ((System.currentTimeMillis() - start) / 1000 / 60);
			int total = usedMinutes + minutes;
			System.out.println("hello");
			if (total >= LIMIT_MINUTES) {
				writeUsage(today, LIMIT_MINUTES);
				shutdownNow();
				break;
			} else {
				writeUsage(today, total);
			}
		}
	}

	// đọc file usage.txt
	private static int readUsage(LocalDate today) {
		try {
			if (!Files.exists(Paths.get(FILE))) {
				writeUsage(today, 0); // tạo mới nếu chưa có
				return 0;
			}
			List<String> lines = Files.readAllLines(Paths.get(FILE));
			if (!lines.isEmpty()) {
				String[] parts = lines.get(0).split(":");
				LocalDate fileDate = LocalDate.parse(parts[0].trim(), FORMAT);
				int mins = Integer.parseInt(parts[1].trim());

				if (fileDate.equals(today)) {
					return mins; // cùng ngày -> dùng tiếp
				} else {
					writeUsage(today, 0); // khác ngày -> reset 0
					return 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// ghi vào file theo format "ngày: phút"
	private static void writeUsage(LocalDate today, int minutes) {
		try {
			String line = today.format(FORMAT) + ": " + minutes;
			Files.write(Paths.get(FILE), Collections.singletonList(line));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// tắt máy bằng vbs
	private static void shutdownNow() {
		try {
			File file = new File("shutdown.vbs");
			if (!file.exists()) {
				System.out.println("Không tìm thấy shutdown.vbs. Hãy tự tạo file này trước!");
				return;
			}
			Runtime.getRuntime().exec("wscript " + file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}