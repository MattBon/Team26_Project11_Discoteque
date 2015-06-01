# Team26_Project11_Discoteque
*Android app able to estimate songs' BPM in real time and simultaneously tweak wireless lights according to BPM value.*

**System Requirements**

*Smartphone*
- Android v. 4.4.2 or v. 4.2.2.

*Emulator*
- Works for Windows and Linux (some issues occur while opening port 80 over Mac OS X - for further info please contact the author available on /Hue-Emulator-Master/README).
- Install Java Runtime Environment here: (https://www.java.com/it/download/)

**Steps to run Discoteque:**

In this section we describe how to build succesfully our project using Eclipse.
*Please remember that /Discoteque is the main project.*

1.  Import in Eclipse the theree projects: *Discoteque*, *BluetoothMidi* and *PdCore* via import -> existing android project.
2.  Now click on Android SDK manager, download and install the following API 19 packets: 
    - SDK Platform
    - ARMEABI v7a System Image
    - Sources for Android SDK
3.  Right click on Discoteque -> Properties -> Android -> reference and make sure that PdCore is linked as library. 
    Do the same with PdCore to make sure BluetoothMidi is linked to PdCore as library. PdCore and BluetoothMidi shall be checked to be marked as "isLibrary".
4.  Right Click on Discoteque -> Properties -> Java Build Path -> Add External Jars, then select both huelocalsdk.jar and huesdkresources.jar and import them (from /Discoteque).
5.  Click on Project -> Build all and Project -> Clean All.
6.  To install the .apk file on an android device, Click on Run Configurations -> Android -> Target, and mark "Always prompt to pick device"
7.  Click on the HueEmulator-v0.4 and choose port 80. Click on Start. 
8.  Run Discoteque. Follow the guided procedure to connect to the bridge and once it has been found wait untill it asks to press the bridge button. Click upon the emulator bridge.
9.  If a real Philips Bridge is available, follow the instructions on (http://www2.meethue.com/en-us/what-is-hue/get-started/) to connect the bridge to the network.
10. Have fun!

**Notes:**
- All the Java Doc documentation is stored in the Discoteque folder.
- Discoteque.apk is stored in /Discoteque/bin.
- Download and install the Android NDJ version r10d (if you wish to compile the pdbeatdetection.c library).

If you have questions, please contact Mattia Bonomi (bonomi.mattia@gmail.com) and Federico Zanetti (federicozanetti64@live.it).
