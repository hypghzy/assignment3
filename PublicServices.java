/*
 * @Author: Yupeng Hou
 * @Id: a1783922
 * @Semester: 2
 * @Year: 2021
 * @Assignment Number: 3
 * @LastEditors: Yupeng Hou
 */
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.time.LocalTime;

public class PublicServices {

  static int proposeId = 1;
  static int numAcceptor = 0;
  static int port = 8080;
  static String portList = "";
  static boolean consensusReached = false;

  public static void main(String[] args) {
    // Every proposer using one incrementing value as their propose ID
    int value;
    ServerSocket proposeServer = null;
    try {
      proposeServer = new ServerSocket(port);
      System.out.printf(
        "=== Public service server on port %d started. ===\n",
        port
      );
      ++port;

      int fileNum = 0;
      String fileName = "TestResults_" + fileNum + ".txt";
      File results = null;
      Timestamp startTimestamp = null;
      Timestamp endTimestamp = null;

      results = new File(fileName);
      while (!results.createNewFile()) {
        ++fileNum;
        fileName = "TestResults_" + fileNum + ".txt";
        results = new File(fileName);
      }
      while (true) { // Keep waiting for connection until the program terminated
        String replyString = "1";
        // System.out.println(LocalTime.now() + " - Waiting for server...");
        Socket server = null;
        try {
          server = proposeServer.accept();

          // System.out.println(
          //   LocalTime.now() + " - Server connected, waiting for request..."
          // );

          while (true) {
            byte[] byteBuffer1 = new byte[1];
            server.getInputStream().read(byteBuffer1);
            int request = Integer.parseInt(
              new String(byteBuffer1, StandardCharsets.UTF_8)
            );
            // System.out.println(request);

            // Analyse request
            if (request == 0) {
              // System.out.println(
              //   LocalTime.now() +
              //   " - Request received, granting port " +
              //   port +
              //   " to proposer."
              // );
              // Send the number and wait for reply
              server
                .getOutputStream()
                .write(String.format("%04d", port).getBytes());
              server.getOutputStream().flush();
              ++port;
              ++numAcceptor;
            } else if (request == 1) {
              if (consensusReached) {
                // System.out.println(
                //   LocalTime.now() +
                //   " - Consensus reached, no more Id will be granted."
                // );
                // Send the number and wait for reply
                server.getOutputStream().write("0000".getBytes());
                server.getOutputStream().flush();
              } else {
                // System.out.println(
                //   LocalTime.now() +
                //   " - Request received, granting id " +
                //   proposeId +
                //   " to proposer."
                // );
                startTimestamp = new Timestamp(System.currentTimeMillis());

                // Send the number and wait for reply
                server
                  .getOutputStream()
                  .write(String.format("%04d", proposeId).getBytes());
                server.getOutputStream().flush();
                ++proposeId;
              }
            } else if (request == 2) {
              // System.out.println(
              //   LocalTime.now() +
              //   " - Request received, sending port list..." +
              //   portList
              // );

              server
                .getOutputStream()
                .write(Integer.toString(numAcceptor).getBytes());
              server.getOutputStream().write(portList.getBytes());
              server.getOutputStream().flush();
            } else if (request == 3) {
              if (consensusReached) {
                // System.out.println(
                //   LocalTime.now() +
                //   " - Consensus has already reached, this purpose is no longer valid."
                // );
                replyString = "0";
              } else {
                // System.out.println(
                //   LocalTime.now() +
                //   " - Consensus has been reached, reading value..."
                // );
                byte[] byteBuffer4 = new byte[4];
                server.getInputStream().read(byteBuffer4);
                value =
                  Integer.parseInt(
                    new String(byteBuffer4, StandardCharsets.UTF_8)
                  );
                System.out.println(
                  LocalTime.now() + " - The final value is " + value
                );
                endTimestamp = new Timestamp(System.currentTimeMillis());
                Files.writeString(
                  Paths.get(fileName),
                  Long.toString(
                    endTimestamp.getTime() - startTimestamp.getTime()
                  ),
                  StandardOpenOption.APPEND
                );
                Files.writeString(
                  Paths.get(fileName),
                  "\n",
                  StandardOpenOption.APPEND
                );
                // consensusReached = true;
              }
            }

            // The reply code
            int result;
            // Read anything as acknowledgement
            if (request != 3) {
              // System.out.println(
              //   LocalTime.now() + " - Waiting for confirm of receiving..."
              // );

              server.getInputStream().read(byteBuffer1);
              result =
                Integer.parseInt(
                  new String(byteBuffer1, StandardCharsets.UTF_8)
                );
            } else {
              result = 1;
            }

            if (result == 1) {
              if (request == 0) {
                portList += (port - 1 + " ");
              }
              // break the loop if the operation is succeeded.
              break;
            } else {
              if (request == 0) {
                --numAcceptor;
              }
            }
          }
          // Reply with 1 as acknowledgement
          server.getOutputStream().write(replyString.getBytes());
          server.getOutputStream().flush();

          // Close the socket no mater got what as reply
          // System.out.println(LocalTime.now() + " - Confirm received.");
          server.close();
          // System.out.println("======= Socket closed =======\n");
        } catch (SocketException e) {
          System.err.println(LocalTime.now() + " - " + e);
          if (server != null) {
            server.close();
          }
          continue;
        }
      }
    } catch (UnknownHostException e) {
      System.err.println(LocalTime.now() + " - " + e);
    } catch (IOException e) {
      System.err.println(LocalTime.now() + " - " + e);
    } finally {
      // System.out.println("====== Service ended. ======\n");
      try {
        if (proposeServer != null) {
          proposeServer.close();
        } else {
          // System.out.println(LocalTime.now() + " - Server not open.");
        }
      } catch (IOException e) {
        System.err.println(LocalTime.now() + " - " + e);
      }
    }
  }
}
