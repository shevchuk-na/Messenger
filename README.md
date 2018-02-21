# LAN Messenger

Desktop Java application for text message communication in same-level LAN. Capable of both server and client functionality.
First launched client becomes server, other discover it by sending UDP packets to local broadcast address. If server application is 
terminated, oldest client becomes server, every client reconnects. 

Application is still in early production stages, many bugs possible.

### Installing

Build application with any IDE.
Testing is possible on same machine with one or several VMs installed. Enable VM bridge networking for clients to be in same LAN.
* [Oracle VirtualBox VM](https://www.virtualbox.org) - Requires OS image

## Built With

* [JavaFX](https://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm#JFXST784) - View
* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Nikita Shevchuk** - (https://github.com/shevchuk-na)
