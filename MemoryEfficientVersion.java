import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class MemoryEfficientVersion{
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
            String[] ans = efficientVersion(str1,str2);
            float score = getScore(ans[0], ans[1]);
            float afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            float spaceUsed = (afterUsedMem - beforeUsedMem) / (1024);

            float endTime = System.nanoTime();
            float time = (endTime - startTime)/1000000000;

            writeToFile(ans[0], ans[1], score, time, spaceUsed);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] efficientVersion(String x, String y){
        int m = x.length();
        int n = y.length();
        String[] ans = new String[2];
        ans[0] = "";
        ans[1] = "";
        if(m == 0){
            for(int i=0; i<n; i++){
                ans[0] += "_";
                ans[1] += y.charAt(i);
            }
        }else if(n == 0){
            for(int i=0; i<m; i++){
                ans[0] += x.charAt(i);
                ans[1] += "_";
            }
        }else if(m == 1 || n == 1){
            ans = similarity(x, y);
        }else{
            int xmid = m/2;

            String x_l = x.substring(0, xmid);
            String x_r = x.substring(xmid, m);

            int ymid = partitionY(score(x_l, y, false), score(x_r, y, true));
            String y_l = y.substring(0, ymid);
            String y_r = y.substring(ymid, n);

            String[] ret1 = efficientVersion(x_l, y_l);
            String[] ret2 = efficientVersion(x_r, y_r);

            ans[0] = ret1[0] + ret2[0];
            ans[1] = ret1[1] + ret2[1];
        }
        return ans;
    }

    public static int partitionY(int[] scoreL, int[] scoreR){
        int n = scoreL.length;
        int min = Integer.MAX_VALUE;
        int index = 0;

        scoreR = reverseInt(scoreR);

        for(int i=0; i<n; ++i){
            if(scoreL[i]+scoreR[i] < min){
                min = scoreL[i] + scoreR[i];
                index = i;
            }
        }
        return index;
    }

    public static int[] score(String x, String y, boolean bool){
        int m = x.length();
        int n = y.length();
        int[] ans = new int[n+1];
        for(int j=0; j<=n; ++j){
            ans[j] = j * b;
        }
        if(bool){
            x = reverseString(x);
            y = reverseString(y);
        }
        for(int i=1; i<=m; ++i){
            int prev = ans[0];
            ans[0] += b;
            for(int j=1; j<=n; ++j){
                int cost = 0;
                if(x.charAt(i-1) == y.charAt(j-1)){
                    cost = prev;
                }else{
                    cost = prev + mismatchPen(x.charAt(i-1), y.charAt(j-1));
                }
                cost = min(cost, ans[j]+b, ans[j-1]+b);
                prev = ans[j];
                ans[j] = cost;
            }
        }
        return ans;
    }

    public static String[] similarity(String x, String y){
        String[] ans = new String[2];
        ans[0] = "";
        ans[1] = "";
        int m = x.length();
        int n = y.length();
        int[][] opt = new int[m+1][n+1];

        for(int i=0; i<=m; ++i){
            opt[i][0] = i * b;
        }
        for(int j=0; j<=n; ++j){
            opt[0][j] = j * b;
        }

        for(int i=1; i<=m; ++i){
            for(int j=1; j<=n; ++j){
                int cost = 0;
                if(x.charAt(i-1) == y.charAt(j-1)){
                    cost = opt[i-1][j-1];
                }else{
                    cost = opt[i-1][j-1] + mismatchPen(x.charAt(i-1), y.charAt(j-1));
                }
                opt[i][j] = min(cost, opt[i-1][j] + b, opt[i][j-1] + b);
            }
        }
        int i = m;
        int j = n;
        String[] align = new String[2];
        align[0] = "";
        align[1] = "";

        while(i>=1 || j>=1){
            if((i>0 && opt[i][j]== opt[i-1][j]+b) || j==0){
                align[0] += x.charAt(i-1);
                align[1] += "_";
                --i;
            }else if((j>0 && opt[i][j]== opt[i][j-1]+b) || i==0){
                align[0] += "_";
                align[1] += y.charAt(j-1);
                --j;
            }else if(opt[i][j] == opt[i-1][j-1]+mismatchPen(x.charAt(i-1), y.charAt(j-1)) || opt[i][j] == opt[i-1][j-1]){
                align[0] += x.charAt(i-1);
                align[1] += y.charAt(j-1);
                --i;
                --j;
            }
        }
        ans[0] = reverseString(align[0]);
        ans[1] = reverseString(align[1]);
        return ans;
    }

    /* Printing the final answer:
       the first 50 elements and the last 50 elements of the actual alignment
     */
    public static void writeToFile(String x, String y, float score, float time, float spaceUsed){
        String str = "";
        str += x.substring(0,50);
        str += " ";
        str += x.substring(y.length()-50, y.length());
        str += "\n";
        str += y.substring(0,50);
        str += " ";
        str += y.substring(y.length()-50, y.length());
        str += "\n";
        str = str + score + "\n"
                + time + "\n"
                + spaceUsed;
        File file = new File(outFile);
        try (FileWriter w = new FileWriter(file)) {
            w.write(str);
            w.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public static float getScore(String x, String y){
        int score = 0;
        for(int i=0; i<x.length(); i++){
            if(x.charAt(i) == '_' || y.charAt(i) == '_'){
                score += b;
            }else{
                score += mismatchPen(x.charAt(i), y.charAt(i));
            }
        }
        float scoref=score;
        return scoref;
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

    public static int min(int a, int b, int c){
        int min;
        if(a<=b){
            min = a;
        }else{
            min = b;
        }
        if(min<=c){
            return min;
        }else{
            return c;
        }
    }

    public static String reverseString(String str){
        char[] try1 = str.toCharArray();
        String output = "";

        for (int i = try1.length - 1; i >= 0; i--){
            output += try1[i];
        }
        return output;
    }

    public static int[] reverseInt(int[] x){
        int[] ret = new int[x.length];
        for (int i=0; i<x.length; i++){
            ret[i] = x[x.length-i-1];
        }
        return ret;
    }
}