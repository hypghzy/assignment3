/*
 * @Author: Yupeng Hou
 * @Id: a1783922
 * @Semester: 2
 * @Year: 2021
 * @Assignment Number: 3
 * @LastEditors: Yupeng Hou
 */

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Proposer {

  static int value, id, acceptorNum, majority, successfulConnection, promiseKept;
  static Map<Integer, Boolean> results = new HashMap<>();

  public static void main(String[] args) {
    byte[] byteBuffer4 = new byte[4];
    byte[] byteBuffer1 = new byte[1];
    value = 0;
    boolean consensusReached = false;

    // What until the time
    // LocalTime current = LocalTime.now();
    // // System.out.println(current);
    // LocalTime until = LocalTime.parse(
    //   args[1],
    //   DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    // );
    // long timeGap = ChronoUnit.MILLIS.between(current, until);
    // // System.out.println(timeGap);
    // try {
    //   TimeUnit.MILLISECONDS.sleep(timeGap);
    // } catch (Exception e) {
    //   System.err.println(e);
    // }

    acceptorNum = Integer.parseInt(args[0]);
    // acceptorNum = 4;
    majority = acceptorNum / 2 + 1;
    // if (args.length > 1) {
    //   value = Integer.parseInt(args[1]);
    // }
    // value = 10;
    // System.out.printf(
    //   LocalTime.now() + " - Total have %d acceptors the value is %d.\n",
    //   acceptorNum,
    //   value
    // );

    // request for purpose ID
    while (!consensusReached) {
      successfulConnection = 0;
      // Ask for propose id
      try {
        Socket publicServices = new Socket("localhost", 8080);
        System.out.println(
          LocalTime.now() + " - Asking for propose ID from public services..."
        );
        publicServices.getOutputStream().write("1".getBytes());
        publicServices.getOutputStream().flush();
        publicServices.getInputStream().read(byteBuffer4);
        id = Integer.parseInt(new String(byteBuffer4, StandardCharsets.UTF_8));
        if (id == 0) {
          consensusReached = true;
          System.out.println(
            LocalTime.now() + " - Consensus has reached, stop now."
          );
          continue;
        } else {
          System.out.printf(
            LocalTime.now() + " - Assigned propose Id is %d.\n",
            id
          );
        }
        publicServices.getOutputStream().write("1".getBytes());
        publicServices.getOutputStream().flush();
        publicServices.getInputStream().read();
        publicServices.close();
      } catch (Exception e) {
        System.err.println(LocalTime.now() + " - " + e);
      }

      // Prepare
      System.out.println("\n====== Preparing ======");
      try {
        // Acquire acceptors list
        Socket publicServices = new Socket("localhost", 8080);
        System.out.println(
          LocalTime.now() + " - Asking for ports of acceptors..."
        );
        publicServices.getOutputStream().write("2".getBytes());
        publicServices.getOutputStream().flush();
        publicServices.getInputStream().read(byteBuffer4);
        int acceptorListNum = Integer.parseInt(
          new String(byteBuffer4, StandardCharsets.UTF_8)
        );
        System.out.printf(
          LocalTime.now() + " - Total %d ports on the list.\n",
          acceptorListNum
        );

        // store the list and reply
        byte[] byteBufferS = new byte[acceptorListNum * 5];
        publicServices.getInputStream().read(byteBufferS);
        String portList = new String(byteBufferS, StandardCharsets.UTF_8);
        System.out.println(
          LocalTime.now() + " - Acceptors' ports: " + portList
        );
        publicServices.getOutputStream().write("1".getBytes());
        publicServices.getOutputStream().flush();
        publicServices.getInputStream().read();
        publicServices.close();

        // split list
        String[] ports = portList.split(" ");
        for (String port : ports) {
          results.put(Integer.valueOf(port), false);
        }
      } catch (Exception e) {
        System.err.println(LocalTime.now() + " - " + e);
      }

      ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
        10,
        10,
        10,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(500)
      );
      Iterator<Integer> portsIterator = results.keySet().iterator();

      while (successfulConnection < acceptorNum && portsIterator.hasNext()) {
        threadPoolExecutor.execute(new Operation_Prepare(portsIterator.next()));
        try {
          TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
          System.err.println(e);
        }
      }

      // Wait for 10 seconds maximum
      int received = 0;
      for (int i = 0; i < 20; i++) {
        Iterator<Boolean> replies = results.values().iterator();
        try {
          TimeUnit.MILLISECONDS.sleep(500);
          received = 0;
          while (replies.hasNext()) {
            if (replies.next()) {
              ++received;
            }
          }
          // Stop collecting promises if got enough of them
          if (received >= majority) {
            // System.out.println("Received majority promises.");
            break;
          }
        } catch (Exception e) {
          System.err.println(LocalTime.now() + " - " + e);
        }
      }
      threadPoolExecutor.shutdown();

      if (received < majority) {
        System.out.println(received + " promises collected.");
        System.out.println(
          LocalTime.now() +
          " - Not enough promises collected, try again now.\n====================================================================\n"
        );
        continue;
      }

      // Propose
      System.out.println("\n====== Proposing ======");

      ThreadPoolExecutor threadPoolExecutor2 = new ThreadPoolExecutor(
        10,
        10,
        10,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(50)
      );

      portsIterator = results.keySet().iterator();
      promiseKept = 0;
      // Send purpose to those who promised
      while (portsIterator.hasNext()) {
        Integer portUsing = portsIterator.next();
        if (results.get(portUsing)) {
          threadPoolExecutor2.execute(new Operation_Propose(portUsing));
          try {
            TimeUnit.SECONDS.sleep(1);
          } catch (Exception e) {
            System.err.println(e);
          }
        }
      }

      // Wait for 10 seconds maximum
      for (int i = 0; i < 20; i++) {
        try {
          TimeUnit.MILLISECONDS.sleep(500);
          // Stop collecting replies if got enough of them
          if (promiseKept >= majority) {
            consensusReached = true;
            // Tell public services the value is set
            threadPoolExecutor2.shutdown();
            Socket publicServices = new Socket("localhost", 8080);
            System.out.println(
              LocalTime.now() +
              " - Notifying the public services, which the consensus has been reached..."
            );
            publicServices.getOutputStream().write("3".getBytes());
            System.out.println(value);

            publicServices
              .getOutputStream()
              .write(String.format("%04d", value).getBytes());
            publicServices.getOutputStream().flush();

            publicServices.getInputStream().read(byteBuffer1);
            int result = Integer.parseInt(
              new String(byteBuffer1, StandardCharsets.UTF_8)
            );

            if (result == 1) {
              System.out.println(LocalTime.now() + " - Services completed.");
            } else {
              System.out.println(
                LocalTime.now() + " - Other proposal has been accepted."
              );
            }

            publicServices.getOutputStream().write("1".getBytes());
            publicServices.getOutputStream().flush();
            publicServices.close();
            break;
          }
        } catch (Exception e) {
          System.err.println(LocalTime.now() + " - " + e);
        }
      }

      if (!threadPoolExecutor2.isShutdown()) {
        threadPoolExecutor2.shutdown();
      }
      if (promiseKept < majority) {
        System.out.println(
          LocalTime.now() +
          " - Too many traitors, try again now.\n====================================================================\n"
        );
        continue;
      }
    }
    System.exit(0);
  }
}
