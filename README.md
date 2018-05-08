
# Cascadia

### writing Rock64 eMMC system image

* Check for new [latest](https://github.com/ayufan-rock64/linux-build/releases/latest) image file; current image: [Xenial mininal 5.15 (ayufan)](https://github.com/ayufan-rock64/linux-build/releases/download/0.5.15/xenial-minimal-rock64-0.5.15-136-arm64.img.xz)
* Use a rPi, Rock64, or similar (Linux system with USB) to write image to eMMC. Start by downloading and decompressing image:
  * `wget -O xenial-minimal.img.xz <IMAGE_URL>`
  * `xz -v -d xenial-minimal.img.xz`
* Use [eMMC adapter](https://ameridroid.com/products/emmc-adapter) with [microSD adapter](https://ameridroid.com/products/transcend-usb30-microsd-adapter) (or similar 1-piece setup like [this](https://www.pine64.org/?product=usb-adapter-for-emmc-module)) to connect eMMC module to USB for writing image.
* Do `ls /dev`, and then connect eMMC adapter to USB on rPi and:
  * Compare `ls /dev` now to determine which device is eMMC.
  * On my rPi, the eMMC usually maps to `/dev/sda` (and sub-partitions like `/dev/sda1`, `/dev/sda2`, ...)
  * WARNING! Be careful not to overwrite stuff unintentionally! But do something like this to write image to eMMC:
    * `sudo dd bs=64K if=xenial-minimal.img of=/dev/sdX ; sync`
      * _Be patient! ... this can take 5 minutes or more ..._
      * _The latest versions of `dd` have `status=progress` option._

### initial system configuration

Boot the Rock64 and determine its IP address (from rPi on same subnet, try something like: `nmap -sP 192.168.1.1-254`). Next ssh login with user:`rock64`, password:`rock64` and set a new password:

    sudo passwd rock64

And set the system time zone:

    sudo dpkg-reconfigure tzdata

Next, clone the Cascadia repo, run the setup script and then reboot:

    git clone https://github.com/cjdaly/cascadia.git
    cd cascadia/setup
    sudo ./sudo-setup-Rock64.sh
    sudo reboot
    
### typical usage

Cascadia will not start automatically after reboots. To start it:

    cd ~/cascadia/runtime
    ./cascadia.sh start

To monitor output/progress:

    ./cascadia.sh
    ./cascadia.sh status
    tail -f cascadia.log

To stop Cascadia:

    ./cascadia.sh stop

