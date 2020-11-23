# LazyPocketBook Android
LazyPocketBook is an app that allows you to change pages remotely on your PocketBook e-reader.

# INSTALLATION GUIDE
To use LazyPocketBook you must jailbreak your PocketBook (enable admin access)

1. Download repository
* Extract repository files on your desktop
* Extract pb_jailbreak.zip
2. Connect PocketBook to PC
* Open your file explorer
* Turn on viewing hidden files (on Ubuntu it is Ctrl+h)
* Copy Jailbreak.app and Services.app to your PocketBook Applications folder.
3. Then from your PocketBook menu
* Launch @Jailbreak. If it succeeds, it will install root su. It does nothing else. Running it again will undo root.
* Launch @Services. This will install the system services, kernel modules, settings menus etc. su must be installed. If jailbreak is missing, the app will silently fail to run. Running it again will undo the install.

Once the device boots after Services install, new menu entry 'Rooted device settings' should appear in settings menu. In it, it will show generated root password, you can change it to your own too.

4. Connect your PC and PocketBook to the same WiFi network.
5. Once the device boots after Services install, new menu entry 'Rooted device settings' should appear in settings menu. In it, it will show generated root password. Remember the password.
6. Using terminal connect via ssh to your PocketBook
* `ssh root@169.254.0.1`
* When it ask you for password enter it
* Now in your ssh terminal type
* `cat /dev/input/event0 > f.txt`
* press forward button on your PocketBook
* then press CTRL+C on PC
* now do the same thing for backward, home and options
* `cat /dev/input/event0 > b.txt`
* press backward button on your PocketBook
* then press CTRL+C on PC
7. Now exit the SSH with CTRL+D
8. Download and install .apk file on your mobile
9. Use LazyPocketBook


# Remember
* Your Smartphone and PocketBook must be connected to the same network.
* Basic login is 169.254.0.1 and password is 1257
* To chech you login, go to settings on your PB, then Rooted device settings, then press System status if IP is blank, it means it is set to default (169.254.0.1)

