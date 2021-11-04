/*
 * @Author: Yupeng Hou
 * @Id: a1783922
 * @Semester: 2
 * @Year: 2021
 * @Assignment Number: 3
 * @LastEditors: Yupeng Hou
 */

import java.io.IOException;
import java.net.BindException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

public class Operation_Propose extends Proposer implements Runnable {

  int portNum;

  public Operation_Propose(int portNum) {
    this.portNum = portNum;
  }

  public void run() {
    Socket server = null;
    while (!Thread.currentThread().isInterrupted()) {
      try {
        server = new Socket("localhost", portNum);
        // System.out.printf(
        //   LocalTime.now() + " - Connected to server %d, asking for promise.\n",
        //   portNum
        // );
        // first byte for operation type, 1 as propose
        server.getOutputStream().write("1".getBytes());
        // second byte for propose Id
        server.getOutputStream().write(String.format("%04d", id).getBytes());
        // third byte for value
        server.getOutputStream().write(String.format("%04d", value).getBytes());
        server.getOutputStream().flush();
        // Verify reply, 1 as accepted
        byte[] byteBuffer = new byte[1];
        server.getInputStream().read(byteBuffer);
        if (
          Integer.parseInt(new String(byteBuffer, StandardCharsets.UTF_8)) == 1
        ) {
          // System.out.printf(
          //   LocalTime.now() + " - %d has kept his/her promise.\n",
          //   portNum
          // );
          synchronized (this) {
            ++promiseKept;
          }
        } else {
          // System.out.printf(LocalTime.now() + " - %d is a traitor.\n", portNum);
        }
        server.close();
        return;
      } catch (BindException e) {
        // System.err.println(e);
        System.err.printf(
          LocalTime.now() + " - Port %d is not available.\n",
          portNum
        );
        return;
      } catch (IOException e) {
        System.err.printf(LocalTime.now() + " - " + e);
        return;
      }
    }
  }
}
