import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class BasicVersion{
    static float startTime = 0;
    static float beforeUsedMem = 0;
    static String outFile = "output.txt";

    //values for alphas
    static int[][] a = { {0,110,48,94},
            {110,0,118,48},
            {48,118,0,110},
            {94,48,110,0} };

    //value for delta
    static int b = 30;
    public static void main(String[] args){

        startTime = System.nanoTime();
        try {
            beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            String fileName = args[0];
            File file = new File(fileName);
            Scanner scan = new Scanner(file);
            String str1 = "";
            String str2 = "";
            str1 = scan.nextLine();
            while(scan.hasNextInt()) {
                int data = scan.nextInt();
                str1 = str1.substring(0, data+1) + str1 + str1.substring(data+1);
            }
            str2 = scan.next();
            while(scan.hasNextInt()) {
                int data = scan.nextInt();
                str2 = str2.substring(0, data+1) + str2 + str2.substring(data+1);
            }
            scan.close();
            dp(str1,str2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dp(String str1, String str2){
        int i, j;

        int m = str1.length();
        int n = str2.length();

        int opt[][] = new int[n + m + 1][n + m + 1];

        // initialising the table
        for (i = 0; i <= (n + m); i++){
            opt[i][0] = i * b;
            opt[0][i] = i * b;
        }

        // calculating the
        // minimum penalty
        for (i = 1; i <= m; i++){
            for (j = 1; j <= n; j++){
                if (str1.charAt(i - 1) == str2.charAt(j - 1)){
                    opt[i][j] = opt[i - 1][j - 1];
                }
                else{
                    opt[i][j] = Math.min(Math.min(opt[i - 1][j - 1] + mismatchPen(str1.charAt(i - 1), str2.charAt(j - 1)),
                                    opt[i - 1][j] + b),
                            opt[i][j - 1] + b );
                }
            }
        }

        // reconstructing the solution
        int l = n + m; // maximum possible length

        i = m; j = n;

        int xpos = l;
        int ypos = l;

        // Final answers for
        // the respective strings
        int xans[] = new int[l + 1];
        int yans[] = new int[l + 1];

        while ( !(i == 0 || j == 0)){
            if (str1.charAt(i - 1) == str2.charAt(j - 1)){
                xans[xpos--] = (int)str1.charAt(i - 1);
                yans[ypos--] = (int)str2.charAt(j - 1);
                i--; j--;
            }
            else if (opt[i - 1][j - 1] + mismatchPen(str1.charAt(i - 1), str2.charAt(j - 1)) == opt[i][j]){
                xans[xpos--] = (int)str1.charAt(i - 1);
                yans[ypos--] = (int)str2.charAt(j - 1);
                i--; j--;
            }
            else if (opt[i - 1][j] + b == opt[i][j]){
                xans[xpos--] = (int)str1.charAt(i - 1);
                yans[ypos--] = (int)'_';
                i--;
            }
            else if (opt[i][j - 1] + b == opt[i][j]){
                xans[xpos--] = (int)'_';
                yans[ypos--] = (int)str2.charAt(j - 1);
                j--;
            }
        }
        while (xpos > 0){
            if (i > 0) xans[xpos--] = (int)str1.charAt(--i);
            else xans[xpos--] = (int)'_';
        }
        while (ypos > 0){
            if (j > 0) yans[ypos--] = (int)str2.charAt(--j);
            else yans[ypos--] = (int)'_';
        }

        int id = 1;
        for (i = l; i >= 1; i--){
            if ((char)yans[i] == '_' && (char)xans[i] == '_'){
                id = i + 1;
                break;
            }
        }

        float afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        float spaceUsed = (afterUsedMem - beforeUsedMem) / (1024);

        float endTime = System.nanoTime();
        float time = (endTime - startTime)/1000000000;

        // Printing the final answer:
        // the first 50 elements and the last 50 elements of the actual alignment
        String str = "";
        for (i = id; i <= id+50; i++){
            str = str + (char)xans[i];
        }
        str = str + " ";
        for (i=l-50; i <= l; i++){
            str = str + (char)xans[i];
        }
        str = str + "\n";
        for (i = id; i <= id+50; i++){
            str = str + (char)yans[i];
        }
        str  = str + " ";
        for (i=l-50; i <= l; i++){
            str = str + (char)yans[i];
        }
        float cost=opt[m][n];
        str = str + "\n"
                + cost + "\n"
                + time + "\n"
                + spaceUsed;
        writeToFile(str);
        return;
    }

    public static void writeToFile(String str){
        File file = new File(outFile);
        try (FileWriter w = new FileWriter(file)) {
            w.write(str);
            w.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public static int mismatchPen(char x, char y){
        int i,j;
        if(x=='A'){
            i=0;
        }else if(x=='C'){
            i=1;
        }else if(x=='G'){
            i=2;
        }else{
            i=3;
        }
        if(y=='A'){
            j=0;
        }else if(y=='C'){
            j=1;
        }else if(y=='G'){
            j=2;
        }else{
            j=3;
        }
        return a[i][j];
    }
}