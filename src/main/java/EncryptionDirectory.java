import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class EncryptionDirectory {
    public static void main(String[] args) throws Exception {
        String delimiter = "`";
        String secondDelimiter = "\n";
        Scanner in = new Scanner(System.in);
        System.out.print("Введите ключевое слово (на английском, чувствительно к регистру): ");
        String key = in.nextLine();

        System.out.print("Введите путь к каталогу для шифрования: ");
        String path = in.nextLine();
        File rootDir = new File(path);

        char[] alphabet = new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G',
                'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
                'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c',
                'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
                'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '!', '@', '\"', '#', '№', ';', '$', '%', ':', '^', '&',
                '?', '*', '(', ')', '-', '_', '+', '=', '[', '{', ']',
                '}', '\\', '|', '/', '\'', ',', '<', '.', '>'};
        Arrays.sort(alphabet);

        char[][] matrix = new char[alphabet.length][alphabet.length];
        for (int i = 0, k; i < matrix.length; i++) {
            k = i;
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = alphabet[k];
                k++;
                if (k == alphabet.length) {
                    k = 0;
                }
            }
        }

        List<String> result = new ArrayList<>();
        Queue<File> fileTree = new PriorityQueue<>();

        String preparedKey = key.repeat(path.length() / key.length() + 1)
                .substring(0, path.length());

        StringBuilder encodedFile = new StringBuilder();
        for (int i = 0; i < path.length(); i++) {
            encodedFile.append(matrix[Arrays.binarySearch(alphabet,
                    preparedKey.charAt(i))][Arrays.binarySearch(alphabet, path.charAt(i))]);
        }
        result.add(encodedFile.toString());

        Collections.addAll(fileTree, rootDir.listFiles());
        while (!fileTree.isEmpty()) {
            File curFile = fileTree.remove();
            String pathCurFile = curFile.getPath();
            encodedFile.delete(0, encodedFile.length());

            preparedKey = key.repeat(pathCurFile.length() / key.length() + 1)
                    .substring(0, pathCurFile.length());
            for(int i = 0; i < pathCurFile.length(); i++) {
                encodedFile.append(matrix[Arrays.binarySearch(alphabet,
                        preparedKey.charAt(i))][Arrays.binarySearch(alphabet, pathCurFile.charAt(i))]);
            }
            if (curFile.isDirectory()) {
                Collections.addAll(fileTree, curFile.listFiles());

                result.add("0" + encodedFile.toString());
            }
            else
            {
                String pathString = encodedFile.toString();

                String curFileString = Files.readString(Path.of(pathCurFile));
                preparedKey = key.repeat(curFileString.length() / key.length() + 1)
                        .substring(0, curFileString.length());
                encodedFile.delete(0, encodedFile.length());
                for(int i = 0; i < curFileString.length(); i++) {
                    encodedFile.append(matrix[Arrays.binarySearch(alphabet,
                            preparedKey.charAt(i))][Arrays.binarySearch(alphabet, curFileString.charAt(i))]);
                }
                result.add("1" + pathString + secondDelimiter + encodedFile.toString());
            }
        }

        File file = new File("src/main/resources/EncryptedDirectory.txt");
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for(int i = 0; i < result.size(); i++) {
            bufferedWriter.write(result.get(i) + delimiter);
        }
        bufferedWriter.close();
        fileWriter.close();

        Files.walk(rootDir.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}