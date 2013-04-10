package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;

import camera.Camera;

import users.User;

public class Server
{
    private static int           _port;
    private static ServerSocket  _socket;
    private static ArrayList<Camera> list;
    
    public static void main(String[] args) throws Exception
    {
        try
        {
            _port   = (args.length == 1) ? Integer.parseInt(args[0]) : 8080;
            _socket = new ServerSocket(_port);

            System.out.println("TCP server is running on " + _port + "...");
            
            /*list = Camera.getAllCamera();
            
            for (Camera c : list) {
            	c.getImage();
            }*/
            
            ServerRuntime autoSave = new ServerRuntime();
            
            while (true)
            {
                ServerThread th = new ServerThread(_socket.accept());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                _socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public void loadConfig() {
    	
    }
}