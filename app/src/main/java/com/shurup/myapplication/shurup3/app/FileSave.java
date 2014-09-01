package com.shurup.myapplication.shurup3.app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class FileSave {
    final String DIR_SD = "/storage/extSdCard/Android/MyApp/";

    public void save(String action,String fName, StringBuffer data){
        // получаем путь к SD
        File sdPath;
        // добавляем свой каталог к пути
        sdPath = new File(DIR_SD+action+"/");
        // создаем каталог
        sdPath.mkdirs();
        File sdFile = new File(sdPath, fName);
        try {
            FileWriter wfile = new FileWriter(sdFile, true);
            // пишем данные
            wfile.append(data);
            wfile.flush();
            // закрываем поток
            wfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
