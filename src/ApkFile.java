import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

/**
 * Created by liuzhenhua on 2017/4/6 16:02.
 */
public class ApkFile {
    public List<String> readManifest(InputStream inputStream) {
        List<String> records = new ArrayList<>(5000);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.startsWith("Name")) {
                    String name = line;
                    String value = reader.readLine();
                    records.add(name);
                    records.add(value);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("读取MANIFEST.MF文件发生错误", e);
        }
        return records;
    }

    public List<String>[] differApk(File baseApk, File otherApk) {
        List<String> diff = new ArrayList<>(); // 不同的记录
        List<String> redundant = new ArrayList<>(); // 多余的记录
        List<String> missing = new ArrayList<>(); // 缺失的记录

        try {
            ZipFile baseApkObj = new ZipFile(baseApk);
            ZipFile otherApkObj = new ZipFile(otherApk);
            List<String> baseApkManifestRecords = readManifest(baseApkObj.getInputStream(baseApkObj.getEntry("META-INF/MANIFEST.MF")));
            List<String> otherApkManifestRecords = readManifest(otherApkObj.getInputStream(otherApkObj.getEntry("META-INF/MANIFEST.MF")));
            for (int baseIndex = 0, otherIndex = 0;
                 baseIndex < baseApkManifestRecords.size() && otherIndex < otherApkManifestRecords.size();
                 baseIndex += 2, otherIndex += 2) {

                if (baseApkManifestRecords.get(baseIndex).equals(otherApkManifestRecords.get(otherIndex))) {
                    if (!baseApkManifestRecords.get(baseIndex + 1).equals(otherApkManifestRecords.get(otherIndex + 1))) {
                        diff.add(baseApkManifestRecords.get(baseIndex).split(":")[1]);
                    }
                    continue;
                }

                // 不同的记录
                int otherRealIndex = otherApkManifestRecords.indexOf(baseApkManifestRecords.get(baseIndex));
                // other apk 多了几条记录
                if (otherRealIndex >= 0) {
                    for (; otherIndex < otherRealIndex; otherIndex++) {
                        if (otherApkManifestRecords.get(otherIndex).startsWith("Name")) {
                            redundant.add(otherApkManifestRecords.get(otherIndex).split(":")[1]);
                        }
                    }
                } else {
                    // other apk少了几条记录
                    int baseRealIndex = baseApkManifestRecords.indexOf(otherApkManifestRecords.get(otherIndex));
                    if (baseRealIndex >= 0) {
                        for (; baseIndex < baseRealIndex; baseIndex++) {
                            if (baseApkManifestRecords.get(baseIndex).startsWith("Name")) {
                                missing.add(baseApkManifestRecords.get(baseIndex).split(":")[1]);
                            }
                        }
                    } else {
                        redundant.add(otherApkManifestRecords.get(otherIndex).split(":")[1]);
                        missing.add(baseApkManifestRecords.get(baseIndex).split(":")[1]);
                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException("比较apk差异时发生错误", e);
        }
        return new List[]{diff, redundant, missing};
    }
}
