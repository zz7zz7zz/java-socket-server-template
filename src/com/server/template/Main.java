package com.server.template;
/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :  服务器入口
 */

public class Main {

    public static void main(String [] args){
        MainNioServer.main_start(args); 
//        MainBioServer.main_start(args);
//        MainUdpNioServer.main_start(args);
//        MainUdpBioServer.main_start(args);
    }

}
