import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Client {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Entrez l'adresse IP du serveur: ");
        String serverIp = scanner.nextLine();

        System.out.print("Entrez le port du serveur: ");
        int port = scanner.nextInt();

        Socket MyClient;
        BufferedInputStream input;
        BufferedOutputStream output;
        int[][] board = new int[8][8];

        try {
            boolean isRed = false;
            MyClient = new Socket(serverIp, port);

            input = new BufferedInputStream(MyClient.getInputStream());
            output = new BufferedOutputStream(MyClient.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                char cmd = 0;
                cmd = (char) input.read();
                System.out.println(cmd);

                if (cmd == '1') {
                    isRed = true;
                    byte[] aBuffer = new byte[1024];
                    int size = input.available();
                    input.read(aBuffer, 0, size);
                    String s = new String(aBuffer).trim();
                    System.out.println(s);
                    String[] boardValues = s.split(" ");
                    int x = 0, y = 0;
                    for (int i = 0; i < boardValues.length; i++) {
                        board[x][y] = Integer.parseInt(boardValues[i]);
                        x++;
                        if (x == 8) {
                            x = 0;
                            y++;
                        }
                    }
                    System.out.println("Nouvelle partie! Vous jouez blanc, entrez votre premier coup : ");
                    String move = Main.aiMove(isRed);
                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                }

                if (cmd == '2') {
                    System.out.println("Nouvelle partie! Vous jouez noir, attendez le coup des blancs");
                    byte[] aBuffer = new byte[1024];
                    int size = input.available();
                    input.read(aBuffer, 0, size);
                    String s = new String(aBuffer).trim();
                    System.out.println(s);
                    String[] boardValues = s.split(" ");
                    int x = 0, y = 0;
                    for (int i = 0; i < boardValues.length; i++) {
                        board[x][y] = Integer.parseInt(boardValues[i]);
                        x++;
                        if (x == 8) {
                            x = 0;
                            y++;
                        }
                    }
                }

                if (cmd == '3') {
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    System.out.println("size :" + size);
                    input.read(aBuffer, 0, size);
                    String s = new String(aBuffer);
                    Pattern pattern = Pattern.compile("[A-Ha-h][1-8]");
                    Matcher matcher = pattern.matcher(s);
                    StringBuilder result = new StringBuilder();
                    while (matcher.find()) {
                        if (result.length() > 0) {
                            result.append(" ");
                        }
                        result.append(matcher.group().toLowerCase());
                    }
                    String finalResult = result.toString();
                    System.out.println("Dernier coup :" + s);
                    Main.playerMove(!isRed, finalResult);
                    System.out.println("Entrez votre coup : ");
                    String move = Main.aiMove(isRed);
                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                }

                if (cmd == '4') {
                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    String move = console.readLine();
                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                }

                if (cmd == '5') {
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer, 0, size);
                    String s = new String(aBuffer);
                    System.out.println("Partie Terminé. Le dernier coup joué est: " + s);
                    String move = console.readLine();
                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        scanner.close();
    }
}
