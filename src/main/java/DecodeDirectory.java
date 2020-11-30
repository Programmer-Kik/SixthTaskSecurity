import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DecodeDirectory {
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.print("Введите ключевое слово (на английском, чувствительно к регистру): ");
        String key = in.nextLine();

        System.out.print("Введите путь к каталогу для расшифрования: ");
        File rootDir = new File(in.nextLine());

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

        String delimiter = "`";
        String secondDelimiter = "\n";
        FileReader reader = new FileReader(rootDir);
        BufferedReader bufferedReader = new BufferedReader(reader);

        StringBuilder text = new StringBuilder();
        String line = bufferedReader.readLine();
        text.append(line);
        while (line != null) {
            line = bufferedReader.readLine();
            if (line != null) {
                text.append("\n" + line);
            }
        }
        bufferedReader.close();
        reader.close();
        List<String> encryptedDirectory = Arrays.asList(text.toString().split(delimiter));

        String path = encryptedDirectory.get(0);
        String preparedKey = key.repeat(path.length() / key.length() + 1)
                .substring(0, path.length());
        StringBuilder decodedPath = new StringBuilder();
        for(int i = 0; i < path.length(); i++) {
            int index = 0;
            char[] lineInMatrix = matrix[Arrays.binarySearch(alphabet, preparedKey.charAt(i))];
            for (int j = 0; j < lineInMatrix.length; j++) {
                if(lineInMatrix[j] == path.charAt(i)) {
                    index = j;
                    break;
                }
            }
            decodedPath.append(matrix[0][index]);
        }

        File root = new File(decodedPath.toString());
        root.mkdir();

        for(int i = 1; i < encryptedDirectory.size(); i++) {
            String curFile = encryptedDirectory.get(i);
            int flag = Integer.parseInt(curFile.substring(0, 1));
            curFile = curFile.substring(1);
            decodedPath.delete(0, decodedPath.length());

            if (flag == 0) {
                preparedKey = key.repeat(curFile.length() / key.length() + 1)
                        .substring(0, curFile.length());
                for(int j = 0; j < curFile.length(); j++) {
                    int index = 0;
                    char[] lineInMatrix = matrix[Arrays.binarySearch(alphabet, preparedKey.charAt(j))];
                    for (int k = 0; k < lineInMatrix.length; k++) {
                        if(lineInMatrix[k] == curFile.charAt(j)) {
                            index = k;
                            break;
                        }
                    }
                    decodedPath.append(matrix[0][index]);
                }
                File file = new File(decodedPath.toString());
                file.mkdir();
            }
            else {
                String[] pathAndBytes = curFile.split(secondDelimiter);
                path = pathAndBytes[0];
                preparedKey = key.repeat(path.length() / key.length() + 1)
                        .substring(0, path.length());
                for(int j = 0; j < path.length(); j++) {
                    int index = 0;
                    char[] lineInMatrix = matrix[Arrays.binarySearch(alphabet, preparedKey.charAt(j))];
                    for (int k = 0; k < lineInMatrix.length; k++) {
                        if(lineInMatrix[k] == path.charAt(j)) {
                            index = k;
                            break;
                        }
                    }
                    decodedPath.append(matrix[0][index]);
                }
                File file = new File(decodedPath.toString());
                file.createNewFile();

                String bytes = pathAndBytes[1];
                StringBuilder decodedBytes = new StringBuilder();
                preparedKey = key.repeat(bytes.length() / key.length() + 1)
                        .substring(0, bytes.length());
                for(int j = 0; j < bytes.length(); j++) {
                    int index = 0;
                    char[] lineInMatrix = matrix[Arrays.binarySearch(alphabet, preparedKey.charAt(j))];
                    for (int k = 0; k < lineInMatrix.length; k++) {
                        if(lineInMatrix[k] == bytes.charAt(j)) {
                            index = k;
                            break;
                        }
                    }
                    decodedBytes.append(matrix[0][index]);
                }

                FileWriter writer = new FileWriter(file);
                String message = decodedBytes.toString();
                writer.write(message);
                writer.close();
            }
        }
        rootDir.delete();
    }
}
