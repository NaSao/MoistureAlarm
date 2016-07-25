Developer Manual
Arduino Part:
	1.Plug WIzFi250 with Arduino UNO, connect them to moisture sensor. Connect them though USB line with computer.
Connect moisture sensor with WizFi250 as the given interfaces: 
VOC - 5v;GND -GND;D0 - A0;A0 - A4
	2.Download arduino-1.6.9 IDE software https://www.arduino.cc/en/Main/Software
	3.Add WizFi250 library to Arduino IDE
	4.Create new code using withWiFi.ino
  5.Using your Ubidots TOKEN and Variable ID to replace the ones in code, replace the WLAN_SSID and WLAN_PASS with your own WIFI's information. 
  6.Upload the code and watch the data changes in Ubidots.
Android Part:
	1.Download Android Studio https://developer.android.com/studio/index.html
	2.Open locate project or 
      GitHub project https://github.com/NaSao/MoistureAlarm/tree/master/moisture
	3.Run it through a real android phone or using virtual machine.
 
