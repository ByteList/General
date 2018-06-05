package de.gamechest.updater.updaters;

import de.bytelist.jenkinsapi.JenkinsAPI;
import de.gamechest.updater.EnumFile;
import de.gamechest.updater.Updater;

import java.io.*;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * Created by ByteList on 25.12.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class JenkinsUpdater extends Thread {

    private final Updater updater = Updater.getInstance();
    private final Logger logger = updater.getLogger();

    private final JenkinsAPI jenkinsAPI;

    private final boolean loggedIn;
    private final String urlPath = "https://kvm.bytelist.de/jenkins/job/GameChest/job/old/job/";
    private final String endLocPath = "/home/minecraft/CloudSystem/";


    public JenkinsUpdater() {
        super("Jenkins Updater Thread");
        jenkinsAPI = new JenkinsAPI("apiUser", "Uf6UYSqSrgOGby01fSIe7dAkd1eSzVYggqH");

        String loginCheck = jenkinsAPI.getLoginCorrect("https://kvm.bytelist.de/jenkins/");
        if(!loginCheck.equals(JenkinsAPI.CORRECT_LOGIN_VARIABLE)) {
            logger.warning("Cannot check for updates:");
            logger.warning(loginCheck);
            loggedIn = false;
        } else {
            loggedIn = true;
        }
    }

    @Override
    public void run() {
        while (updater.isRunning) {
            try {
                update("GameChest-Spigot", urlPath+"GameChest-General/lastSuccessfulBuild/", urlPath+"GameChest-General/lastSuccessfulBuild/artifact/general-spigot/target/GameChest-Spigot.jar",
                        endLocPath+"Generals/plugins/");

                update("Lobby", urlPath+"GameChest-Lobby/lastSuccessfulBuild/", urlPath+"GameChest-Lobby/lastSuccessfulBuild/artifact/target/Lobby.jar",
                        endLocPath+"Templates/LOBBY/plugins/");

                update("Survival", urlPath+"GameChest-Survival/lastSuccessfulBuild/", urlPath+"GameChest-Survival/lastSuccessfulBuild/artifact/target/Survival.jar",
                        endLocPath+"Servers/permanent/Survival/plugins/update/", endLocPath+"Servers/permanent/Survival/plugins/");

                update("Verify", urlPath+"GameChest-General/lastSuccessfulBuild/", urlPath+"GameChest-General/lastSuccessfulBuild/artifact/verify/target/Verify.jar",
                        endLocPath+"Servers/permanent/Verify/plugins/update/", endLocPath+"Servers/permanent/Verify/plugins/");

                update("BuildPlugin", urlPath+"GameChest-BuildPlugin/lastSuccessfulBuild/", urlPath+"GameChest-BuildPlugin/lastSuccessfulBuild/artifact/target/BuildPlugin.jar",
                        "/home/minecraft/Build2/plugins/update/", "/home/minecraft/Build2/plugins/");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update(String name, String buildUrl, String artifactUrl, String endFileLocation) throws IOException {
        if(!loggedIn) return;
        int currentBuildNumber = Integer.parseInt(buildNumberFromJar(endFileLocation+name+".jar"));
        int lastSuccessfulBuild = Integer.parseInt(jenkinsAPI.getBuildNumber(buildUrl));

        if(currentBuildNumber < lastSuccessfulBuild) {
            logger.info("["+name+"] Update found! Current build: "+currentBuildNumber+" - New build: "+lastSuccessfulBuild);
            logger.info("["+name+"] Downloading...");
            if(!new File(EnumFile.DOWNLOADS.getPath()).exists()) {
                new File(EnumFile.DOWNLOADS.getPath()).mkdir();
            }
            logger.info("["+name+"] "+downloadFile(artifactUrl, EnumFile.DOWNLOADS.getPath()+name+".jar"));
            logger.info("["+name+"] "+moveFile(EnumFile.DOWNLOADS.getPath()+name+".jar", endFileLocation+name+".jar"));
        }
    }

    private void update(String name, String buildUrl, String artifactUrl, String endFileLocation, String checkFileLocation) throws IOException {
        if(!loggedIn) return;
        int currentBuildNumber = Integer.parseInt(buildNumberFromJar(checkFileLocation+name+".jar"));
        int lastSuccessfulBuild = Integer.parseInt(jenkinsAPI.getBuildNumber(buildUrl));

        if(currentBuildNumber < lastSuccessfulBuild) {
            if(new File(endFileLocation, name+".jar").exists()) {
                try {
                    if(Integer.parseInt(buildNumberFromJar(endFileLocation+name+".jar")) == lastSuccessfulBuild) {
                        return;
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            logger.info("["+name+"] Update found! Current build: "+currentBuildNumber+" - New build: "+lastSuccessfulBuild);
            logger.info("["+name+"] Downloading...");
            if(!new File(EnumFile.DOWNLOADS.getPath()).exists()) {
                new File(EnumFile.DOWNLOADS.getPath()).mkdir();
            }
            logger.info("["+name+"] "+downloadFile(artifactUrl, EnumFile.DOWNLOADS.getPath()+name+".jar"));
            logger.info("["+name+"] "+moveFile(EnumFile.DOWNLOADS.getPath()+name+".jar", endFileLocation+name+".jar"));
        }
    }

    private String downloadFile(String url, String downloadedFile) throws IOException {

        InputStream in = jenkinsAPI.getInputStream(url);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buf = new byte[1024];
        int n;
        System.out.println("Please wait... (File size: " + jenkinsAPI.getContentLength(url) + " bytes)");
        try {
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] response = out.toByteArray();

        FileOutputStream fos = new FileOutputStream(downloadedFile);
        fos.write(response);
        fos.close();
        return "Downloaded " + url + " to " + downloadedFile;
    }

    private String moveFile(String fileString, String toString) {
        File file = new File(fileString), to = new File(toString);

        if(file.renameTo(to)) {
            return "Moved file "+file.getName()+" to "+to.getPath();
        }
        return "Error while moving "+file.getName() + " to "+to.getPath();
    }

    private String buildNumberFromJar(String file) throws IOException {
        JarFile jarFile = new JarFile(file);
        Set<Entry<Object, Object>> entries = jarFile.getManifest().getMainAttributes().entrySet();

        for (Entry<Object, Object> entry : entries) {
            if (entry.getKey().toString().equals("Implementation-Version")) {
                String value = String.valueOf(entry.getValue());
                return value.replace(".", ":").split(":")[2];
            }
        }
        return "-1";
    }
}
