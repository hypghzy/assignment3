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

public class Operation_Prepare extends Proposer implements Runnable {

  int portNum;

  public Operation_Prepare(int portNum) {
    this.portNum = portNum;
  }

  public void run() {
    Socket server = null;
    while (!Thread.currentThread().isInterrupted()) {
      try {
        server = new Socket("localhost", portNum);
        synchronized (this) {
          ++successfulConnection;
        }
        // System.out.printf(
        //   LocalTime.now() + " - Connected to server %d, asking for promise.\n",
        //   portNum
        // );
        // first byte for operation type, 0 as prepare
        server.getOutputStream().write("0".getBytes());
        // second byte for propose Id
        server.getOutputStream().write(String.format("%04d", id).getBytes());
        server.getOutputStream().flush();
        // Verify reply, 1 as accepted
        byte[] byteBuffer = new byte[1];
        server.getInputStream().read(byteBuffer);
        if (
          Integer.parseInt(new String(byteBuffer, StandardCharsets.UTF_8)) == 1
        ) {
          // System.out.printf(
          //   LocalTime.now() + " - Got promise from %d.\n",
          //   portNum
          // );
          synchronized (this) {
            results.put(portNum, true);
          }
        } else {
          // System.out.printf(LocalTime.now() + " - Refused by %d.\n", portNum);
        }
        server.close();
        return;
      } catch (Exception e) {
        // System.err.println(e);
        System.err.printf(
          LocalTime.now() + " - Port %d is not available.\n",
          portNum
        );
        return;
      }
    }
  }
}
