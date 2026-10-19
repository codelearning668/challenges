package sk.mkrajcovic.challenges.util;


import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class ImageExtractor {

    static String SOURCE_PATH_CARS = "E:\\SteamLibrary\\steamapps\\common\\assettocorsa\\content\\cars";
    static String TARGET_PATH_CARS = "C:\\Users\\Asus\\IdeaProjects\\challenges\\src\\main\\resources\\cars\\";
    static String SOURCE_PATH_TRACKS = "E:\\SteamLibrary\\steamapps\\common\\assettocorsa\\content\\tracks";
    static String TARGET_PATH_TRACKS = "C:\\Users\\Asus\\IdeaProjects\\challenges\\src\\main\\resources\\tracks\\";

    public static void main(String[] args){

        copyCars(SOURCE_PATH_CARS, TARGET_PATH_CARS);
        copyTracks(SOURCE_PATH_TRACKS, TARGET_PATH_TRACKS);
    }

    private static void copyCars(String sourcePath, String targetPath){
        Map<String, Path> images = new HashMap<>();
        Path dir = Paths.get(sourcePath);
        try {
            Files.walk(dir).forEach(path -> {
                if(!path.endsWith("preview.jpg")){
                    return;
                }
                String imageName = path.toString().substring(sourcePath.length());
                imageName = imageName.substring(1);
                imageName = imageName.substring(0, imageName.indexOf("\\"));
                images.putIfAbsent(imageName, path);
                images.forEach((key, value) -> System.out.println(key + ": " + value));
                System.out.println(images.size());
            });

            for(Map.Entry<String, Path> entry : images.entrySet()){
                Files.copy(entry.getValue(), Paths.get(targetPath + entry.getKey() + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Doesn\t take different layouts into account(long, short...), I/ll add that when I/ll have next 20 free mins some other time
    private static void copyTracks(String sourcePath, String targetPath){
        Map<String, Path> images = new HashMap<>();
        Path dir = Paths.get(sourcePath);
        try {
            Files.walk(dir).forEach(path -> {
                if(!path.endsWith("map.png")){
                    return;
                }
                String imageName = path.toString().substring(sourcePath.length());
                imageName = imageName.substring(1);
                imageName = imageName.substring(0, imageName.indexOf("\\"));
                images.putIfAbsent(imageName, path);
                images.forEach((key, value) -> System.out.println(key + ": " + value));
                System.out.println(images.size());
            });

            for(Map.Entry<String, Path> entry : images.entrySet()){
                Files.copy(entry.getValue(), Paths.get(targetPath + entry.getKey() + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
