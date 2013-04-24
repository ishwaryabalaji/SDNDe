SDNDe
=====

SDN debugger - is a network debugging tool similar to linux gdb for debugging software defined networks. 


Architecture / Technologies - Quick View
========================================

<SDN Client>  --- web socket --- <SDN Server Debugger Thread>   ---  <Open Flow Controller>   --- <Mininet>
  |                Cmds / Op             |                Cmds/Op              |
 HTML                               JavaEE Server                     Beacon Controller
 JQUERY                             JwebSocket                        MongoDB
 JWebSocket
 
 
 
NOTES 
======
 
 Beacon (Java-based OpenFlow controller) is used which communicates with all the switches on the network
 Debuuger Thread receives the inputs from client & interacts with the Beacon Controller
 
 Features in actions -- prototype version
 Forward Trace / Backward Trace | Breakpoint | Monitor | Step Continue .. 
 
 
Demo Video 
==========
Part 1 - http://youtu.be/5635I5wE2-4

Part 2 - http://youtu.be/_Ov1WhepbB8
