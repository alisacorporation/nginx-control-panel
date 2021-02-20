package com.xeon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class App {
    private JButton btnMsg;
    private JPanel panelMain;
    private JLabel ngPic;
    private JButton startButton;
    private JButton stopButton;
    private JButton restartButton;
    private JLabel status;
    private JLabel labelMsg;

    public App() throws IOException {
        // Create new image from file
        ImageIcon nginxImg = new ImageIcon(getClass().getClassLoader().getResource("nginx.png"));
        ngPic.setPreferredSize(new Dimension(80, 80));
        ngPic.setIcon(nginxImg);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    exec(new String[]{"/bin/bash", "-c", "systemctl start nginx"}, true, status);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    exec(new String[]{"/bin/bash", "-c", "systemctl stop nginx"}, true, status);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    exec(new String[]{"/bin/bash", "-c", "systemctl restart nginx"}, true, status);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    public static void refreshStatus(JLabel status, int delay) throws IOException {
        CompletableFuture.delayedExecutor(delay, TimeUnit.SECONDS).execute(() -> {
            // Your code here executes after 5 seconds!
            try {
                exec(new String[]{
                        "/bin/bash",
                        "-c",
                        " systemctl status nginx | grep active | awk '{print $2,$3,$9,$10}'"
                }, false, status);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) throws IOException {
        JFrame jFrame = new JFrame("App");
        Dimension dim = new Dimension(300, 250);
        jFrame.setPreferredSize(dim);
        jFrame.setSize(dim);
        jFrame.setResizable(false);
        jFrame.setContentPane(new App().panelMain);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    public static void exec(String[] cmd, boolean refresh, JLabel status) throws IOException {

        System.err.println("exec method executed!!!");
        Process p = Runtime.getRuntime().exec(cmd);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s;
        while ((s = stdInput.readLine()) != null) {
            status.setText(s);
        }

        // read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }

        if (refresh) {
            refreshStatus(status, 2);
        }
    }
}
