package com.honor.packageTool;



public class Main {

    public static void main(String[] args) {
        System.out.println("exec java main.args="+args.length);
        for (int i = 0; i <args.length ; i++) {
            System.out.println(args[i]);
        }
        com.honor.packageTool.TestKt.main(args);



    }


}


