import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            usage();
            return;
        }

        File baseApk = new File(args[0]);
        File otherApk = new File(args[1]);
        ApkFile apkFile = new ApkFile();

        if (otherApk.isDirectory()) {
            List<String> diffs = new ArrayList<>(3);
            for (File o : otherApk.listFiles()) {
                if (diff(apkFile, baseApk, o)) {
                    diffs.add(o.getName());
                }
            }
            if (diffs.size() > 0) {
                System.out.println("\n\n\n总共有" + diffs.size() + "个渠道包有差异，分别是：");
                for (String d : diffs) {
                    System.out.println(d);
                }
            }
        } else {
            diff(apkFile, baseApk, otherApk);
        }
    }

    // 返回是否有差异
    private static boolean diff(ApkFile apkFile, File baseApk, File otherApk) {
        List<String>[] result = apkFile.differApk(baseApk, otherApk);
        // 去除渠道号的影响
        for (String d : result[1]) {
            if (d.endsWith("@weiche.cn")) {
                result[1].remove(d);
                break;
            }
        }
        for (String d : result[2]) {
            if (d.endsWith("@weiche.cn")) {
                result[2].remove(d);
                break;
            }
        }
        boolean hasDiff = result[0].size() > 0 || result[1].size() > 0 || result[2].size() > 0;
        if (hasDiff) {
            System.out.println("\n-----------------------" + otherApk.getName() + "-----------------------");
        }
        if (result[0].size() > 0) {
            System.out.println("差异：");
            for (String record : result[0]) {
                System.out.println(record);
            }
        }
        if (result[1].size() > 0) {
            System.out.println("多余：");
            for (String record : result[1]) {
                System.out.println(record);
            }
        }
        if (result[2].size() > 0) {
            System.out.println("缺失：");
            for (String record : result[2]) {
                System.out.println(record);
            }
        }
        return hasDiff;
    }

    private static void usage() {
        System.out.println(
                "Usage:\n" +
                        "   必须有两个参数，第一个参数为base apk\n" +
                        "   第二个参数为需要比较的apk或者apk目录"
        );
    }
}
