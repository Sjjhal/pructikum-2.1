import utils.FilesystemUtilities;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DiaryApplication {
    private static final int MAKS_ZAPYSIV = 50;
    private static String[] daty = new String[MAKS_ZAPYSIV];
    private static String[] zapysy = new String[MAKS_ZAPYSIV];
    private static int kilkistZapysiv = 0;
    private static Scanner vvid = new Scanner(System.in);
    private static SimpleDateFormat formatVidobrazhennyaDaty = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static SimpleDateFormat formatFailuDaty = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void run() {
        System.out.println("Відновити щоденник з файлу? (yes/no)");
        String vybir = vvid.nextLine();

        if (vybir.equalsIgnoreCase("yes")) {
            System.out.print("Введіть шлях до файлу: ");
            String path = vvid.nextLine();
            zavantazhytyZFailu(path);
        } else {
            System.out.println("Створено новий щоденник.");
            vibratyFormatDaty();
        }

        while (true) {
            System.out.println("\nМій щоденник");
            System.out.println("1. Додати запис");
            System.out.println("2. Видалити записи за датою");
            System.out.println("3. Переглянути всі записи");
            System.out.println("4. Змінити формат дати");
            System.out.println("5. Вийти");
            System.out.print("Оберіть дію: ");
            vybir = vvid.nextLine();

            switch (vybir) {
                case "1": dodatyZapys(); break;
                case "2": vydalytyZaDatoiu(); break;
                case "3": pokazatyVsiZapysy(); break;
                case "4": vibratyFormatDaty(); break;
                case "5": zberegtyDoFailu(); vvid.close(); return;
                default: System.out.println("Невірний вибір!");
            }
        }
    }

    private static void vibratyFormatDaty() {
        System.out.println("\nОберіть формат дати:");
        System.out.println("1. дд.мм.рррр ГГ:хх");
        System.out.println("2. мм/дд/рррр гг:мм a");
        System.out.println("3. рррр-мм-дд гг:хх:сс");
        System.out.println("4. Ввести свій формат");
        System.out.print("Ваш вибір: ");
        String vybir = vvid.nextLine();

        try {
            switch (vybir) {
                case "1": formatVidobrazhennyaDaty = new SimpleDateFormat("dd.MM.yyyy HH:mm"); break;
                case "2": formatVidobrazhennyaDaty = new SimpleDateFormat("MM/dd/yyyy hh:mm a"); break;
                case "3": formatVidobrazhennyaDaty = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); break;
                case "4":
                    System.out.print("Введіть свій формат: ");
                    String custom = vvid.nextLine();
                    formatVidobrazhennyaDaty = new SimpleDateFormat(custom);
                    break;
                default:
                    System.out.println("Невірний вибір, використовується формат за замовчуванням.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Невірний формат! Використовується формат за замовчуванням.");
            formatVidobrazhennyaDaty = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        }
    }

    private static void dodatyZapys() {
        if (kilkistZapysiv >= MAKS_ZAPYSIV) {
            System.out.println("Щоденник заповнений!");
            return;
        }

        String data = otrymatyKorektnuDatu();
        if (data == null) return;

        System.out.println("Введіть текст запису (пустий рядок для завершення):");
        String tekst = zbyrayTekst();

        daty[kilkistZapysiv] = data;
        zapysy[kilkistZapysiv] = tekst;
        kilkistZapysiv++;
        System.out.println("Запис додано успішно!");
    }

    private static String otrymatyKorektnuDatu() {
        while (true) {
            System.out.print("Введіть дату та час (у вибраному форматі): ");
            String vvedenaData = vvid.nextLine();
            try {
                Date parsed = formatVidobrazhennyaDaty.parse(vvedenaData);
                return formatFailuDaty.format(parsed);
            } catch (ParseException e) {
                System.out.println("Некоректна дата або час! Спробуйте ще раз.");
            }
        }
    }

    private static String zbyrayTekst() {
        StringBuilder tekst = new StringBuilder();
        while (true) {
            String ryadok = vvid.nextLine();
            if (ryadok.isEmpty()) {
                if (tekst.length() == 0) {
                    System.out.println("Запис не може бути порожнім!");
                    continue;
                }
                break;
            }
            if (tekst.length() > 0) tekst.append("\n");
            tekst.append(ryadok);
        }
        return tekst.toString();
    }

    private static void vydalytyZaDatoiu() {
        System.out.print("Введіть дату для видалення: ");
        String vvedenaData = vvid.nextLine();

        int kilkistVydalenykh = 0;
        try {
            Date d = formatVidobrazhennyaDaty.parse(vvedenaData);
            String formatData = formatFailuDaty.format(d);

            for (int i = 0; i < kilkistZapysiv; i++) {
                if (daty[i].equals(formatData)) {
                    for (int j = i; j < kilkistZapysiv - 1; j++) {
                        daty[j] = daty[j + 1];
                        zapysy[j] = zapysy[j + 1];
                    }
                    kilkistZapysiv--;
                    i--;
                    kilkistVydalenykh++;
                }
            }
        } catch (ParseException e) {
            System.out.println("Некоректна дата!");
            return;
        }

        System.out.println(kilkistVydalenykh > 0 ? "Видалено записів: " + kilkistVydalenykh : "Записів не знайдено");
    }

    private static void pokazatyVsiZapysy() {
        if (kilkistZapysiv == 0) {
            System.out.println("Щоденник порожній!");
            return;
        }

        System.out.println("\nВсі записи:");
        for (int i = 0; i < kilkistZapysiv; i++) {
            try {
                Date d = formatFailuDaty.parse(daty[i]);
                String formatted = formatVidobrazhennyaDaty.format(d);
                System.out.println("Дата: " + formatted);
                System.out.println("Запис:\n" + zapysy[i]);
                System.out.println("-------------------");
            } catch (ParseException e) {
                System.out.println("Помилка форматування дати для запису #" + (i + 1));
            }
        }
    }

    private static void zberegtyDoFailu() {
        System.out.print("Зберегти щоденник? (yes/no): ");
        String vybir = vvid.nextLine().trim();

        if (vybir.equalsIgnoreCase("yes")) {
            System.out.print("Введіть шлях до файлу: ");
            String path = vvid.nextLine().trim();

            if (path.isEmpty()) {
                System.out.println("Некоректний шлях!");
                return;
            }

            List<String> lines = new ArrayList<>();
            for (int i = 0; i < kilkistZapysiv; i++) {
                lines.add(daty[i]);
                lines.add(zapysy[i]);
                lines.add("");
            }

            try {
                FilesystemUtilities.saveToFile(path, lines);
                System.out.println("Успішно збережено у файл: " + path);
            } catch (IOException e) {
                System.out.println("Помилка збереження: " + e.getMessage());
            }
        }
    }

    private static void zavantazhytyZFailu(String path) {
        try {
            List<String> lines = FilesystemUtilities.loadFromFile(path);
            String data = null, tekst = "";
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    if (data != null && kilkistZapysiv < MAKS_ZAPYSIV) {
                        daty[kilkistZapysiv] = data;
                        zapysy[kilkistZapysiv] = tekst;
                        kilkistZapysiv++;
                    }
                    data = null;
                    tekst = "";
                } else if (data == null) {
                    data = line;
                } else {
                    tekst += tekst.isEmpty() ? line : "\n" + line;
                }
            }
            System.out.println("Щоденник завантажено успішно! Записів: " + kilkistZapysiv);
        } catch (IOException e) {
            System.out.println("Помилка при завантаженні файлу: " + e.getMessage());
        }
    }
}
