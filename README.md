<!--
 * @Author: Yupeng Hou
 * @Id: a1783922
 * @Semester: 3
 * @Year: 2021
 * @Assignment Number: 3
 * @LastEditors: Yupeng Hou
-->

TestOne:
` java PublicServices & acceporNum for (; acceptorNum < 500; ++acceptorNum){ java Acceptor & java Proposer acceptorNum }`

Presets:

- All proposer will ignore all other proposal.
- The public services must run on 8080
- The maximum amount of proposes is 9999.
- The acceptable port number is 8081 to 9999.

- Commands:
  - Use `javac PublicServices.java; java PublicServices` to compile start the public services
  - Open another terminal and use `javac Acceptor.java; java Acceptor $0 $1` to compile and start a acceptor or just `java Acceptor $0 $1` if the code is already compiled, all parameters are optional.
    - $0 represent the bound (exclusive) of delay of reply with unite of SECOND, all delay is randomly generated integer, the default bound is 4, input numbers override the maximum number of delay, -1 as no response but will accept first connection and do nothing
    - $1 represent the accidents, set it to 0 means no accidents at all server will work forever or caught some Exception, any other number will turn on random accidents, the server may die at any stages of it's life cycle. Default is 0. PS: there are three dead point which means that the server only have 12.5% chance to perform the whole process.
  - Open another terminal and use `javac Proposer.java; java Proposer $0 $1` to compile and start a acceptor or just `java Proposer $0 $1` if the code is already compiled
    - $0 represent the number of acceptors
    - $1 represent the value that proposer wants to put.

Test Cases(I cannot figure out how to open a new terminal by script while I cannot know what terminal will be used, I can only provide the test scenarios, please manually test according instructions):

1. Two server, default delay, no accidents, it shell able to reach the consensus.
   1. Start public service in one terminal
   2. Start one acceptor **with no parameters** in another terminal
   3. Start one proposer **with $0 = 1 $1 = 10** in another terminal
2. Three server, default delay, no accidents, it shell able to reach the consensus, test if proposer able to ask for two acceptors.
   1. Start public service in one terminal
   2. Start two acceptor **with no parameters** in separate terminals
   3. Start one proposer **with $0 = 2 $1 = 10** in another terminal
3. Five server, two acceptors have default delay, other two has $0 = -1 which is no reply, no accidents, it will be a infinite loop becaus there is no enough normal acceptors, test if proposer retry after time out.

   1. Start public service in one terminal
   2. Start two acceptors **with no parameters** in separate terminals
   3. Start two acceptors **with $0 = -1** in separate terminals
   4. Start one proposer **with $0 = 4 $1 = 10** in another terminal

4. Five server, two acceptors have default delay, another acceptor has $0 = -1 which is no reply, no accidents, two proposer with same $0 and different $1, test for change value in the middle, the value will be the new one.

   1. Start public service in one terminal
   2. Start two acceptors **with no parameters** in separate terminals
   3. Start one acceptor **with $0 = -1** in another terminal
   4. Start one proposer **with $0 = 3 $1 = 10** in another terminal
   5. Start one proposer right after the previous on started **with $0 = 3 $1 = 14** in another terminal

5. Case in Assignment3 web page
   1. Start public service in one terminal
   2. Start one acceptor **with no parameters** in separate terminal
   3. Start one acceptor **with $0 = 20** in another terminal
   4. Start one acceptor **with $0 = -1** in another terminal
   5. Start other 5 acceptor **with any combination of $0 and $1** in other terminals
   6. Start one proposer **with $0 = 8 $1 = 10** in another terminal
6. Improvising, any other conditions can be tested by changing the parameters of starting command.
