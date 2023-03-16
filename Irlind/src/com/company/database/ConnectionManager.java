package com.company.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager {
    private static Connection connection=null;

    private static String HOST="localhost";
    private static String PORT="5432";
    private static String DATABASE="postgres";
    private static String USER="postgres";
    private static String PW="12345678";

    public static Connection getConnection(){
        if(connection==null){
            String connectionString="jdbc:postgresql://"+HOST+":"+PORT+"/"+DATABASE;
            try{
                Connection con=DriverManager.getConnection(connectionString,USER,PW);
                connection=con;
                return con;
            }
            catch (Exception e){
                System.out.println("Connection to Database could not be established");
                return null;
            }
        }
        else{
            return connection;
        }
    }
}