import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Client {
    public static void main(String[] args) {

        Socket MyClient;
        BufferedInputStream input;
        BufferedOutputStream output;
        int[][] board = new int[8][8];

        try {
            boolean isRed = false;
            MyClient = new Socket("localhost", 8888);

            input    = new BufferedInputStream(MyClient.getInputStream());
            output   = new BufferedOutputStream(MyClient.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while(1 == 1){
                char cmd = 0;

                cmd = (char)input.read();
                System.out.println(cmd);
                // Debut de la partie en joueur blanc
                if(cmd == '1'){
                    isRed = true;
                    byte[] aBuffer = new byte[1024];

                    int size = input.available();
                    //System.out.println("size " + size);
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer).trim();
                    System.out.println(s);
                    String[] boardValues;
                    boardValues = s.split(" ");
                    int x=0,y=0;
                    for(int i=0; i<boardValues.length;i++){
                        board[x][y] = Integer.parseInt(boardValues[i]);
                        x++;
                        if(x == 8){
                            x = 0;
                            y++;
                        }
                    }

                    System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
                    String move = null;
                    //move = console.readLine();
                    move = Main.aiMove(isRed);
                    output.write(move.getBytes(),0,move.length());
                    output.flush();
                }
                // Debut de la partie en joueur Noir
                if(cmd == '2'){
                    System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
                    byte[] aBuffer = new byte[1024];

                    int size = input.available();
                    //System.out.println("size " + size);
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer).trim();
                    System.out.println(s);
                    String[] boardValues;
                    boardValues = s.split(" ");
                    int x=0,y=0;
                    for(int i=0; i<boardValues.length;i++){
                        board[x][y] = Integer.parseInt(boardValues[i]);
                        x++;
                        if(x == 8){
                            x = 0;
                            y++;
                        }
                    }
                }


                // Le serveur demande le prochain coup
                // Le message contient aussi le dernier coup joue.
                if(cmd == '3'){
                    byte[] aBuffer = new byte[16];

                    int size = input.available();
                    System.out.println("size :" + size);
                    input.read(aBuffer,0,size);

                    String s = new String(aBuffer);
                    // Expression régulière pour trouver les positions
                    Pattern pattern = Pattern.compile("[A-Ha-h][1-8]");
                    Matcher matcher = pattern.matcher(s);

                    // StringBuilder pour construire le résultat
                    StringBuilder result = new StringBuilder();

                    while (matcher.find()) {
                        // Ajouter l'expression trouvée en minuscules
                        if (result.length() > 0) {
                            result.append(" ");
                        }
                        result.append(matcher.group().toLowerCase());
                    }

                    // Convertir StringBuilder en String
                    String finalResult = result.toString();

                    System.out.println("Dernier coup :"+ s);
                    Main.playerMove(!isRed,finalResult);
                    System.out.println("Entrez votre coup : ");
                    String move = null;
                    //move = console.readLine();
                    move = Main.aiMove(isRed);
                    output.write(move.getBytes(),0,move.length());
                    output.flush();

                }
                // Le dernier coup est invalide
                if(cmd == '4'){
                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    String move = null;
                    move = console.readLine();
                    output.write(move.getBytes(),0,move.length());
                    output.flush();

                }
                // La partie est terminée
                if(cmd == '5'){
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer);
                    System.out.println("Partie Terminé. Le dernier coup joué est: "+s);
                    String move = null;
                    move = console.readLine();
                    output.write(move.getBytes(),0,move.length());
                    output.flush();

                }
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }

    }

}