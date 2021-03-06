package com.server.template;

import com.open.net.server.GServer;
import com.open.net.server.impl.tcp.nio.NioClient;
import com.open.net.server.impl.tcp.nio.NioServer;
import com.open.net.server.message.Message;
import com.open.net.server.object.AbstractServerClient;
import com.open.net.server.object.AbstractServerMessageProcessor;
import com.open.net.server.object.ServerConfig;
import com.open.net.server.object.ServerLog;
import com.open.net.server.object.ServerLog.LogListener;
import com.open.util.log.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :  服务器入口
 */

public final class MainNioServer {

    public static void main_start(String [] args){
    	
    	//1.配置初始化
        ServerConfig mServerInfo = new ServerConfig();
        mServerInfo.initArgsConfig(args);
        mServerInfo.initFileConfig("./conf/lib.server.config");
        
        //2.数据初始化
        GServer.init(mServerInfo, NioClient.class);
        
        //3.日志初始化
        Logger.init("./conf/lib.log.config");
        Logger.addFilterTraceElement(ServerLog.class.getName());
        Logger.addFilterTraceElement(mLogListener.getClass().getName());
        Logger.v("-------Server------"+ mServerInfo.toString());
        
        //4.连接初始化
        Logger.v("-------Server------start---------");
        try {
            NioServer mNioServer = new NioServer(mServerInfo,mMessageProcessor,mLogListener);
            mNioServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        Logger.v("-------Server------end---------");
    }

    //-------------------------------------------------------------------------------------------
    public static AbstractServerMessageProcessor mMessageProcessor = new AbstractServerMessageProcessor() {

        private ByteBuffer mWriteBuffer  = ByteBuffer.allocate(128*1024);
        private long oldTime = System.currentTimeMillis();
        private long nowTime  = oldTime;
        
        protected void onReceiveMessage(AbstractServerClient client, Message msg){

            Logger.v("--onReceiveMessage()- rece  "+new String(msg.data,msg.offset,msg.length));
            String data ="MainNioServer--onReceiveMessage()--src_reuse_type "+msg.src_reuse_type
                    + " dst_reuse_type " + msg.dst_reuse_type
                    + " block_index " +msg.block_index
                    + " offset " +msg.offset;
            Logger.v("--onReceiveMessage()--reply "+data);
            
            byte[] response = data.getBytes();

            mWriteBuffer.clear();
            mWriteBuffer.put(response,0,response.length);
            mWriteBuffer.flip();
//        unicast(client,mWriteBuffer.array(),0,response.length);
            broadcast(mWriteBuffer.array(),0,response.length);
            mWriteBuffer.clear();
        }
        
        
		@Override
		public void onTimeTick() {
			nowTime = System.currentTimeMillis();
			if(nowTime - oldTime > 1000){
				oldTime = nowTime;
				
			}
		}

		@Override
		public void onClientEnter(AbstractServerClient client) {
			System.out.println("onClientEnter " + client.mClientId);
		}

		@Override
		public void onClientExit(AbstractServerClient client) {
			System.out.println("onClientExit " + client.mClientId);
		}
    };
    
    public static LogListener mLogListener = new LogListener(){

		@Override
		public void onLog(String tag, String msg) {
			Logger.v(msg);
		}
    };
}
