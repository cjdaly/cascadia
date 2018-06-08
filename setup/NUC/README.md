# Cascadia setup steps for Intel NUC

### reference links

* [Intel NUC7i3BNH specs](https://www.intel.com/content/www/us/en/products/boards-kits/nuc/kits/nuc7i3bnh.html)

### writing Ubuntu 16.04 image

Use a rPi, Rock64, or similar (Linux system with USB) to write Ubuntu ISO image to a USB drive.

* Do `ls /dev`, and then connect USB drive to rPi and:
  * Compare `ls /dev` now to determine which device is the USB drive.
  * On my rPi, the USB drive usually maps to `/dev/sda` (and sub-partitions like `/dev/sda1`, `/dev/sda2`, ...)
  * WARNING! Be careful not to overwrite stuff unintentionally! But do something like this to write image to USB:
    * `sudo dd bs=64K if=ubuntu-16.04.4-server-amd64.iso of=/dev/sdX ; sync`
      * _Be patient! ... this can take 5 minutes or more ..._
      * _The latest versions of `dd` have `status=progress` option._

When the USB drive with the Ubuntu image is ready, before applying power to the NUC, make sure all these are connected:

* USB drive with Ubuntu image
* USB keyboard
* USB mouse
* HDMI monitor (1920x1080 or similar)

When hard-booting the NUC, press `F2` to get into the BIOS to adjust settings as necessary.

Choose the first option in the Ubuntu installer: "Install Ubuntu Server". Choose mostly default (or locale-specific) settings with these exceptions:

* If trying to overwrite a previous install with a fresh one (as is typical), need to override defaults related to disk partitioning and similar.  Just blow the old stuff away and start fresh.
* In the "Software selection" section, add the `OpenSSH server` option (assuming this is needed to login externally via ssh).

At the end of the install, when prompted to reboot, pull the power and then remove the KVM and USB drive (leaving just ethernet connected).

### initial NUC system configuration

Boot the NUC and determine its IP address (from rPi on same subnet, try something like: `nmap -sP 192.168.1.1-254`). Then `ssh` login with the user/password established during the install process.

Clone the Cascadia repo, run the setup script and then reboot:

    git clone https://github.com/cjdaly/cascadia.git
    cd cascadia/setup/NUC
    sudo ./sudo-setup-NUC.sh
    sudo reboot

