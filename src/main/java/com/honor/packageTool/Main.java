package com.honor.packageTool;

public class Main {

    public static void main(String[] args) {

        System.out.print("exec java main.args:");
        for (String item : args) {
            System.out.print(item+"\t");
        }
        System.out.println();
        com.honor.packageTool.TestKt.main(args);
    }


}



