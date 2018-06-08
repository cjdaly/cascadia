# Cascadia setup steps for UpBoard

### writing Ubuntu 16.04 image to UpBoard eMMC

Refer to the UpBoard [Ubuntu wiki](https://wiki.up-community.org/Ubuntu) for any updates in the basic setup process.  Download the ISO image for the server install.  Latest tested image is the [16.04 LTS](http://releases.ubuntu.com/16.04/ubuntu-16.04.4-server-amd64.iso).

Use a rPi, Rock64, or similar (Linux system with USB) to write Ubuntu ISO image to a USB drive.

* Do `ls /dev`, and then connect USB drive to rPi and:
  * Compare `ls /dev` now to determine which device is the USB drive.
  * On my rPi, the USB drive usually maps to `/dev/sda` (and sub-partitions like `/dev/sda1`, `/dev/sda2`, ...)
  * WARNING! Be careful not to overwrite stuff unintentionally! But do something like this to write image to USB:
    * `sudo dd bs=64K if=ubuntu-16.04.4-server-amd64.iso of=/dev/sdX ; sync`
      * _Be patient! ... this can take 5 minutes or more ..._
      * _The latest versions of `dd` have `status=progress` option._

When the USB drive with the Ubuntu image is ready, before applying power to the Up board, make sure all these are connected:

* USB drive with Ubuntu image
* USB keyboard
* USB mouse
* HDMI monitor (1920x1080 or similar)

If another OS was previously installed and want to "re-pave" the machine, likely will need to change BIOS boot order to give USB priority (over eMMC).  When hard-booting the Up board and a logo screen is first visible, press `Esc` (or maybe any key) to get into the BIOS. There may be a password prompt. If no password has been set previously, just press `Enter`.  In the BIOS go to the `Boot` section to change the USB drive to top priority and then `F4` to save and restart.

Choose the first option in the Ubuntu installer: "Install Ubuntu Server". Choose mostly default (or locale-specific) settings with these exceptions:

* If trying to overwrite a previous install with a fresh one (as is typical), need to override defaults related to disk partitioning and similar.  Just blow the old stuff away and start fresh.
* In the "Software selection" section, add the `OpenSSH server` option (assuming this is needed to login externally via ssh).

At the end of the install, when prompted to reboot, pull the power and then remove the KVM and USB drive (leaving just ethernet connected).

### initial UpBoard system configuration

Boot the Up board and determine its IP address (from rPi on same subnet, try something like: `nmap -sP 192.168.1.1-254`). Then `ssh` login with the user/password established during the install process.

Clone the Cascadia repo, run the setup script and then reboot:

    git clone https://github.com/cjdaly/cascadia.git
    cd cascadia/setup
    sudo ./sudo-setup-UpBoard.sh
    sudo reboot

