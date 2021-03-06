# Cascadia setup steps for RaspberryPi

### writing Raspbian image to microSD

* Use a rPi, Rock64, or similar (Linux system with USB) to write image to microSD.
* Download the latest [Raspbian](https://www.raspberrypi.org/downloads/raspbian/) 'Desktop' image and unzip it.
* Do `ls /dev`, and then connect USB/microSD adapter to rPi and:
  * Compare `ls /dev` now to determine which device is microSD.
  * On my rPi, the microSD usually maps to `/dev/sda` (and sub-partitions like `/dev/sda1`, `/dev/sda2`, ...)
  * WARNING! Be careful not to overwrite stuff unintentionally! But do something like this to write image:
    * `sudo dd bs=64K if=raspbian-stretch.img of=/dev/sdX status=progress ; sync`
* Before disconnecting the microSD, [enable ssh login](https://www.raspberrypi.org/blog/a-security-update-for-raspbian-pixel/) by creating an empty file named `ssh` in the `boot` directory (probably auto-mounted somewhere under `/media`).

### initial RaspberryPi system configuration

Boot the rPi and determine its IP address (from rPi on same subnet, try something like: `nmap -sP 192.168.1.1-254`).
Then `ssh` login with `user:pi` ; `pw:raspberry` and set a new password:

    sudo passwd pi

Next, run `sudo raspi-config` and:
* in Localization Options, set Timezone
* in Advanced Options, set Memory Split to 16 for GPU
* tweak other settings as desired

Now reboot:

    sudo reboot

... and reconnect, and clone the Cascadia repo, run the setup script and then reboot:

    git clone https://github.com/cjdaly/cascadia.git
    cd cascadia/setup/RaspberryPi
    sudo ./sudo-setup-rPi.sh
    sudo reboot
