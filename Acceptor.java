/*
 * @Author: Yupeng Hou
 * @Id: a1783922
 * @Semester: 2
 * @Year: 2021
 * @Assignment Number: 3
 * @LastEditors: Yupeng Hou
 */

import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Acceptor {

  static ServerSocket acceptServerSocket = null;
  static int promise = 0;
  static Integer value = null;

  public static void main(String[] args) {
    String[] causesOfDeath = {
      "====== Just dead, no reason. ======",
      "====== Suffered a power off. ======",
      "====== Server exploded. ======",
      "====== A hacker paralyzed my. ======",
      "====== Got plugged out by some one. ======",
      "====== AI got tired, went for resting. ======",
    };

    byte[] byteBuffer4 = new byte[4];
    byte[] byteBuffer1 = new byte[1];
    int delay = 4;
    if (args.length >= 1 && args[0] != null && !args[0].isEmpty()) {
      delay = Integer.parseInt(args[0]);
    }

    int dangerous = 0;
    if (args.length >= 2 && args[1] != null && !args[1].isEmpty()) {
      dangerous = Integer.parseInt(args[1]);
    }

    Random randomDelay = new Random();

    // Danger
    if (dangerous != 0 && randomDelay.nextInt(100) % 2 != 0) {
      System.err.println("Die young...:-(");
      return;
    }

    // Get a port for this server
    try {
      Socket PublicServices = new Socket("localhost", 8080);
      // System.out.println(LocalTime.now() + " - Asking for a port number.");
      while (true) {
        // Send 0 to request for a port number
        PublicServices.getOutputStream().write(("0").getBytes());
        PublicServices.getOutputStream().flush();

        PublicServices.getInputStream().read(byteBuffer4);
        int portNum = Integer.parseInt(
          new String(byteBuffer4, StandardCharsets.UTF_8)
        );

        // Use this port number to create server
        try {
          acceptServerSocket = new ServerSocket(portNum);
          // System.out.printf(
          //   LocalTime.now() +
          //   " - Acceptor on port %d started, listening for proposals...\n\n",
          //   portNum
          // );
        } catch (BindException e) {
          // Send 0 if the port is not available
          PublicServices.getOutputStream().write("0".getBytes());
          System.err.println(LocalTime.now() + " - " + e);
          continue;
        }

        // send 1 and close the socket
        PublicServices.getOutputStream().write("1".getBytes());
        PublicServices.getOutputStream().flush();
        PublicServices.getInputStream().read();
        PublicServices.close();
        break;
      }

      // Danger
      if (dangerous != 0 && randomDelay.nextInt(100) % 2 != 0) {
        System.err.println(causesOfDeath[randomDelay.nextInt(6)]);
        return;
      }
      // Keep waiting for proposals
      while (true) {
        try {
          Socket replySocket = acceptServerSocket.accept();
          // System.out.println(
          //   LocalTime.now() +
          //   " - Connection from a proposer received, reading message..."
          // );
          // danger
          if (dangerous != 0 && randomDelay.nextInt(100) % 2 != 0) {
            System.err.println(causesOfDeath[randomDelay.nextInt(6)]);
            return;
          }
          if (delay != -1) {
            // Identify type, 0 as prepare, 1 as proposal
            replySocket.getInputStream().read(byteBuffer1);
            int type = Integer.parseInt(
              new String(byteBuffer1, StandardCharsets.UTF_8)
            );
            // Identify ID, there always have an ID
            replySocket.getInputStream().read(byteBuffer4);
            int proposeId = Integer.parseInt(
              new String(byteBuffer4, StandardCharsets.UTF_8)
            );
            if (type == 1) {
              value = replySocket.getInputStream().read(byteBuffer4);
              Integer.parseInt(new String(byteBuffer4, StandardCharsets.UTF_8));
            }

            // Danger
            if (dangerous != 0 && randomDelay.nextInt(100) % 2 != 0) {
              System.err.println(causesOfDeath[randomDelay.nextInt(6)]);
              return;
            }

            // Give promise if the proposal is new
            if (proposeId >= promise) {
              promise = proposeId;
              if (type == 0) {
                // System.out.println(LocalTime.now() + " - Sending promise");
              } else {
                // System.out.println(LocalTime.now() + " - Value accepted");
              }
              TimeUnit.SECONDS.sleep(randomDelay.nextInt(delay));
              replySocket.getOutputStream().write("1".getBytes());
              replySocket.getOutputStream().flush();
              // Give refuse if the proposal is old
            } else {
              if (type == 0) {
                // System.out.println(LocalTime.now() + " - Proposal refused.");
              } else {
                // System.out.println(LocalTime.now() + " - Value refused.");
              }
              TimeUnit.SECONDS.sleep(randomDelay.nextInt(delay));
              replySocket.getOutputStream().write("0".getBytes());
              replySocket.getOutputStream().flush();
            }

            replySocket.close();
            // System.out.println("===== Message sent, socket closed =====\n");
            // System.out.println(
            //   LocalTime.now() + " - Listening for new proposes."
            // );
          }
        } catch (Exception e) {
          System.err.println(LocalTime.now() + " - " + e);
          continue;
        }
      }
    } catch (Exception e) {
      System.err.println(LocalTime.now() + " - " + e);
    }
  }
}
